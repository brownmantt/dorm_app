package com.dom.project.entity.common;

import java.util.Collections;
import java.util.List;

/**
 * ページングレスポンス構造。
 * フロントエンド Element Plus ページングコンポーネント対応：content / totalElements / totalPages。
 *
 * @param <T> 列表元素类型
 */
public class PageResult<T> {

    /** 当前页数据列表 */
    private List<T> content;

    /** 总记录数 */
    private Long totalElements;

    /** 总页数 */
    private Integer totalPages;

    public PageResult() {
    }

    public PageResult(List<T> content, Long totalElements, Integer totalPages) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    /**
     * ページング結果を構築。
     *
     * @param content       当前页数据
     * @param totalElements 总记录数
     * @param pageSize      每页大小
     * @param <T>           元素类型
     * @return ページングレスポンスオブジェクト
     */
    public static <T> PageResult<T> of(List<T> content, Long totalElements, Integer pageSize) {
        List<T> safeContent = content == null ? Collections.emptyList() : content;
        Long safeTotal = totalElements == null ? 0L : totalElements;
        int safePageSize = pageSize == null || pageSize <= 0 ? 1 : pageSize;
        int totalPages = safeTotal == 0L ? 0 : (int) Math.ceil((double) safeTotal / safePageSize);
        return new PageResult<>(safeContent, safeTotal, totalPages);
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
