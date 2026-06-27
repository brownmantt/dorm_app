package com.dom.project.util;

import com.dom.project.constant.AppConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 業務主キー ID 生成器。
 * 形式：{prefix}{yyyyMMdd}{4桁連番}、最大長 VARCHAR(20) 以内。
 */
public final class IdGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private static volatile String currentDate = LocalDate.now().format(DATE_FORMAT);

    private IdGenerator() {
    }

    /**
     * 業務プレフィックス付き主キー ID を生成。
     *
     * @param prefix 業務プレフィックス（例：D、R、RH、DF）
     * @return 20 文字以内の業務 ID
     */
    public static String nextId(String prefix) {
        String safePrefix = prefix == null ? "" : prefix.trim().toUpperCase();
        if (safePrefix.length() > AppConstants.ID_PREFIX_MAX_LENGTH) {
            safePrefix = safePrefix.substring(0, AppConstants.ID_PREFIX_MAX_LENGTH);
        }

        String today = LocalDate.now().format(DATE_FORMAT);
        synchronized (IdGenerator.class) {
            if (!today.equals(currentDate)) {
                currentDate = today;
                SEQUENCE.set(0);
            }
            int seq = SEQUENCE.incrementAndGet() % 10000;
            if (seq == 0) {
                seq = SEQUENCE.incrementAndGet() % 10000;
            }
            return String.format("%s%s%04d", safePrefix, today, seq);
        }
    }

    /**
     * DB 上の当日最大連番を反映し、再起動後の主キー重複を防ぐ。
     *
     * @param minSequence 当日分 ID の末尾4桁の最大値（0 の場合は無視）
     */
    public static void ensureMinSequence(int minSequence) {
        if (minSequence <= 0) {
            return;
        }
        synchronized (IdGenerator.class) {
            String today = LocalDate.now().format(DATE_FORMAT);
            if (!today.equals(currentDate)) {
                currentDate = today;
                SEQUENCE.set(0);
            }
            if (SEQUENCE.get() < minSequence) {
                SEQUENCE.set(minSequence);
            }
        }
    }
}
