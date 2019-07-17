package com.aliware.tianchi;

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
            System.out.println("current avg time,offset:" + offset + " stat_time:" + DateTimeUtils.formatDateTime(time) + ",avg:" + null);
            return null;
        }

        long avg = totalRespTime / totalCount;
        System.out.println("current avg time,offset:" + offset + " stat_time:" + DateTimeUtils.formatDateTime(time) + ",avg:" + avg);
        return avg;
    }

    private HardCodeProviderCostAvgTimeRecorderImpl() {

    }

    private static class Inner {
        private static final HardCodeProviderCostAvgTimeRecorderImpl instance = new HardCodeProviderCostAvgTimeRecorderImpl();
    }

    public static HardCodeProviderCostAvgTimeRecorderImpl getInstance() {
        return Inner.instance;
    }
}
