package com.aliware.tianchi.recorder;

import com.aliware.tianchi.model.PerformanceIndicator;

import java.util.Date;

/**
 * @author Fan Huaran
 * created on 2019/7/17
 * @description
 */
@Deprecated
public interface ProviderCostAvgTimeRecorder {
    void recordStart();

    void recordCostTime(Date startTime, long respTime);

    Long getAvgCostTime(Date time, int beforeSeconds);

    PerformanceIndicator getPerformanceIndicator(Date time, int beforeSeconds);
}
