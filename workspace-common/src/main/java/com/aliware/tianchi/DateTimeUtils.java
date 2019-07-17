package com.aliware.tianchi;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Fan Huaran
 * created on 2019/7/17
 * @description
 */
public class DateTimeUtils {

    public static String formatDateTime(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS") ;
        return dateFormat.format(date);
    }

}
