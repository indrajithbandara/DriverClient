package com.botann.driverclient.model;

import java.io.Serializable;

/**
 * Created by Orion on 2017/7/21.
 */
public class RechargeInfo implements Serializable {
    private Long createDate;
    private String stationName;
    private Integer rechargeAmount;
    private Integer rechargeType;

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Integer getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Integer rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }
}
