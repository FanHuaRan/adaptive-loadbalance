package com.aliware.tianchi.model;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description 性能处理指标
 */
public class PerformanceIndicator {
    /**
     * 总处理时间
     */
    private Long totalCostTime;

    /**
     * 总处理数量
     */
    private Long totalReceiveCount;

    /**
     * 平均处理时间
     */
    private Long avgCostTime;

    /**
     * 当前使用的线程数
     */
    private Long usedThreadCount;

    public PerformanceIndicator() {
    }

    public PerformanceIndicator(Long totalCostTime, Long totalReceiveCount, Long avgCostTime, Long usedThreadCount) {
        this.totalCostTime = totalCostTime;
        this.totalReceiveCount = totalReceiveCount;
        this.avgCostTime = avgCostTime;
        this.usedThreadCount = usedThreadCount;
    }

    public Long getTotalCostTime() {
        return totalCostTime;
    }

    public void setTotalCostTime(Long totalCostTime) {
        this.totalCostTime = totalCostTime;
    }

    public Long getTotalReceiveCount() {
        return totalReceiveCount;
    }

    public void setTotalReceiveCount(Long totalReceiveCount) {
        this.totalReceiveCount = totalReceiveCount;
    }

    public Long getAvgCostTime() {
        return avgCostTime;
    }

    public void setAvgCostTime(Long avgCostTime) {
        this.avgCostTime = avgCostTime;
    }

    public Long getUsedThreadCount() {
        return usedThreadCount;
    }

    public void setUsedThreadCount(Long usedThreadCount) {
        this.usedThreadCount = usedThreadCount;
    }

    @Override
    public String toString() {
        return "PerformanceIndicator{" +
                "totalCostTime=" + totalCostTime +
                ", totalReceiveCount=" + totalReceiveCount +
                ", avgCostTime=" + avgCostTime +
                ", usedThreadCount=" + usedThreadCount +
                '}';
    }
}
