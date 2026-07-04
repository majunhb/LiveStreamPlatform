package com.livestream.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    /** 日期格式：yyyy-MM-dd */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    
    /** 时间格式：HH:mm:ss */
    public static final String TIME_PATTERN = "HH:mm:ss";
    
    /** 日期时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    /** 时间戳格式：yyyyMMddHHmmss */
    public static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmss";

    /**
     * 格式化日期
     */
    public static String format(Date date) {
        return format(date, DATETIME_PATTERN);
    }

    /**
     * 格式化日期（自定义格式）
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 解析日期字符串
     */
    public static Date parse(String dateStr) {
        return parse(dateStr, DATETIME_PATTERN);
    }

    /**
     * 解析日期字符串（自定义格式）
     */
    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前时间戳字符串
     */
    public static String currentTimestamp() {
        return format(new Date(), TIMESTAMP_PATTERN);
    }

    /**
     * 获取当前Unix时间戳（秒）
     */
    public static long currentUnixTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前Unix时间戳（毫秒）
     */
    public static long currentUnixTimeMillis() {
        return System.currentTimeMillis();
    }
}
