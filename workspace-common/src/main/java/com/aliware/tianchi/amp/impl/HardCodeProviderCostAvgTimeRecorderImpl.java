package com.aliware.tianchi.amp.impl;

import com.aliware.tianchi.amp.PerformanceIndicator;
import com.aliware.tianchi.amp.ProviderCostAvgTimeRecorder;
import com.aliware.tianchi.util.DateTimeUtils;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Fan Huaran
 * created on 2019/7/17
 * @description
 */
public class HardCodeProviderCostAvgTimeRecorderImpl implements ProviderCostAvgTimeRecorder {
    private static final int STORAGE_LENGTH = 600;
    private final long initSecondTime = new Date().getTime() / 1000;

    private final LongAdder[] invokerTotalRespTimeStorage;

    private final LongAdder[] invokerTotalCountStorage;

    {
        LongAdder[] array = new LongAdder[STORAGE_LENGTH];
        for (int i = 0; i < STORAGE_LENGTH; i++) {
            array[i] = new LongAdder();
        }
        invokerTotalRespTimeStorage = array;

        array = new LongAdder[STORAGE_LENGTH];
        for (int i = 0; i < STORAGE_LENGTH; i++) {
            array[i] = new LongAdder();
        }
        invokerTotalCountStorage = array;
    }

    private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);


    @Override
    public void recordCostTime(Date startTime, long respTime) {
        executor.execute(() -> {
            int offset = (int) (startTime.getTime() / 1000 - initSecondTime);

            invokerTotalRespTimeStorage[offset].add(respTime);
            invokerTotalCountStorage[offset].increment();
        });
    }

    @Override
    public Long getAvgCostTime(Date time, int beforeSeconds) {
        int offset = (int) (time.getTime() / 1000 - initSecondTime) - beforeSeconds;
        if (offset < 0) {
            return null;
        }

        long totalRespTime = invokerTotalRespTimeStorage[offset].sum();
        long totalCount = invokerTotalCountStorage[offset].sum();

        if (totalRespTime == 0 || totalCount == 0) {
            System.out.println("current avg time,offset:" + offset + " stat_time:" + DateTimeUtils.formatDateTime(time) + ",avg:" + null + ",cur_time:" + DateTimeUtils.formatDateTime(new Date()));
            return null;
        }

        long avg = totalRespTime / totalCount;
        System.out.println("current avg time,offset:" + offset + " stat_time:" + DateTimeUtils.formatDateTime(time) + ",avg:" + avg + ",cur_time:" + DateTimeUtils.formatDateTime(new Date()));
        return avg;
    }

    @Override
    public PerformanceIndicator getPerformanceIndicator(Date time, int beforeSeconds) {
        int offset = (int) (time.getTime() / 1000 - initSecondTime) - beforeSeconds;
        if (offset < 0) {
            return null;
        }

        long totalRespTime = invokerTotalRespTimeStorage[offset].sum();
        long totalCount = invokerTotalCountStorage[offset].sum();
        if (totalRespTime == 0 || totalCount == 0) {
            System.out.println("current PerformanceIndicator,offset:" + offset + " stat_time:" + DateTimeUtils.formatDateTime(time) + ",performanceIndicator:" + null + ",cur_time:" + DateTimeUtils.formatDateTime(new Date()));
            return null;
        }

        long avg = totalRespTime / totalCount;
        PerformanceIndicator performanceIndicator = new PerformanceIndicator(totalRespTime, totalCount, avg);
        System.out.println("current PerformanceIndicator,offset:" + offset + " stat_time:" + DateTimeUtils.formatDateTime(time) + ",performanceIndicator:" + performanceIndicator + ",cur_time:" + DateTimeUtils.formatDateTime(new Date()));
        return new PerformanceIndicator();
    }

    protected HardCodeProviderCostAvgTimeRecorderImpl() {

    }

    private static class Inner {
        private static final HardCodeProviderCostAvgTimeRecorderImpl instance = new HardCodeProviderCostAvgTimeRecorderImpl();
    }

    public static HardCodeProviderCostAvgTimeRecorderImpl getInstance() {
        return Inner.instance;
    }
}
