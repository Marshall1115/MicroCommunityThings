package com.java110.entity.car;

import java.io.Serializable;

public class CarDayDto implements Serializable {
    public CarDayDto() {
    }

    public CarDayDto(String carNum, String leaseType, int day) {
        this.carNum = carNum;
        this.leaseType = leaseType;
        this.day = day;
    }

    private String carNum;
    private String leaseType;
    private int day;

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getLeaseType() {
        return leaseType;
    }

    public void setLeaseType(String leaseType) {
        this.leaseType = leaseType;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
