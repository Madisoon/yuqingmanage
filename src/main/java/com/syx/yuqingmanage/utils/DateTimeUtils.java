package com.syx.yuqingmanage.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Msater Zg on 2017/7/24.
 */
public class DateTimeUtils {
    public static String getNowTime(String format) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String dataTime = simpleDateFormat.format(date);
        return dataTime;
    }
}
