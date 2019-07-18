package com.aliware.tianchi.core;

import com.aliware.tianchi.amp.HardCodeMetricImpl;
import com.aliware.tianchi.amp.Metric;
import com.aliware.tianchi.amp.PerformanceIndicator;
import com.aliware.tianchi.extension.Tuple;
import com.aliware.tianchi.util.DateTimeUtils;
import javassist.runtime.Inner;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;

import java.util.Date;
import java.util.Map;
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
    private Metric metric = HardCodeMetricImpl.getInstance();
    private final Map<Tuple<String, Integer>, Integer> invokerThreadCounts = new ConcurrentHashMap();


    @Override
    public Integer getWeight(Invoker invoker) {
        Tuple<String, Integer> key = getKey(invoker);
        Integer threadCount = invokerThreadCounts.get(key);
        if (threadCount == null) {
            return null;
        }

        Date now = new Date();
        PerformanceIndicator performanceIndicator = metric.getPerformanceIndicator(invoker, now, 1);
        if (performanceIndicator == null) {
            return null;
        }
        int freeThreadCount = (int) (threadCount - performanceIndicator.getUsedThreadCount());
        long avgCostTime = performanceIndicator.getAvgCostTime();
        int weight = (int) (freeThreadCount * 100/ avgCostTime);

        if(ThreadLocalRandom.current().nextInt(5000) >= 4999) {
            executor.execute(() -> {
                System.out.println("current time:" + DateTimeUtils.formatDateTime(new Date()) + ",key:" + key + ",freeThreadCount:" + freeThreadCount + ",avg_time:" + avgCostTime + ",weight:" + weight);
//                System.out.flush();
            });
        }
        return weight;
    }

    private static final Executor executor = Executors.newSingleThreadExecutor();



    public void setThreadCount(String host, Integer port, Integer weight) {
        Tuple<String, Integer> key = new Tuple<>(host, port);

        this.invokerThreadCounts.put(key, weight);
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


    private RealTimeDynamicInvokerWeight() {

    }

    private static final class Inner {
        private static final RealTimeDynamicInvokerWeight DYNAMIC_INVOKER_WEIGHT = new RealTimeDynamicInvokerWeight();

    }

}