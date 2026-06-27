package com.dom.project.entity.view;

/**
 * 割当可能部屋検索結果ビュー。
 */
public class AssignableRoomView {

    private String roomId;
    private String roomName;
    private String dormitoryId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDormitoryId() {
        return dormitoryId;
    }

    public void setDormitoryId(String dormitoryId) {
        this.dormitoryId = dormitoryId;
    }
}
