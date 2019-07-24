package com.aliware.tianchi.core;

import com.aliware.tianchi.metric.InvokerMetric;
import com.aliware.tianchi.metric.LeapWindowInvokerMetricImpl;
import com.aliware.tianchi.metric.RelativeLeapWindowInvokerMetricImpl;
import com.aliware.tianchi.model.PerformanceIndicator;
import com.aliware.tianchi.model.Tuple;
import com.aliware.tianchi.statistics.RelativeLeapWindowMetric;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description 动态权重源实现，策略是：权重= 剩余线程数/平均响应时间
 * 平均响应时间可以是以下两种：
 * 1.时间滑动窗口，前500s或者当前500s响应时间，QPS可到125W
 * 2.调用滑动窗口，前200次调用平均响应时间，完全滑动, QPS可到127W
 */
public class RealTimeDynamicInvokerWeight implements DynamicInvokerWeight {
    private static final Logger logger = LoggerFactory.getLogger(RealTimeDynamicInvokerWeight.class);
    //    private InvokerMetric metric = LeapWindowInvokerMetricImpl.getInstance();
    private InvokerMetric metric = RelativeLeapWindowInvokerMetricImpl.getInstance();

    private final Map<Tuple<String, Integer>, Integer> invokerThreadCounts = new ConcurrentHashMap();

    private final Map<Tuple<String, Integer>, Integer> invokerCpuCores = new ConcurrentHashMap();

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

        PerformanceIndicator performanceIndicator = metric.getPerformanceIndicator(invoker);

        if (performanceIndicator == null) {
            return null;
        }
        int freeThreadCount = (int) (threadCount - performanceIndicator.getUsedThreadCount());
        long avgCostTime = performanceIndicator.getAvgCostTime();
        int weight = (int) (freeThreadCount * 100 / avgCostTime);

        return weight;
    }

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
