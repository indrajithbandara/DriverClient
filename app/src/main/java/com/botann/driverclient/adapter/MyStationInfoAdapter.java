package com.botann.driverclient.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.botann.driverclient.MainApp;
import com.botann.driverclient.R;
import com.botann.driverclient.model.StationInfo;
import com.botann.driverclient.ui.activity.BNMainActivity;
import com.botann.driverclient.utils.Constants;
import com.botann.driverclient.utils.Utils;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Orion on 2017/7/26.
 */
public class MyStationInfoAdapter extends BaseAdapter<StationInfo> {
    private static SimpleDateFormat format =  new SimpleDateFormat("HH:mm");
    private static DecimalFormat df = new DecimalFormat("######0.00");
    private static Drawable icon;
    private Context mContext;

    public MyStationInfoAdapter(Context context) { mContext = context; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.station_list_item, parent, false);
        return new MyStationInfoAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyStationInfoAdapter.MyViewHolder) holder).bind(getDataSet().get(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mStationName;
        TextView mStationStatus;
        TextView mStationAddr;
        TextView mStationDist;
        TextView mStationPhone;
        TextView mStationTime;
        TextView mBatteryCount;
        TextView mLineCount;
        Button btnNavigation;

        public MyViewHolder(View itemView) {
            super(itemView);

            mStationName = (TextView) itemView.findViewById(R.id.station_name);
            mStationStatus = (TextView) itemView.findViewById(R.id.station_status);
            mStationAddr = (TextView) itemView.findViewById(R.id.station_address);
            mStationDist = (TextView) itemView.findViewById(R.id.station_distance);
            mStationPhone = (TextView) itemView.findViewById(R.id.station_phone);
            mStationTime = (TextView) itemView.findViewById(R.id.station_time);
            mBatteryCount = (TextView) itemView.findViewById(R.id.battery_count);
            mLineCount = (TextView) itemView.findViewById(R.id.line_count);
            btnNavigation = (Button) itemView.findViewById(R.id.btn_navigation);
        }

        public void bind(final StationInfo content) {
            LatLng start = new LatLng(Constants.currentLatitude, Constants.currentLongitude);
            LatLng end = new LatLng(content.getLatitude(), content.getLongitude());
            mStationName.setText(content.getStationName());
            mStationStatus.setText(content.getStatus());
            float textLength = Utils.getTextViewLength(mStationName,content.getStationName());
            int length = ((int)textLength)+6;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mStationStatus.getLayoutParams();
            params.setMargins(length,6,0,6);
            mStationStatus.setLayoutParams(params);
            if(content.getStatus().equals("运营中")) {
                mStationStatus.setBackgroundResource(R.drawable.station_status_on_bg);
            }else{
                mStationStatus.setBackgroundResource(R.drawable.station_status_off_bg);
            }
            mStationAddr.setText(content.getAddress());
            mStationDist.setText(df.format(DistanceUtil.getDistance(start, end)/1000) + "km");
            mStationPhone.setText(content.getPhone());
            mStationTime.setText(content.getBeginTime().substring(0,5) + "-" + content.getEndTime().substring(0,5));
            if(content.getBatteryCount()!=null){
                if(content.getBatteryCount()>0){
                    mBatteryCount.setText("电池库存:有");
                }else{
                    mBatteryCount.setText("电池库存:无");
                }
            }else{
                mBatteryCount.setText("电池库存:无");
            }
            mLineCount.setText("排队人数:"+content.getLineCount());
            btnNavigation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
                    //跳转到新activity导航
                    //Intent intent = new Intent(MainApp.getInstance().getApplicationContext(),BNMainActivity.class);
                    //Bundle bundle = new Bundle();
                    //bundle.putDouble("sLongitude",Constants.currentLongitude);
                    //bundle.putDouble("sLatitude",Constants.currentLatitude);
                    //bundle.putString("sName",Constants.currentAddr);
                    //bundle.putDouble("eLongitude",content.getLongitude());
                    //bundle.putDouble("eLatitude",content.getLatitude());
                    //bundle.putString("eStationName",content.getStationName());
                    //intent.putExtras(bundle);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //MainApp.getInstance().getApplicationContext().startActivity(intent);
                    //跳转到外部导航app
                    if(Utils.isAvilible(MainApp.getInstance().getApplicationContext(),"com.baidu.BaiduMap")&&!Utils.isAvilible(MainApp.getInstance().getApplicationContext(),"com.autonavi.minimap")) {
                        try {
                            Intent intent = Intent.getIntent("intent://map/direction?" +
                                    "origin="+Constants.currentAddr+  //起点  此处不传值默认选择当前位置
                                    "&destination="+content.getAddress()+ //"|name:我的目的地"+        //终点
                                    "&mode=driving" +          //导航路线方式
                                    "&src=伯坦科技|蓝色大道#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainApp.getInstance().getApplicationContext().startActivity(intent); //启动调用
                        } catch (Exception e) {
                            Log.e("intent", e.getMessage());
                        }
                    }if(!Utils.isAvilible(MainApp.getInstance().getApplicationContext(),"com.baidu.BaiduMap")&&!Utils.isAvilible(MainApp.getInstance().getApplicationContext(),"com.autonavi.minimap")) {
                        Toast.makeText(MainApp.getInstance().getApplicationContext(), "您尚未安装高德地图", Toast.LENGTH_LONG).show();
                        Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainApp.getInstance().getApplicationContext().startActivity(intent);
                    }else{
                        try {
                            Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=蓝色大道&poiname=伯坦科技"+"&lat="+content.getLatitude()+"&lon="+content.getLongitude()+"&dev=0");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainApp.getInstance().getApplicationContext().startActivity(intent); //启动调用
                        } catch (Exception e) {
                            Log.e("intent", e.getMessage());
                        }
                    }
                }
            });
        }

    }
}
