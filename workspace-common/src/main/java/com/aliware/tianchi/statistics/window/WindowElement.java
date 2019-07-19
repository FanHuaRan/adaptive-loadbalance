package com.aliware.tianchi.statistics.window;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description 窗口元素
 */
public class WindowElement {
    /**
     * 窗口长度，毫秒为单位，volatile保证可见
     */
    private volatile long windowLength;

    /**
     * 窗口起始位置，unix毫秒时间戳，volatile保证可见
     */
    private volatile long windowStart;

    /**
     * 个数计数器
     */
    private LongAdder countAdder = new LongAdder();

    /**
     * 处理时间计数器
     */
    private LongAdder totalTimeAdder = new LongAdder();

    public WindowElement(long windowLength, long windowStart) {
        this.windowLength = windowLength;
        this.windowStart = windowStart;
    }

    public long getWindowLength() {
        return windowLength;
    }

    public long getWindowStart() {
        return windowStart;
    }


    /**
     * 记录一次处理
     *
     * @param costTime
     */
    public void addPass(long costTime) {
        // 次数+1
        countAdder.increment();
        // 总时间+costTime
        totalTimeAdder.add(costTime);
    }

    /**
     * 获取总处理次数
     *
     * @return
     */
    public long getCount() {
        return countAdder.sum();
    }

    /**
     * 获取总响应时间
     *
     * @return
     */
    public long getTotalCostTime() {
        return totalTimeAdder.sum();
    }

    /**
     * 重置窗口
     *
     * @param windowLength
     * @param windowStart
     */
    public void reset(long windowLength, long windowStart) {
        // 变动窗口起始时间
        this.windowLength = windowLength;
        this.windowStart = windowStart;

        // 计数清零
        this.countAdder.reset();
        this.totalTimeAdder.reset();
    }


}
