package com.aliware.tianchi.core;

import com.aliware.tianchi.metric.InvokerMetric;
import com.aliware.tianchi.metric.LeapWindowInvokerMetricImpl;
import com.aliware.tianchi.model.PerformanceIndicator;
import com.aliware.tianchi.model.Tuple;
import com.aliware.tianchi.util.DateTimeUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invoker;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description
 */
public class RealTimeDynamicInvokerWeight implements DynamicInvokerWeight {
    private static final Logger logger = LoggerFactory.getLogger(RealTimeDynamicInvokerWeight.class);

    private InvokerMetric metric = LeapWindowInvokerMetricImpl.getInstance();

    private final Map<Tuple<String, Integer>, Integer> invokerThreadCounts = new ConcurrentHashMap();

    private final Map<Tuple<String, Integer>, Integer> invokerCpuCores = new ConcurrentHashMap();

    private final Map<Tuple<String, Integer>, PerformanceIndicator> invokerPerformances = new ConcurrentHashMap();

    private Timer timer = new Timer();

    protected RealTimeDynamicInvokerWeight() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!invokerThreadCounts.isEmpty()) {
                    for (Map.Entry<Tuple<String, Integer>, Integer> entry : invokerThreadCounts.entrySet()) {
                        String host = entry.getKey().getItem1();
                        Integer port = entry.getKey().getItem2();

                        PerformanceIndicator performanceIndicator = metric.getPerformanceIndicator(host, port);
                        if (performanceIndicator != null) {
                            invokerPerformances.put(entry.getKey(), performanceIndicator);
                        }
                    }
                }
            }
        }, 0, 50);
    }

    @Override
    public Integer getWeight(Invoker invoker) {
        Tuple<String, Integer> key = getKey(invoker);
        Integer threadCount = invokerThreadCounts.get(key);
        if (threadCount == null) {
            return null;
        }

//        Integer cpuCore = invokerCpuCores.get(key);
//        if (cpuCore == null) {
//            return null;
//        }

        Date now = new Date();

        PerformanceIndicator performanceIndicator;
        if (true) {
            URL url = invoker.getUrl();
            String host = url.getHost();
            Integer port = url.getPort();
            performanceIndicator = invokerPerformances.get(new Tuple<>(host, port));
        } else {
            performanceIndicator = metric.getPerformanceIndicator(invoker);
        }

        if (performanceIndicator == null) {
            return null;
        }
        int freeThreadCount = (int) (threadCount - performanceIndicator.getUsedThreadCount());
        long avgCostTime = performanceIndicator.getAvgCostTime();
        int weight = (int) (freeThreadCount * 100 / avgCostTime);

        if (ThreadLocalRandom.current().nextInt(5000) >= 4999) {
            executor.execute(() -> {
                logger.info(String.format("<get_weight> current time:%s,key:%s,freeThreadCount:%s,avg_time:%s,weight:%s", DateTimeUtils.formatDateTime(now), key, freeThreadCount, avgCostTime, weight));
            });
        }

        return weight;
    }

    private static final Executor executor = Executors.newSingleThreadExecutor();


    public void setThreadCount(String host, Integer port, Integer thread) {
        Tuple<String, Integer> key = new Tuple<>(host, port);

        this.invokerThreadCounts.put(key, thread);
    }

    public void setCpuCore(String host, Integer port, Integer cpuCore) {
        Tuple<String, Integer> key = new Tuple<>(host, port);

        this.invokerCpuCores.put(key, cpuCore);
    }

    private Tuple<String, Integer> getKey(Invoker invoker) {
        URL url = invoker.getUrl();
        String host = url.getHost();
        Integer port = url.getPort();
        return new Tuple<>(host, port);
    }

    public static RealTimeDynamicInvokerWeight getInstance() {
        return Inner.DYNAMIC_INVOKER_WEIGHT;
    }


    protected static final class Inner {
        private static final RealTimeDynamicInvokerWeight DYNAMIC_INVOKER_WEIGHT = new RealTimeDynamicInvokerWeight();

    }

}