package com.java110.entity.parkingCouponCar;

import com.java110.entity.PageDto;

import java.io.Serializable;

/**
 * @ClassName CommunityDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/18 8:43
 * @Version 1.0
 * add by wuxw 2020/5/18
 **/
public class ParkingCouponCarDto extends PageDto implements Serializable {

    public static final String STATE_W = "1001";
    public static final String STATE_F = "2002";

    private String taskId;
    private String pccId;

    private String couponName;

    private String shopName;

    private String communityId;

    private String paId;

    private String createTime;

    private String carNum;

    private String giveWay;

    private String typeCd;

    private String statusCd;
    private String value;
    private String state;
    private String remark;
    private String startTime;
    private String endTime;
    private String extPccId;


    public String getPccId() {
        return pccId;
    }

    public void setPccId(String pccId) {
        this.pccId = pccId;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getPaId() {
        return paId;
    }

    public void setPaId(String paId) {
        this.paId = paId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getGiveWay() {
        return giveWay;
    }

    public void setGiveWay(String giveWay) {
        this.giveWay = giveWay;
    }

    public String getTypeCd() {
        return typeCd;
    }

    public void setTypeCd(String typeCd) {
        this.typeCd = typeCd;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExtPccId() {
        return extPccId;
    }

    public void setExtPccId(String extPccId) {
        this.extPccId = extPccId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
