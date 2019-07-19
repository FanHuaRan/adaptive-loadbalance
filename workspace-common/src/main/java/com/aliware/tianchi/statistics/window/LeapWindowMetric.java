package com.aliware.tianchi.statistics.window;

import com.aliware.tianchi.util.TimeUtils;

/**
 * @author Fan Huaran
 * created on 2019/7/19
 * @description
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
