package com.java110.entity.manufacturer;

import java.io.Serializable;

/**
 * @ClassName ManufacturerDto 硬件厂商
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/18 20:42
 * @Version 1.0
 * add by wuxw 2020/5/18
 **/
public class ManufacturerAttrDto extends ManufacturerDto implements Serializable {

    public static final String SPEC_TOPIC = "95001";

    private String hmId;
    private String attrId;
    private String specCd;
    private String value;
    private String createTime;

    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    public String getSpecCd() {
        return specCd;
    }

    public void setSpecCd(String specCd) {
        this.specCd = specCd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
