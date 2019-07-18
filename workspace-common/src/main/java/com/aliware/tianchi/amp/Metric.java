package com.aliware.tianchi.amp;

import org.apache.dubbo.rpc.Invoker;

import java.util.Date;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description
 */
public interface Metric {

    void invokeStart(Invoker invoker);


    void invokeEnd(Invoker invoker, Date startTime, long costTime);

    PerformanceIndicator getPerformanceIndicator(Invoker invoker, Date reqTime, int beforeWindow);
}
