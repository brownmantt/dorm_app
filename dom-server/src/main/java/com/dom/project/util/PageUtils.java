package com.dom.project.util;

import com.dom.project.constant.AppConstants;

/**
 * ページングユーティリティ。
 */
public final class PageUtils {

    private PageUtils() {
    }

    /**
     * ページングオフセットを計算（page は 0 始まり）。
     */
    public static int offset(Integer page, Integer size) {
        int safePage = page == null || page < 0 ? AppConstants.DEFAULT_PAGE : page;
        int safeSize = size == null || size <= 0 ? AppConstants.DEFAULT_PAGE_SIZE : size;
        return safePage * safeSize;
    }

    /**
     * 规范化每页大小。
     */
    public static int limit(Integer size) {
        return size == null || size <= 0 ? AppConstants.DEFAULT_PAGE_SIZE : size;
    }
}
