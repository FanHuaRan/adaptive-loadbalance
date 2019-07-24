package com.aliware.tianchi.metric;

import com.aliware.tianchi.model.PerformanceIndicator;
import com.aliware.tianchi.model.Tuple;
import com.aliware.tianchi.statistics.LeapWindowMetric;
import com.aliware.tianchi.statistics.RelativeLeapWindowMetric;
import com.aliware.tianchi.statistics.WindowPerformance;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
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
public class RelativeLeapWindowInvokerMetricImpl implements InvokerMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(RelativeLeapWindowInvokerMetricImpl.class);

    private static final int WINDOW_LENGTH = 200;

    private Map<Tuple<String, Integer>, RelativeLeapWindowMetric> invokerLeapWindowMetricStorage = new ConcurrentHashMap<>(16, 0.5F);

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
            if (costTime != -1) {
                RelativeLeapWindowMetric relativeLeapWindowMetric = invokerLeapWindowMetricStorage.get(key);
                relativeLeapWindowMetric.addRtt(costTime);
            }
        });
    }

    @Override
    public PerformanceIndicator getPerformanceIndicator(Invoker invoker) {
        return doGetPerformanceIndicator(getKey(invoker));
    }

    @Override
    public PerformanceIndicator getPerformanceIndicator(String host, Integer port) {
        Tuple<String, Integer> key = new Tuple<>(host, port);

        return doGetPerformanceIndicator(key);
    }

    private PerformanceIndicator doGetPerformanceIndicator(Tuple<String, Integer> key) {
        RelativeLeapWindowMetric relativeLeapWindowMetric = invokerLeapWindowMetricStorage.get(key);
        if (relativeLeapWindowMetric == null) {
            return null;
        }

        long avg = relativeLeapWindowMetric.getAvg();
        if (avg == 0) {
            return null;
        }

        LongAdder usedThreadCounter = invokerUsedThreadCountStorage.get(key);
        if (usedThreadCounter == null) {
            return null;
        }
        long usedCount = usedThreadCounter.sum();

        return new PerformanceIndicator(-1L, -1L, avg, usedCount);
    }


    private void doInit(Tuple<String, Integer> key) {
        lock.lock();
        try {
            RelativeLeapWindowMetric relativeLeapWindowMetric = invokerLeapWindowMetricStorage.get(key);
            if (relativeLeapWindowMetric == null) {
                LOGGER.info("<InvokerMetric> init relativeLeapWindowMetric,key:" + key);
                relativeLeapWindowMetric = new RelativeLeapWindowMetric(WINDOW_LENGTH);
                invokerLeapWindowMetricStorage.putIfAbsent(key, relativeLeapWindowMetric);
            }

            LongAdder usedThreadCounter = invokerUsedThreadCountStorage.get(key);
            if (usedThreadCounter == null) {
                LOGGER.info("<InvokerMetric> init usedThreadCounter,key:" + key);
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

    public static RelativeLeapWindowInvokerMetricImpl getInstance() {
        return Inner.instance;
    }

    protected RelativeLeapWindowInvokerMetricImpl() {

    }

    protected static final class Inner {
        private static final RelativeLeapWindowInvokerMetricImpl instance = new RelativeLeapWindowInvokerMetricImpl();

    }

}
