package com.aliware.tianchi.statistics.window;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author Fan Huaran
 * created on 2019/7/18
 * @description
 */
public class WindowElement {
    private volatile long windowLength;

    private volatile long windowStart;

    private LongAdder countAdder = new LongAdder();

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

    public void addPass(long costTime) {
        countAdder.increment();
        totalTimeAdder.add(costTime);
    }

    public long getCount() {
        return countAdder.sum();
    }

    public long getTotalCostTime() {
        return totalTimeAdder.sum();
    }

    public void reset(long windowLength, long windowStart){
        this.windowLength = windowLength;
        this.windowStart = windowStart;
        this.countAdder.reset();
        this.totalTimeAdder.reset();
    }


}
