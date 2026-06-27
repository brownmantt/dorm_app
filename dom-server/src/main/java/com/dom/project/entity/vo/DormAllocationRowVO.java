package com.dom.project.entity.vo;

import java.time.LocalDate;
import java.util.List;

/**
 * 寮割当カレンダー行 VO。
 */
public class DormAllocationRowVO {

    private String residenceHistoryId;
    private String employeeId;
    private String employeeName;
    private String affiliationName;
    private String roomName;
    private String roomId;
    private String roomDetail;
    private Boolean hasAirConditioner;
    private Boolean vacant;
    private Boolean isManager;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private Boolean moveOutInMonth;
    private Boolean moveOutWarning;
    private List<Integer> occupiedDays;
    private List<Integer> conflictDays;

    public String getResidenceHistoryId() {
        return residenceHistoryId;
    }

    public void setResidenceHistoryId(String residenceHistoryId) {
        this.residenceHistoryId = residenceHistoryId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getAffiliationName() {
        return affiliationName;
    }

    public void setAffiliationName(String affiliationName) {
        this.affiliationName = affiliationName;
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

    public String getRoomDetail() {
        return roomDetail;
    }

    public void setRoomDetail(String roomDetail) {
        this.roomDetail = roomDetail;
    }

    public Boolean getHasAirConditioner() {
        return hasAirConditioner;
    }

    public void setHasAirConditioner(Boolean hasAirConditioner) {
        this.hasAirConditioner = hasAirConditioner;
    }

    public Boolean getVacant() {
        return vacant;
    }

    public void setVacant(Boolean vacant) {
        this.vacant = vacant;
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean manager) {
        isManager = manager;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }

    public Boolean getMoveOutInMonth() {
        return moveOutInMonth;
    }

    public void setMoveOutInMonth(Boolean moveOutInMonth) {
        this.moveOutInMonth = moveOutInMonth;
    }

    public Boolean getMoveOutWarning() {
        return moveOutWarning;
    }

    public void setMoveOutWarning(Boolean moveOutWarning) {
        this.moveOutWarning = moveOutWarning;
    }

    public List<Integer> getOccupiedDays() {
        return occupiedDays;
    }

    public void setOccupiedDays(List<Integer> occupiedDays) {
        this.occupiedDays = occupiedDays;
    }

    public List<Integer> getConflictDays() {
        return conflictDays;
    }

    public void setConflictDays(List<Integer> conflictDays) {
        this.conflictDays = conflictDays;
    }
}
