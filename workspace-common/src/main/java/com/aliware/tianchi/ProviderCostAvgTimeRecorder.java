package com.aliware.tianchi;

import java.util.Date;

/**
 * @author Fan Huaran
 * created on 2019/7/17
 * @description
 */
public interface ProviderCostAvgTimeRecorder {
    void recordCostTime(Date startTime, long respTime);

    Long getAvgCostTime(Date time, int beforeSeconds);
}
