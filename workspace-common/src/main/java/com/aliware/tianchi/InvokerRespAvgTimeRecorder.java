package com.aliware.tianchi;

import org.apache.dubbo.rpc.Invoker;

import java.util.Date;

/**
 * @author Fan Huaran
 * created on 2019/7/17
 * @description
 */
public interface InvokerRespAvgTimeRecorder {
    void recordCostTime(Invoker invoker, Date startTime, long respTime);

    Long getAvgCostTime(Invoker invoker, Date time, int beforeSeconds);
}
