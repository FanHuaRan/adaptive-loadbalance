package com.aliware.tianchi.statistics.window;

import com.aliware.tianchi.util.TimeUtils;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Fan Huaran
 * created on 2019/7/19
 * @description
 */
public class LeapWindow {
    /**
     * 采样窗口的长度，以毫秒为单位
     */
    protected final int windowLength;

    /**
     * 时间窗的总时间间隔，以毫秒为单位
     */
    protected final int intervalInMs;

    /**
     * 采样窗口的个数 sampleCount=intervalInMs/windowLength
     */
    protected final int sampleCount;

    /**
     * 采样的时间窗口数组，需要使用AtomicReferenceArray保证数组内元素的可见性
     */
    protected final AtomicReferenceArray<WindowElement> array;

    /**
     * 时间窗滑动的时候，涉及多个共享资源，所以需要独占锁保护滑动时候的线程安全
     */
    protected Lock addLock = new ReentrantLock();

    public LeapWindow(int windowLength, int intervalInSec) {
        this.windowLength = windowLength;
        this.intervalInMs = intervalInSec * 1000;
        this.sampleCount = intervalInSec * 1000 / windowLength;

        this.array = new AtomicReferenceArray<>(sampleCount);
    }

    /**
     * 以time为准的当前采样窗口，time通过外部传入，可以支持多种时间（相对时间，网络时间等）
     *
     * @param time
     * @return
     */
    public WindowElement currentWindowElement(long time) {
        // time每增加一个windowLength的长度，timeId就会增加1，时间窗口就会往前滑动一个
        long timeId = time / windowLength;
        // Calculate current index.
        // idx被分成[0,arrayLength-1]中的某一个数，作为array数组中的索引
        int idx = (int) (timeId % array.length());

        // Cut the time to current window start.
        long currentWindowStart = time - time % windowLength;

        while (true) {
            // 从采样数组中根据索引获取缓存的时间窗口
            WindowElement old = array.get(idx);
            // array数组长度不宜过大，否则old很多情况下都命中不了，就会创建很多个WindowWrap对象
            if (old == null) {
                // 如果没有获取到，则创建一个新的
                WindowElement window = new WindowElement(windowLength, currentWindowStart);
                // 通过CAS将新窗口设置到数组中去
                if (array.compareAndSet(idx, null, window)) {
                    // 如果能设置成功，则将该窗口返回
                    return window;
                } else {
                    // 否则当前线程让出时间片，等待
                    Thread.yield();
                }
                // 如果当前窗口的开始时间与old的开始时间相等，则直接返回old窗口
            } else if (currentWindowStart == old.getWindowStart()) {
                return old;
                // 如果当前时间窗口的开始时间已经超过了old窗口的开始时间，则放弃old窗口
                // 并将time设置为新的时间窗口的开始时间，此时窗口向前滑动
            } else if (currentWindowStart > old.getWindowStart()) {
                if (addLock.tryLock()) {
                    try {
                        // if (old is deprecated) then [LOCK] resetTo currentTime.
                        return resetWindowTo(old, currentWindowStart);
                    } finally {
                        addLock.unlock();
                    }
                } else {
                    Thread.yield();
                }
                // 这个条件一般不可能存在，除非允许向之前位置的窗口写数据，然后处理又过度延迟，需要在设计上尽量避免
            } else if (currentWindowStart < old.getWindowStart()) {
                // Cannot go through here.
                return new WindowElement(windowLength, currentWindowStart);
            }
        }
    }

    /**
     * 获取以time为准的前一个采样窗口，time通过外部传入，可以支持多种时间（相对时间，网络时间等）
     *
     * @param time
     * @return
     */
    public WindowElement getPreviousWindow(long time) {
        // 计算上一个窗的timeId
        long timeId = (time - windowLength) / windowLength;
        // idx被分成[0,arrayLength-1]中的某一个数，作为array数组中的索引
        int idx = (int) (timeId % array.length());
        // time = time - 时间窗长度
        time = time - windowLength;

        // 获取窗口元素
        WindowElement windowElement = array.get(idx);

        // null or  采样窗口已过期(即采样窗口的起始时间到当前时间的间隔大于时间窗总间隔)
        if (windowElement == null || isWindowDeprecated(windowElement)) {
            return null;
        }

        // 因为是对数组进行循环使用，所以可能这个窗口是老窗口了。
        if (windowElement.getWindowStart() + windowLength < (time)) {
            return null;
        }

        return windowElement;
    }

    /**
     * 判断采样窗口是否已过期，即采样窗口的起始时间到当前时间的间隔大于时间窗总间隔
     *
     * @param windowElement
     * @return
     */
    private boolean isWindowDeprecated(WindowElement windowElement) {
        return TimeUtils.currentTimeMillis() - windowElement.getWindowStart() >= intervalInMs;
    }

    /**
     * 重置时间窗口
     *
     * @param old
     * @param currentWindowStart
     * @return
     */
    private WindowElement resetWindowTo(WindowElement old, long currentWindowStart) {
        // 这儿是直接重置窗口基本属性，可以减少内存使用率，但有脏数据的风险
        // 允许向之前位置的窗口写数据，然后处理又过度延迟就会出现脏写，需要在设计上尽量避免
        old.reset(windowLength, currentWindowStart);
        return old;

        // or return new WindowElement(windowLength, currentWindowStart)
    }
}
