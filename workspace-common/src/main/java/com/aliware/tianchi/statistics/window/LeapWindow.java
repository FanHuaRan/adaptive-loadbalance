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
    // 时间窗口的长度
    protected final int windowLength;

    // 采样窗口的个数
    protected final int sampleCount;

    // 以毫秒为单位的时间间隔
    protected final int intervalInMs;

    // 采样的时间窗口数组
    protected AtomicReferenceArray<WindowElement> array;

    protected Lock addLock = new ReentrantLock();

    public LeapWindow(int windowLength, int intervalInSec) {
        this.windowLength = windowLength;
        this.intervalInMs = intervalInSec * 1000;
        this.sampleCount = intervalInSec * 1000 / windowLength;

        this.array = new AtomicReferenceArray<>(sampleCount);
    }

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
                // 这个条件不可能存在
            } else if (currentWindowStart < old.getWindowStart()) {
                // Cannot go through here.
                return new WindowElement(windowLength, currentWindowStart);
            }
        }
    }

    public WindowElement getPreviousWindow(long time) {
        long timeId = (time - windowLength) / windowLength;
        int idx = (int) (timeId % array.length());
        time = time - windowLength;
        WindowElement windowElement = array.get(idx);

        if (windowElement == null || isWindowDeprecated(windowElement)) {
            return null;
        }

        if (windowElement.getWindowStart() + windowLength < (time)) {
            return null;
        }

        return windowElement;
    }

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
        old.reset(windowLength, currentWindowStart);
        return old;
    }
}
