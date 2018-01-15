package com.botann.driverclient.utils;

/**
 * Created by Orion on 2017/7/11.
 */
public class Constants {

    //accountId
    public static Integer accountId = -1;
    //driverId
    public static Integer driverId = -1;

    //正式服务器地址
    public static String appBaseURL = "http://114.55.40.180:8085";
    public final static String serverType = "/driver";

    //测试服务器地址
    //public static String appBaseURL = "http://114.55.40.180:8088";
    //public final static String serverType = "/drivertest";

    public static final int SUCCESS = 0;
    //是否是团队账号,0:否;1:是.
    public static Integer isTeam = 0;

    public static String province = "";
    public static String city = "";
    public static double currentLongitude = 120.150;       //定不到位默认给杭州的经度
    public static double currentLatitude = 30.280;          //定不到位默认给杭州的维度

    public static double lat = 0;       //换电站选择城市后该城市的维度
    public static double lon = 0;        //换电站选择城市后该城市的经度

    public static String cityId = "";
    public static String cityname = "";
    public static Integer change = 0;

    public static volatile Integer stationTotal = 0;
    public static volatile String stationRes = "";

    public static volatile Integer stationT = 0;
    public static volatile String stationR = "";

    public static volatile Integer refreshTotal = 0;
    public static volatile String refreshRes = "";

    public static volatile Integer couponTotal = 0;
    public static volatile String couponRes = "";

    public static volatile Integer messageTotal = 0;
    public static volatile String messageRes = "";

    public static volatile Integer postMessageTotal = 0;
    public static volatile String postMessageRes = "";

    public static String currentAddr = "";
}
