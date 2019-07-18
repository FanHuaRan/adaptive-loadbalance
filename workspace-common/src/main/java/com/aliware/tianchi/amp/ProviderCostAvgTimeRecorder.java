package com.aliware.tianchi.amp;

import java.util.Date;

/**
 * @author Fan Huaran
 * created on 2019/7/17
 * @description
 */
public interface ProviderCostAvgTimeRecorder {
    void recordStart();

    void recordCostTime(Date startTime, long respTime);

    Long getAvgCostTime(Date time, int beforeSeconds);

    PerformanceIndicator getPerformanceIndicator(Date time, int beforeSeconds);
}
