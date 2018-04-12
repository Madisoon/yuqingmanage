package com.syx.yuqingmanage.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Msater Zg on 2017/2/17.
 */
public class DifTimeGet {
    public static String getWeekTime(Date dt) {
        String[] weekDays = {"day_seven", "day_one", "day_two", "day_three", "day_four", "day_five", "day_six"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static int judgeTimeInterval(String timeInterval, Date nowTime) {
        int flag;
        /*String timeInterval = getPostTime(schemeId, nowTime);*/
        String[] times = timeInterval.split("-");
        if (timeInterval == "" || times[0].equals(times[1])) {
            flag = 0;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String nowTimeHM = sdf.format(nowTime);
            int startResult = nowTimeHM.compareTo(times[0]);
            int endResult = nowTimeHM.compareTo(times[1]);
            if (startResult > 0 && endResult < 0) {
                //立刻发
                flag = 1;
            } else {
                //定时发送
                flag = 0;
            }
        }
        return flag;
    }
}
