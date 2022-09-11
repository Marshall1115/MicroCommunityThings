package com.java110.entity.openDoor;

import com.java110.entity.PageDto;

import java.io.Serializable;

public class ManualOpenDoorLogDto extends PageDto implements Serializable {

    private String logId;
    private String staffId;
    private String staffName;
    private String extStaffId;
    private String machineId;
    private String machineName;
    private String extMachineId;
    private String photoJpg;
    private String communityId;

    private String boxId;

    private String createTime;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getExtStaffId() {
        return extStaffId;
    }

    public void setExtStaffId(String extStaffId) {
        this.extStaffId = extStaffId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getExtMachineId() {
        return extMachineId;
    }

    public void setExtMachineId(String extMachineId) {
        this.extMachineId = extMachineId;
    }

    public String getPhotoJpg() {
        return photoJpg;
    }

    public void setPhotoJpg(String photoJpg) {
        this.photoJpg = photoJpg;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
