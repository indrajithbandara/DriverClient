package com.botann.driverclient.model;

import java.io.Serializable;

/**
 * Created by Orion on 2017/7/11.
 */
public class User implements Serializable{
    private String token;
    private Integer accountId;
    private Integer driverId;    //团队司机账号

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }
}
