package com.dom.project.entity.view;

import java.time.LocalDate;

/**
 * 空き室一覧検索結果ビュー。
 */
public class VacancyListView {

    private String dormitoryId;
    private String dormitoryName;
    private String roomId;
    private String roomName;
    /** ステータス：VACANT / OCCUPIED */
    private String status;
    private String residentName;
    private String residenceHistoryId;
    private LocalDate expectedMoveOutDate;
    private Boolean assignable;

    public String getDormitoryName() {
        return dormitoryName;
    }

    public void setDormitoryName(String dormitoryName) {
        this.dormitoryName = dormitoryName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

    public LocalDate getExpectedMoveOutDate() {
        return expectedMoveOutDate;
    }

    public void setExpectedMoveOutDate(LocalDate expectedMoveOutDate) {
        this.expectedMoveOutDate = expectedMoveOutDate;
    }

    public Boolean getAssignable() {
        return assignable;
    }

    public void setAssignable(Boolean assignable) {
        this.assignable = assignable;
    }

    public String getDormitoryId() {
        return dormitoryId;
    }

    public void setDormitoryId(String dormitoryId) {
        this.dormitoryId = dormitoryId;
    }
}
