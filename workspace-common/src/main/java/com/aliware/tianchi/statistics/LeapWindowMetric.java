package com.aliware.tianchi.statistics;

import com.aliware.tianchi.statistics.window.LeapWindow;
import com.aliware.tianchi.statistics.window.WindowElement;
import com.aliware.tianchi.util.TimeUtils;

/**
 * @author Fan Huaran
 * created on 2019/7/19
 * @description 滑动窗口的应用之滑动窗口指标记录器
 */
public class LeapWindowMetric {

    private final LeapWindow leapWindow;

    public LeapWindowMetric(int windowLength, int intervalInSec) {
        this.leapWindow = new LeapWindow(windowLength, intervalInSec);
    }

    public void addPass(long costTime){
        WindowElement windowElement = leapWindow.currentWindowElement(TimeUtils.currentTimeMillis());
        windowElement.addPass(costTime);
    }

    public WindowPerformance getCurrentWindowPerformance(){
        WindowElement windowElement = leapWindow.currentWindowElement(TimeUtils.currentTimeMillis());
        if (windowElement == null){
            return null;
        }

        long totalCount = windowElement.getCount();
        long totalCostTime = windowElement.getTotalCostTime();

        return new WindowPerformance(totalCount, totalCostTime);
    }

    public WindowPerformance getPreviousWindowPerformance(){
        WindowElement windowElement = leapWindow.getPreviousWindow(TimeUtils.currentTimeMillis());
        if (windowElement == null){
            return null;
        }

        long totalCount = windowElement.getCount();
        long totalCostTime = windowElement.getTotalCostTime();

        return new WindowPerformance(totalCount, totalCostTime);
    }



}
