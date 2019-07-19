package com.aliware.tianchi.metric;

import com.aliware.tianchi.model.PerformanceIndicator;
import org.apache.dubbo.rpc.Invoker;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description
 */
public interface InvokerMetric {

    void invokeStart(Invoker invoker);

    void invokeEnd(Invoker invoker, long costTime);

    PerformanceIndicator getPerformanceIndicator(Invoker invoker);

    PerformanceIndicator getPerformanceIndicator(String host, Integer port);
}
