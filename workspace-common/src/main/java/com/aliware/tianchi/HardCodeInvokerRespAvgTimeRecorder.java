package com.aliware.tianchi;

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
        }

        respTimeArray = invokerTotalRespTimeStorage.get(tuple);
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

        if (array[offset] == null) {
            LongAdder[] respTimeArray = invokerTotalRespTimeStorage.get(tuple);
            LongAdder[] countArray = invokerTotalCountStorage.get(tuple);

            long totalRespTime = respTimeArray[offset].sum();
            long totalCount = countArray[offset].sum();

            if (totalRespTime == 0 || totalCount == 0){
                System.out.println("current avg time, tuple:" + tuple + ", current_time:" + time + ",avg:" + 0);
                return null;
            }

            if (array[offset] == null) {
                array[offset] = totalRespTime / totalCount;
                System.out.println("current avg time, tuple:" + tuple + ", current_time:" + time + ",avg:" + array[offset]);
            }
        }

        return array[offset];
    }
}
