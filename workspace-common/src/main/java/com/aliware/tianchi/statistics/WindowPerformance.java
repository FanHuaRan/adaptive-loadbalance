package com.aliware.tianchi.statistics;

/**
 * @author Fan Huaran
 * created on 2019/7/19
 * @description 窗口性能信息
 */
public class WindowPerformance {
    /**
     * 总处理数量
     */
    private Long totalCount;

    /**
     * 总处理时间
     */
    private Long totalCostTime;

    public WindowPerformance() {
    }

    public WindowPerformance(Long totalCount, Long totalCostTime) {
        this.totalCount = totalCount;
        this.totalCostTime = totalCostTime;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getTotalCostTime() {
        return totalCostTime;
    }

    public void setTotalCostTime(Long totalCostTime) {
        this.totalCostTime = totalCostTime;
    }

    @Override
    public String toString() {
        return "WindowPerformance{" +
                "totalCount=" + totalCount +
                ", totalCostTime=" + totalCostTime +
                '}';
    }
}
