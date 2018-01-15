package com.botann.driverclient.model;

import java.io.Serializable;

/**
 * Created by Orion on 2017/8/3.
 */
public class CouponInfo implements Serializable {
    private Integer fare;
    private Integer used;
    private Integer usedAmouont;
    private Long createDate;
    private Long endDate;
    private Integer prescription;
    private String couponTitle;

    public Integer getFare() {
        return fare;
    }

    public void setFare(Integer fare) {
        this.fare = fare;
    }

    public Integer getUsed() {
        return used;
    }

    public void setUsed(Integer used) {
        this.used = used;
    }

    public Integer getUsedAmouont() {
        return usedAmouont;
    }

    public void setUsedAmouont(Integer usedAmouont) {
        this.usedAmouont = usedAmouont;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Integer getPrescription() {
        return prescription;
    }

    public void setPrescription(Integer prescription) {
        this.prescription = prescription;
    }

    public String getCouponTitle() {
        return couponTitle;
    }

    public void setCouponTitle(String couponTitle) {
        this.couponTitle = couponTitle;
    }

}
