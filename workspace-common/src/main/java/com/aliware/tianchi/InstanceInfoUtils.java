package com.aliware.tianchi;

import com.sun.management.OperatingSystemMXBean;
import org.apache.dubbo.common.utils.StringUtils;

import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fan Huaran
 * created on 2019/7/10
 * @description 实例信息收集工具
 */
public class InstanceInfoUtils {

    /**
     * 获取实例信息
     *
     * @return
     */
    public final static InstanceInfo getInstanceInfo() {

        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setPid(getPid());
        /// step1: 取内存信息
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMen = mbean.getHeapMemoryUsage();
        // 堆内存
        // 返回 Java 虚拟机最初从操作系统请求用于内存管理的内存量（以字节为单位）。
        instanceInfo.setHeapInit(heapMen.getInit());
        // 返回已使用的内存量（以字节为单位）。
        instanceInfo.setHeapUsed(heapMen.getUsed());
        // 返回可以用于内存管理的最大内存量（以字节为单位）。
        instanceInfo.setHeapMax(heapMen.getMax());
        // 返回已提交给 Java 虚拟机使用的内存量（以字节为单位）。
        instanceInfo.setHeapCommitted(heapMen.getCommitted());
        // 非堆内存
        heapMen = mbean.getNonHeapMemoryUsage();
        instanceInfo.setNonHeapInit(heapMen.getInit());
        instanceInfo.setNonHeapUsed(heapMen.getUsed());
        instanceInfo.setNonHeapMax(heapMen.getMax());
        instanceInfo.setNonHeapCommitted(heapMen.getCommitted());

        /// step2：取线程信息
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        // 返回活动守护线程的当前数目。
        instanceInfo.setThreadDaemonCount(threadMXBean.getDaemonThreadCount());
        // 返回自从 Java 虚拟机启动或峰值重置以来峰值活动线程计数。
        instanceInfo.setThreadPeakCount(threadMXBean.getPeakThreadCount());
        // 返回活动线程的当前数目，包括守护线程和非守护线程。
        instanceInfo.setThreadCount(threadMXBean.getThreadCount());
        // 返回自从 Java 虚拟机启动以来创建和启动的线程总数目。
        instanceInfo.setThreadTotalCount(threadMXBean.getTotalStartedThreadCount());

        /// step3：获取取系统信息
        // 获取系统相关资源使用：CPU
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 返回“近期CPU使用率”为Java虚拟机的过程,返回值为0.0-1.0,如果不可用，则返回-1
        instanceInfo.setProcessCpuLoad(osmxb.getProcessCpuLoad());
        // 返回“近期CPU使用率”为整个系统,返回值为0.0-1.0,如果不可用，则返回-1
        instanceInfo.setSystemCpuLoad(osmxb.getSystemCpuLoad());
        // 返回当前JVM进程使用CPU的运行时间,单位纳秒
        instanceInfo.setProcessCpuTime(osmxb.getProcessCpuTime());
        // 返回最后一分钟的系统负荷平均值,只支持JDK1.7以上
        instanceInfo.setSystemLoadAverage(osmxb.getSystemLoadAverage());

        return instanceInfo;

    }

    private static Integer getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pidStr = name.split("@")[0];
        if (StringUtils.isBlank(pidStr) || StringUtils.isNumeric(pidStr, false)) {
            return null;
        }

        return Integer.valueOf(pidStr);
    }

}
