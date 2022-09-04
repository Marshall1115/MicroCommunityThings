package com.java110.entity.parkingArea;

import java.io.Serializable;

public class InOutCarTextDto implements Serializable {
    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private String voice;
    private String remark;

    public InOutCarTextDto() {
    }

    public InOutCarTextDto(String text1, String text2, String voice) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = "";
        this.text4 = "";
        this.voice = voice;
        this.remark = voice;
    }

    public InOutCarTextDto(String text1, String text2, String voice,String remark) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = "";
        this.text4 = "";
        this.voice = voice;
        this.remark = remark;
    }

    public InOutCarTextDto(String text1, String text2, String text3, String text4, String voice, String remark) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        this.voice = voice;
        this.remark = remark;
    }

    public InOutCarTextDto(String text1, String text2, String text3, String text4, String voice) {
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;
        this.voice = voice;
        this.remark = voice;
    }


    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getText4() {
        return text4;
    }

    public void setText4(String text4) {
        this.text4 = text4;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
