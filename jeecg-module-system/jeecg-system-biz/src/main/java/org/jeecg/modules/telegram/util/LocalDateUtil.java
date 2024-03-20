package org.jeecg.modules.telegram.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LocalDateUtil {
    public static final String yyyy_mm_dd_H24 = "yyyy-MM-dd HH:mm:ss";

    public static String expireStr() {
        return LocalDateTime.now().plusMinutes(30).format(DateTimeFormatter.ofPattern(yyyy_mm_dd_H24));
    }

    public static String expireStr(LocalDateTime localDateTime) {
        return localDateTime.plusMinutes(30).format(DateTimeFormatter.ofPattern(yyyy_mm_dd_H24));
    }

    public static String toDateTimeStr(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(yyyy_mm_dd_H24));
    }

    public static long milliSecond(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static LocalDateTime secondsToDateTime(long seconds) {
        return LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
    }

}
