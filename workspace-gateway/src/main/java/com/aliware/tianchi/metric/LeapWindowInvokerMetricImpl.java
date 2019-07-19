package com.aliware.tianchi.metric;

import com.aliware.tianchi.model.PerformanceIndicator;
import com.aliware.tianchi.model.Tuple;
import com.aliware.tianchi.statistics.window.LeapWindowMetric;
import com.aliware.tianchi.statistics.window.WindowPerformance;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Fan Huaran
 * created on 2019/7/19
 * @description
 */
public class LeapWindowInvokerMetricImpl implements InvokerMetric {

    private static final int WINDOW_LENGTH = 500;

    private static final int INTERNAL_IN_SEC = 1;

    private Map<Tuple<String, Integer>, LeapWindowMetric> invokerLeapWindowMetricStorage = new ConcurrentHashMap<>(16, 0.5F);

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
    public void invokeEnd(Invoker invoker, long costTime) {
        executor.execute(() -> {

            Tuple<String, Integer> key = getKey(invoker);
            LongAdder threadCounter = invokerUsedThreadCountStorage.get(key);
            if (threadCounter == null) {
                doInit(key);
                threadCounter = invokerUsedThreadCountStorage.get(key);
            }
            threadCounter.decrement();

            LeapWindowMetric leapWindowMetric = invokerLeapWindowMetricStorage.get(key);
            leapWindowMetric.addPass(costTime);
        });
    }

    @Override
    public PerformanceIndicator getPerformanceIndicator(Invoker invoker) {
        Tuple<String, Integer> key = getKey(invoker);

        LeapWindowMetric leapWindowMetric = invokerLeapWindowMetricStorage.get(key);
        if (leapWindowMetric == null) {
            return null;
        }

        WindowPerformance windowPerformance = leapWindowMetric.getPreviousWindowPerformance();
        if (windowPerformance == null) {
            return null;
        }

        LongAdder usedThreadCounter = invokerUsedThreadCountStorage.get(key);
        if (usedThreadCounter == null) {
            return null;
        }

        long totalCount = windowPerformance.getTotalCount();
        long totalCostTime = windowPerformance.getTotalCostTime();
        if (totalCount == 0 || totalCostTime == 0) {
            return null;
        }

        long usedCount = usedThreadCounter.sum();

        return new PerformanceIndicator(totalCostTime, totalCount, totalCostTime / totalCount, usedCount);
    }


    private void doInit(Tuple<String, Integer> key) {
        lock.lock();
        try {
            LeapWindowMetric leapWindowMetric = invokerLeapWindowMetricStorage.get(key);
            if (leapWindowMetric == null) {
                System.out.println("init leapWindowMetric,key:" + key);
                leapWindowMetric = new LeapWindowMetric(WINDOW_LENGTH, INTERNAL_IN_SEC);
                invokerLeapWindowMetricStorage.putIfAbsent(key, leapWindowMetric);
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

    private Tuple<String, Integer> getKey(Invoker invoker) {
        URL url = invoker.getUrl();
        String host = url.getHost();
        Integer port = url.getPort();
        return new Tuple<>(host, port);
    }

    public static LeapWindowInvokerMetricImpl getInstance() {
        return Inner.instance;
    }

    protected LeapWindowInvokerMetricImpl() {

    }

    protected static final class Inner {
        private static final LeapWindowInvokerMetricImpl instance = new LeapWindowInvokerMetricImpl();

    }

}
