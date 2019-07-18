package com.aliware.tianchi.amp;

/**
 * @author Fan Huaran
 * created on 2019/7/10
 * @description 实例信息
 */
public class InstanceInfo {
    /**
     * 进程编号
     */
    private Integer pid;
    /**
     * cpu核数
     */
    private Integer cpuCore;
    /**
     * 近期进程CPU使用率
     */
    private Double processCpuLoad;
    /**
     * 返回当前JVM进程使用CPU的运行时间,单位纳秒
     */
    private Long processCpuTime;
    /**
     * 返回近期系统CPU使用率
     */
    private Double systemCpuLoad;
    /**
     * 系统近期一分钟load
     */
    private Double systemLoadAverage;
    /**
     * 已提交给 Java 虚拟机使用的内存量（以字节为单位）。
     */
    private Long heapCommitted;
    /**
     * Java 虚拟机最初从操作系统请求用于内存管理的内存量（以字节为单位）
     */
    private Long heapInit;
    /**
     * 可以用于内存管理的最大内存量（以字节为单位）
     */
    private Long heapMax;
    /**
     * 已使用的内存量（以字节为单位）。
     */
    private Long heapUsed;
    /**
     * 非堆内存初始化多少
     */
    private Long nonHeapInit;
    /**
     * 非堆内存最大可用多少
     */
    private Long nonHeapMax;
    /**
     * 已经使用多少非堆内存
     */
    private Long nonHeapUsed;

    /**
     *
     */
    private Long nonHeapCommitted;

    /**
     * 活动线程数量
     */
    private Integer threadCount;

    /**
     * 守护线程数量
     */
    private Integer threadDaemonCount;
    /**
     * 从 Java 虚拟机启动或峰值重置以来峰值活动线程计数。
     */
    private Integer threadPeakCount;
    /**
     * 线程总数
     */
    private Long threadTotalCount;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getCpuCore() {
        return cpuCore;
    }

    public void setCpuCore(Integer cpuCore) {
        this.cpuCore = cpuCore;
    }

    public Double getProcessCpuLoad() {
        return processCpuLoad;
    }

    public void setProcessCpuLoad(Double processCpuLoad) {
        this.processCpuLoad = processCpuLoad;
    }

    public Long getProcessCpuTime() {
        return processCpuTime;
    }

    public void setProcessCpuTime(Long processCpuTime) {
        this.processCpuTime = processCpuTime;
    }

    public Double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(Double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public Double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    public void setSystemLoadAverage(Double systemLoadAverage) {
        this.systemLoadAverage = systemLoadAverage;
    }

    public Long getHeapCommitted() {
        return heapCommitted;
    }

    public void setHeapCommitted(Long heapCommitted) {
        this.heapCommitted = heapCommitted;
    }

    public Long getHeapInit() {
        return heapInit;
    }

    public void setHeapInit(Long heapInit) {
        this.heapInit = heapInit;
    }

    public Long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(Long heapMax) {
        this.heapMax = heapMax;
    }

    public Long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(Long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public Long getNonHeapInit() {
        return nonHeapInit;
    }

    public void setNonHeapInit(Long nonHeapInit) {
        this.nonHeapInit = nonHeapInit;
    }

    public Long getNonHeapMax() {
        return nonHeapMax;
    }

    public void setNonHeapMax(Long nonHeapMax) {
        this.nonHeapMax = nonHeapMax;
    }

    public Long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public void setNonHeapUsed(Long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
    }

    public Long getNonHeapCommitted() {
        return nonHeapCommitted;
    }

    public void setNonHeapCommitted(Long nonHeapCommitted) {
        this.nonHeapCommitted = nonHeapCommitted;
    }

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public Integer getThreadDaemonCount() {
        return threadDaemonCount;
    }

    public void setThreadDaemonCount(Integer threadDaemonCount) {
        this.threadDaemonCount = threadDaemonCount;
    }

    public Integer getThreadPeakCount() {
        return threadPeakCount;
    }

    public void setThreadPeakCount(Integer threadPeakCount) {
        this.threadPeakCount = threadPeakCount;
    }

    public Long getThreadTotalCount() {
        return threadTotalCount;
    }

    public void setThreadTotalCount(Long threadTotalCount) {
        this.threadTotalCount = threadTotalCount;
    }

    @Override
    public String toString() {
        return "InstanceInfo{" +
                "pid=" + pid +
                ", cpuCore=" + cpuCore +
                ", processCpuLoad=" + processCpuLoad +
                ", processCpuTime=" + processCpuTime +
                ", systemCpuLoad=" + systemCpuLoad +
                ", systemLoadAverage=" + systemLoadAverage +
                ", heapCommitted=" + heapCommitted +
                ", heapInit=" + heapInit +
                ", heapMax=" + heapMax +
                ", heapUsed=" + heapUsed +
                ", nonHeapInit=" + nonHeapInit +
                ", nonHeapMax=" + nonHeapMax +
                ", nonHeapUsed=" + nonHeapUsed +
                ", threadCount=" + threadCount +
                ", threadDaemonCount=" + threadDaemonCount +
                ", threadPeakCount=" + threadPeakCount +
                ", threadTotalCount=" + threadTotalCount +
                '}';
    }
}
