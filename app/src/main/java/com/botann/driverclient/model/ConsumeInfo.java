package com.botann.driverclient.model;

import java.io.Serializable;

/**
 * Created by Orion on 2017/7/19.
 */
public class ConsumeInfo implements Serializable {
    private String serialNum;
    private Long createDate;
    private String stationName;
    private String carNumber;
    private Integer realMile;
    private Integer referMiles;
    private Integer realFare;
    private Integer coupon;
    private Integer balance;

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

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

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public Integer getRealMile() {
        return realMile;
    }

    public void setRealMile(Integer realMile) {
        this.realMile = realMile;
    }

    public Integer getReferMiles() {
        return referMiles;
    }

    public void setReferMiles(Integer referMiles) {
        this.referMiles = referMiles;
    }

    public Integer getRealFare() {
        return realFare;
    }

    public void setRealFare(Integer realFare) {
        this.realFare = realFare;
    }

    public Integer getCoupon() {
        return coupon;
    }

    public void setCoupon(Integer coupon) {
        this.coupon = coupon;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
