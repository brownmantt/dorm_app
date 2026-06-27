package com.dom.project.util;

import com.dom.project.constant.AppConstants;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 寮割カレンダー用ヘルパー。
 */
public final class DormAllocationHelper {

    private static final LocalDate INFINITE_END = LocalDate.of(9999, 12, 31);
    private static final List<String> WEEKDAY_JA = List.of("日", "月", "火", "水", "木", "金", "土");

    private DormAllocationHelper() {
    }

    public static YearMonth parseYearMonth(String yearMonth) {
        return YearMonth.parse(yearMonth);
    }

    public static LocalDate monthStart(String yearMonth) {
        return parseYearMonth(yearMonth).atDay(1);
    }

    public static LocalDate monthEnd(String yearMonth) {
        return parseYearMonth(yearMonth).atEndOfMonth();
    }

    public static int daysInMonth(String yearMonth) {
        return parseYearMonth(yearMonth).lengthOfMonth();
    }

    public static List<String> weekdayJaLabels(String yearMonth) {
        LocalDate start = monthStart(yearMonth);
        int days = daysInMonth(yearMonth);
        List<String> labels = new ArrayList<>(days);
        for (int day = 0; day < days; day++) {
            labels.add(weekdayJa(start.plusDays(day).getDayOfWeek()));
        }
        return labels;
    }

    public static String weekdayJa(DayOfWeek dayOfWeek) {
        int index = dayOfWeek.getValue() % 7;
        return WEEKDAY_JA.get(index);
    }

    public static LocalDate effectiveMoveOut(LocalDate moveOutDate) {
        return moveOutDate == null ? INFINITE_END : moveOutDate;
    }

    public static List<Integer> occupiedDays(LocalDate moveInDate, LocalDate moveOutDate, String yearMonth) {
        if (moveInDate == null) {
            return Collections.emptyList();
        }
        LocalDate start = monthStart(yearMonth);
        LocalDate end = monthEnd(yearMonth);
        LocalDate effectiveOut = effectiveMoveOut(moveOutDate);
        if (moveInDate.isAfter(end) || effectiveOut.isBefore(start)) {
            return Collections.emptyList();
        }

        LocalDate occupiedStart = moveInDate.isAfter(start) ? moveInDate : start;
        LocalDate occupiedEnd = effectiveOut.isBefore(end) ? effectiveOut : end;
        List<Integer> days = new ArrayList<>();
        for (LocalDate date = occupiedStart; !date.isAfter(occupiedEnd); date = date.plusDays(1)) {
            days.add(date.getDayOfMonth());
        }
        return days;
    }

    public static List<Integer> detectConflicts(List<Integer> occupiedDays, java.util.Map<String, Integer> dayCountByKey,
                                                String roomConflictKey, int roomCapacity) {
        if (roomConflictKey == null || roomConflictKey.isBlank() || occupiedDays == null || occupiedDays.isEmpty()) {
            return Collections.emptyList();
        }
        int capacity = roomCapacity > 0 ? roomCapacity : 1;
        List<Integer> conflicts = new ArrayList<>();
        for (Integer day : occupiedDays) {
            String key = roomConflictKey + ":" + day;
            if (dayCountByKey.getOrDefault(key, 0) > capacity) {
                conflicts.add(day);
            }
        }
        return conflicts;
    }

    public static boolean moveOutWarning(LocalDate moveOutDate, LocalDate asOfDate) {
        if (moveOutDate == null) {
            return false;
        }
        LocalDate baseDate = asOfDate == null ? LocalDate.now() : asOfDate;
        long diff = java.time.temporal.ChronoUnit.DAYS.between(baseDate, moveOutDate);
        return diff >= 0 && diff <= AppConstants.MOVE_OUT_WARNING_DAYS;
    }

    public static boolean moveOutInMonth(LocalDate moveOutDate, String yearMonth) {
        if (moveOutDate == null) {
            return false;
        }
        return !moveOutDate.isBefore(monthStart(yearMonth)) && !moveOutDate.isAfter(monthEnd(yearMonth));
    }

    public static String inferRegionFromAddress(String address) {
        String text = address == null ? "" : address;
        if (text.contains("東京")) {
            return "TOKYO";
        }
        if (text.contains("大阪")) {
            return "OSAKA";
        }
        if (text.contains("名古屋") || text.contains("愛知")) {
            return "NAGOYA";
        }
        return "OTHER";
    }
}
