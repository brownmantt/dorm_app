package com.dom.project.entity.view;

import java.time.LocalDate;

/**
 * 初回利用日検索結果ビュー（社員情報を関連付け）。
 */
public class FirstUseDateView {

    private LocalDate firstUseDate;
    private String employeeCategory;
    private String employeeName;

    public LocalDate getFirstUseDate() {
        return firstUseDate;
    }

    public void setFirstUseDate(LocalDate firstUseDate) {
        this.firstUseDate = firstUseDate;
    }

    public String getEmployeeCategory() {
        return employeeCategory;
    }

    public void setEmployeeCategory(String employeeCategory) {
        this.employeeCategory = employeeCategory;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
