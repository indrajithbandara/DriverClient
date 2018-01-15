package com.botann.driverclient.network.api;

import com.botann.driverclient.utils.Constants;

/**
 * Created by Orion on 2017/7/11.
 */
public class API {

    /****************      Server      ****************/
    //服务器
    public static String BASE_URL = Constants.appBaseURL+Constants.serverType;

    /****************      API      ****************/
    public static String URL_LOGIN = "/login";                          // 登录
    public static String URL_INFO = "/information";                     // 获取个人主页信息
    public static String URL_EXCHANGE_LIST = "/exchange/list";          // 消费记录
    public static String URL_RECHARGE_LIST = "/recharge/list";          // 充值记录
    public static String URL_CITYID = "/city/getCityByName";            // 通过城市名称获取城市ID
    public static String URL_STATION_LIST = "/station/list";            // 换电站列表
    public static String URL_COUPON_LIST = "/coupon/list";              // 优惠券
    public static String URL_SYSTEM_MESSAGE = "/message/sysMsgList";    // 系统消息
    public static String URL_POST_MESSAGE = "/message/list";            // 推送消息
    public static String URL_TEAM_INFO = "/team/teamInformation";       // 我的团队
    public static String URL_MEMBER_INFO = "/team/teamMember";          // 成员信息
    public static String URL_VERSION_INFO = "/apkInfo/getApkInfo";      // 获取服务器版本信息
}
