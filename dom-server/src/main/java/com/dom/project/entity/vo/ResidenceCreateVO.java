package com.dom.project.entity.vo;

import java.util.Collections;
import java.util.List;

/**
 * 入居登録レスポンス VO。
 */
public class ResidenceCreateVO {

    private String residenceHistoryId;
    private String firstUseDate;
    private List<String> warnings = Collections.emptyList();

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

    public String getFirstUseDate() {
        return firstUseDate;
    }

    public void setFirstUseDate(String firstUseDate) {
        this.firstUseDate = firstUseDate;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings == null ? Collections.emptyList() : warnings;
    }
}
