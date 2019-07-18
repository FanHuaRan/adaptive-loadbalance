package com.aliware.tianchi.amp.impl;

import com.aliware.tianchi.amp.InvokerRespAvgTimeRecorder;
import com.aliware.tianchi.extension.Tuple;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Fan Huaran
 * created on 2019/7/17
 * @description
 */
public class HardCodeInvokerRespAvgTimeRecorder implements InvokerRespAvgTimeRecorder {

    private static final int STORAGE_LENGTH = 600;

    private long initSecondTime = new Date().getTime() / 1000;

    private Map<Tuple<String, Integer>, LongAdder[]> invokerTotalRespTimeStorage = new ConcurrentHashMap<>(16, 0.5F);

    private Map<Tuple<String, Integer>, LongAdder[]> invokerTotalCountStorage = new ConcurrentHashMap<>();

    private Map<Tuple<String, Integer>, Long[]> invokerAvgRespTimeStorage = new ConcurrentHashMap<>();

//    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

//    {
//        Date currentTime = new Date();
//        int millionPart = (int) (currentTime.getTime() % 1000);
//        int delay = (1000 - millionPart + 10) % 1000;
//
//        System.out.println("start config schedule executor, currentTime:" + DateTimeUtils.formatDateTime(currentTime) + ", delay:" + delay);
//
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            Date now = new Date();
//            int offset = (int) (now.getTime() / 1000 - initSecondTime) - 1;
//            System.out.println("syn avg time start, now:" + DateTimeUtils.formatDateTime(now) + ", offset:" + offset);
//
//            Set<Tuple<String, Integer>> keySet = invokerAvgRespTimeStorage.keySet();
//
//            for (Tuple<String, Integer> key : keySet) {
//                LongAdder[] respTimeArray = invokerTotalRespTimeStorage.get(key);
//                LongAdder[] countArray = invokerTotalCountStorage.get(key);
//                Long[] array = invokerAvgRespTimeStorage.get(key);
//
//                if (respTimeArray == null || countArray == null || array == null) {
//                    continue;
//                }
//
//                long totalRespTime = respTimeArray[offset].sum();
//                long totalCount = countArray[offset].sum();
//
//                if (totalRespTime == 0 || totalCount == 0) {
//                    System.out.println("current avg time, server:" + key + ",offset:" + offset + " current_time:" + DateTimeUtils.formatDateTime(now) + ",avg:" + null);
//                    continue;
//                }
//
//                array[offset] = totalRespTime / totalCount;
//                System.out.println("current avg time, server:" + key + ",offset:" + offset + " current_time:" + DateTimeUtils.formatDateTime(now) + ",avg:" + array[offset]);
//            }
//
//        }, delay, 1000, TimeUnit.MILLISECONDS);
//    }


    private void init(Tuple<String, Integer> tuple) {
        LongAdder[] array = new LongAdder[STORAGE_LENGTH];
        for (int i = 0; i < STORAGE_LENGTH; i++) {
            array[i] = new LongAdder();
        }
        invokerTotalRespTimeStorage.putIfAbsent(tuple, array);

        array = new LongAdder[STORAGE_LENGTH];
        for (int i = 0; i < STORAGE_LENGTH; i++) {
            array[i] = new LongAdder();
        }
        invokerTotalCountStorage.putIfAbsent(tuple, array);

        invokerAvgRespTimeStorage.putIfAbsent(tuple, new Long[STORAGE_LENGTH]);
    }

    @Override
    public void recordCostTime(Invoker invoker, Date startTime, long respTime) {
        URL url = invoker.getUrl();
        String host = url.getHost();
        Integer port = url.getPort();
        Tuple<String, Integer> tuple = new Tuple<>(host, port);

        LongAdder[] respTimeArray = invokerTotalRespTimeStorage.get(tuple);
        if (respTimeArray == null) {
            init(tuple);
            respTimeArray = invokerTotalRespTimeStorage.get(tuple);
        }

        LongAdder[] countArray = invokerTotalCountStorage.get(tuple);

        int offset = (int) (startTime.getTime() / 1000 - initSecondTime);

        respTimeArray[offset].add(respTime);
        countArray[offset].increment();
    }

    @Override
    public Long getAvgCostTime(Invoker invoker, Date time, int beforeSeconds) {
        int offset = (int) (time.getTime() / 1000 - initSecondTime) - beforeSeconds;
        if (offset < 0) {
            return null;
        }

        URL url = invoker.getUrl();
        String host = url.getHost();
        Integer port = url.getPort();
        Tuple<String, Integer> tuple = new Tuple<>(host, port);

        Long[] array = invokerAvgRespTimeStorage.get(tuple);
        if (array == null) {
            return null;
        }

        return array[offset];
    }
}
