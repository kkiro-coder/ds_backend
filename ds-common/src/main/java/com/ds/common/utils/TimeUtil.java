package com.ds.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 *
 * @author ds
 */
public final class TimeUtil {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    private TimeUtil() {
    }

    // ==================== LocalDateTime ====================

    /**
     * LocalDateTime → 字符串 (yyyy-MM-dd HH:mm:ss)
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * LocalDateTime → 字符串（指定格式）
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 字符串 → LocalDateTime (yyyy-MM-dd HH:mm:ss)
     */
    public static LocalDateTime parseDateTime(String str) {
        return str != null && !str.isBlank() ? LocalDateTime.parse(str, DATE_TIME_FORMATTER) : null;
    }

    /**
     * 字符串 → LocalDateTime（指定格式）
     */
    public static LocalDateTime parseDateTime(String str, String pattern) {
        return str != null && !str.isBlank() ? LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 获取当前 LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前 LocalDateTime 字符串
     */
    public static String nowStr() {
        return format(LocalDateTime.now());
    }

    // ==================== LocalDate ====================

    /**
     * LocalDate → 字符串 (yyyy-MM-dd)
     */
    public static String format(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }

    /**
     * LocalDate → 字符串（指定格式）
     */
    public static String format(LocalDate date, String pattern) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 字符串 → LocalDate (yyyy-MM-dd)
     */
    public static LocalDate parseDate(String str) {
        return str != null && !str.isBlank() ? LocalDate.parse(str, DATE_FORMATTER) : null;
    }

    /**
     * 字符串 → LocalDate（指定格式）
     */
    public static LocalDate parseDate(String str, String pattern) {
        return str != null && !str.isBlank() ? LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 获取当前 LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 获取当前 LocalDate 字符串
     */
    public static String todayStr() {
        return format(LocalDate.now());
    }
}
