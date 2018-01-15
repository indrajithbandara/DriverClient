package com.botann.driverclient.model;

import java.io.Serializable;

/**
 * Created by Orion on 2017/9/20.
 */
public class TeamInfo implements Serializable {
    private String name;
    private String phone;
    private Integer rank;
    private Integer yesterdayCurrent;
    private String totalCurrent;
    private String serverRank;
    private Integer del;
    private String teamName;
    private Long catchDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getYesterdayCurrent() {
        return yesterdayCurrent;
    }

    public void setYesterdayCurrent(Integer yesterdayCurrent) {
        this.yesterdayCurrent = yesterdayCurrent;
    }

    public String getTotalCurrent() {
        return totalCurrent;
    }

    public void setTotalCurrent(String totalCurrent) {
        this.totalCurrent = totalCurrent;
    }

    public String getServerRank() {
        return serverRank;
    }

    public void setServerRank(String serverRank) {
        this.serverRank = serverRank;
    }

    public Integer getDel() {
        return del;
    }

    public void setDel(Integer del) {
        this.del = del;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getCatchDate() {
        return catchDate;
    }

    public void setCatchDate(Long catchDate) {
        this.catchDate = catchDate;
    }
}
