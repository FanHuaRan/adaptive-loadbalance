package com.aliware.tianchi.statistics;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Fan Huaran
 * created on 2019/7/19
 * @description 相对调用次数滑窗
 */
public class RelativeLeapWindowMetric {
    /**
     * 窗口元素长度
     */
    private final int windowLength;

    /**
     * 窗口元素数组
     */
    private final long[] windows;

    /**
     * 锁
     */
    private final Lock locker = new ReentrantLock();

    /**
     * 循环指针
     */
    private volatile int tail = 0;

    /**
     * 时间总数
     */
    private volatile long total;

    /**
     * 平均值
     */
    private volatile long avg;

    public RelativeLeapWindowMetric(int windowLength) {
        this.windowLength = windowLength;
        this.windows = new long[windowLength];
    }

    /**
     * 记录rtt
     *
     * @param rtt
     */
    public void addRtt(long rtt) {
        // 需要锁保证安全
        locker.lock();
        try {
            // tail可能已经越界，需要循环使用
            tail %= windowLength;
            // 获取旧rtt
            long oldRtt = windows[tail];
            // 记录新rtt
            windows[tail++] = rtt;
            // 增量更新total
            total += (rtt - oldRtt);
            // 更新avg，冷启动的时候，avg肯定有误差，需要看业务允许度或者自行修正
            avg = total / windowLength;
        } finally {
            locker.unlock();
        }
    }

    /**
     * 获取平均响应时间
     *
     * @return
     */
    public long getAvg() {
        return this.avg;
    }

}
