package com.botann.driverclient.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextPaint;
import android.widget.TextView;

import org.apache.http.cookie.Cookie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orion on 2017/7/17.
 */

public class Utils {

    private static List<Cookie> cookies;

    public static List<Cookie> getCookies() {
        return cookies;
    }

    public static void setCookies(List<Cookie> cookies) {
        Utils.cookies = cookies;
    }

    // 计算出该TextView中文字的长度(像素)
    public static float getTextViewLength(TextView textView,String text) {
        TextPaint paint = textView.getPaint();
        // 得到使用该paint写上text的时候，像素为多少
        float textLength = paint.measureText(text);
        return textLength;
    }

    /* 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName: 应用包名
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }
}
