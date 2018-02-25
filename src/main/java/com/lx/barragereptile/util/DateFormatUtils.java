package com.lx.barragereptile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 字符串转时间
     */
    public static Date parseToDate(String time, String fmt) {
        SimpleDateFormat format = new SimpleDateFormat(fmt);
        Date date = new Date();
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    /**
     * 时间转字符串
     */
    public static String fmtToString(Date date, String fmt) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat(fmt);
            return format.format(date);
        }
        return null;
    }

    /**
     * 将Unix时间戳转换成date
     */
    public static Date parseUnixTimeToData(String time) {
        long timest = Long.parseLong(time) * 1000;
        return new Date(timest);
    }

}
