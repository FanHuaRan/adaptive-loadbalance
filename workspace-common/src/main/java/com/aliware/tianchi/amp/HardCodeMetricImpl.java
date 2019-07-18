package com.aliware.tianchi.amp;

import com.aliware.tianchi.extension.Tuple;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description
 */
public class HardCodeMetricImpl implements Metric {

    private static final int STORAGE_LENGTH = 1200;

    private long initHalfSecondTime = new Date().getTime() / 500;

    private Map<Tuple<String, Integer>, AtomicReferenceArray<LongAdder>> invokerTotalRespTimeStorage = new ConcurrentHashMap<>(16, 0.5F);

    private Map<Tuple<String, Integer>, AtomicReferenceArray<LongAdder>> invokerTotalCountStorage = new ConcurrentHashMap<>();

    private Map<Tuple<String, Integer>, LongAdder> invokerUsedThreadCountStorage = new ConcurrentHashMap<>();

    private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    @Override
    public void invokeStart(Invoker invoker) {
        executor.execute(() -> {
            Tuple<String, Integer> key = getKey(invoker);

            LongAdder threadCounter = invokerUsedThreadCountStorage.get(key);

            if (threadCounter == null) {
                doInit(key);
                threadCounter = invokerUsedThreadCountStorage.get(key);
            }

            threadCounter.increment();
        });
    }


    private ReentrantLock lock = new ReentrantLock();


    @Override
    public void invokeEnd(Invoker invoker, Date startTime, long costTime) {
        executor.execute(() -> {
            Tuple<String, Integer> key = getKey(invoker);
            LongAdder threadCounter = invokerUsedThreadCountStorage.get(key);
            if (threadCounter == null) {
                doInit(key);
                threadCounter = invokerUsedThreadCountStorage.get(key);
            }
            threadCounter.decrement();

            int offset = (int) (startTime.getTime() / 500 - initHalfSecondTime);
            AtomicReferenceArray<LongAdder> totalTimeArray = invokerTotalRespTimeStorage.get(key);

            AtomicReferenceArray<LongAdder> totalCountArray = invokerTotalCountStorage.get(key);
            totalTimeArray.get(offset).add(costTime);
            totalCountArray.get(offset).increment();
        });
    }

    @Override
    public PerformanceIndicator getPerformanceIndicator(Invoker invoker, Date reqTime, int beforeWindow) {
        int offset = (int) (reqTime.getTime() / 500 - initHalfSecondTime) - beforeWindow;
        if (offset < 0) {
            return null;
        }

        Tuple<String, Integer> key = getKey(invoker);

        AtomicReferenceArray<LongAdder> totalTimeArray = invokerTotalRespTimeStorage.get(key);
        if (totalTimeArray == null) {
            return null;
        }

        AtomicReferenceArray<LongAdder> totalCountArray = invokerTotalCountStorage.get(key);
        if (totalCountArray == null) {
            return null;
        }

        LongAdder usedThreadCounter = invokerUsedThreadCountStorage.get(key);
        if (usedThreadCounter == null) {
            return null;
        }

        long totalCostTime = totalTimeArray.get(offset).sum();
        long totalCount = totalCountArray.get(offset).sum();
        if (totalCostTime == 0 || totalCount == 0) {
            return null;
        }

        long usedCount = usedThreadCounter.sum();

        return new PerformanceIndicator(totalCostTime, totalCount, totalCostTime / totalCount, usedCount);
    }


    private void doInit(Tuple<String, Integer> key) {
        lock.lock();
        try {
            AtomicReferenceArray<LongAdder> totalTimeArray = invokerTotalRespTimeStorage.get(key);
            if (totalTimeArray == null) {
                System.out.println("init totalTimeArray,key:" + key);
                totalTimeArray = new AtomicReferenceArray<>(getLongAdderArray());
                invokerTotalRespTimeStorage.putIfAbsent(key, totalTimeArray);
            }

            AtomicReferenceArray<LongAdder> totalCountArray = invokerTotalCountStorage.get(key);
            if (totalCountArray == null) {
                System.out.println("init totalCountArray,key:" + key);
                totalCountArray = new AtomicReferenceArray<>(getLongAdderArray());
                invokerTotalCountStorage.putIfAbsent(key, totalCountArray);
            }

            LongAdder usedThreadCounter = invokerUsedThreadCountStorage.get(key);
            if (usedThreadCounter == null) {
                System.out.println("init usedThreadCounter,key:" + key);
                usedThreadCounter = new LongAdder();
                invokerUsedThreadCountStorage.putIfAbsent(key, usedThreadCounter);
            }

        } finally {
            lock.unlock();
        }
    }

    private LongAdder[] getLongAdderArray() {
        LongAdder[] array = new LongAdder[STORAGE_LENGTH];
        for (int i = 0; i < STORAGE_LENGTH; i++) {
            array[i] = new LongAdder();
        }
        return array;
    }

    private Tuple<String, Integer> getKey(Invoker invoker) {
        URL url = invoker.getUrl();
        String host = url.getHost();
        Integer port = url.getPort();
        return new Tuple<>(host, port);
    }

    public static HardCodeMetricImpl getInstance(){
        return Inner.instance;
    }

    private HardCodeMetricImpl(){

    }

    private static final class Inner {
        private static final HardCodeMetricImpl instance = new HardCodeMetricImpl();

    }
}
