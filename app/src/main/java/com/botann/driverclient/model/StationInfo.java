package com.botann.driverclient.model;

import java.io.Serializable;

/**
 * Created by Orion on 2017/7/26.
 */
public class StationInfo implements Serializable {
    private String stationName;
    private String address;
    private String phone;
    private String beginTime;
    private String endTime;
    private Double longitude;
    private Double latitude;
    private Double distance;
    private String status;
    private Integer lineCount;
    private Integer batteryCount;

    public StationInfo(String StationName,String Address,String Phone,String BeginTime,String EndTime,Double Longitude,Double Latitude,Double Distance,String Status,Integer LineCount,Integer BatteryCount) {
        this.stationName = StationName;
        this.address = Address;
        this.phone = Phone;
        this.beginTime = BeginTime;
        this.endTime = EndTime;
        this.longitude = Longitude;
        this.latitude = Latitude;
        this.distance = Distance;
        this.status = Status;
        this.lineCount = LineCount;
        this.batteryCount = BatteryCount;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getLineCount() {
        return lineCount;
    }

    public void setLineCount(Integer lineCount) {
        this.lineCount = lineCount;
    }

    public Integer getBatteryCount() {
        return batteryCount;
    }

    public void setBatteryCount(Integer batteryCount) {
        this.batteryCount = batteryCount;
    }
}
