package com.botann.driverclient.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bigkoo.pickerview.OptionsPickerView;
import com.botann.driverclient.MainApp;
import com.botann.driverclient.R;
import com.botann.driverclient.model.Bean.JsonBean;
import com.botann.driverclient.model.StationInfo;
import com.botann.driverclient.ui.InfoWindowHolder;
import com.botann.driverclient.utils.Constants;
import com.botann.driverclient.utils.GetJsonDataUtil;
import com.botann.driverclient.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orion on 2017/8/11.
 */
public class MapActivity extends Activity implements SensorEventListener {

    private static DecimalFormat df = new DecimalFormat("######0.00");

    private ArrayList<JsonBean> option1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> option2Items = new ArrayList<>();
    private ArrayList<ArrayList<Double>> Lat2Items = new ArrayList<>();
    private ArrayList<ArrayList<Double>> Lon2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> option3Items = new ArrayList<>();
    private Thread thread;
    private TextView mapCity;
    private TextView mapQuit;
    private Dialog mStationDialog;
    private Context mContext;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private String mCurrentAddr = null;
    private float mCurrentAccracy;
    //换电站数据
    private List<StationInfo> stationInfoList = new ArrayList<StationInfo>();
    private static final BitmapDescriptor bitmap1 = BitmapDescriptorFactory
            .fromResource(R.drawable.location_normal);
    private static final BitmapDescriptor bitmap2 = BitmapDescriptorFactory
            .fromResource(R.drawable.location_unnormal);
    private OverlayOptions options;
    //marker信息
    private String markerName;
    private String markerStatus;
    private String markerAddress;
    private String markerPhone;
    private String markerBegin;
    private String markerEnd;
    private Integer markerBattery;
    private Integer markerLine;
    private Double markerLat;
    private Double markerLon;
    /**
     *@Fields mInfoWindow : 弹出的窗口
     */
    private InfoWindow mInfoWindow;
    private LinearLayout baidumap_infowindow;
    private MarkerOnInfoWindowClickListener markerListener;

    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    RadioGroup.OnCheckedChangeListener radioButtonListener;
    Button requestLocButton;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private float direction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_map);
        Intent intent=getIntent();
        stationInfoList = (List<StationInfo>) intent.getSerializableExtra("list");
        baidumap_infowindow = (LinearLayout) LayoutInflater.from (this).inflate (R.layout.baidumap_infowindow, null);
        markerListener = new MarkerOnInfoWindowClickListener ();
        requestLocButton = (Button) findViewById(R.id.button1);
        mapCity = (TextView) this.findViewById(R.id.map_city);
        mapQuit = (TextView) this.findViewById(R.id.map_quit);
        if(Constants.change == 1) {
            mapCity.setText(Constants.cityname);
        }
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        requestLocButton.setText("普通");
        View.OnClickListener btnClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        requestLocButton.setText("普通");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        requestLocButton.setText("罗盘");
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);

        RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
        radioButtonListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.defaulticon) {
                    // 传入null则，恢复默认图标
                    mCurrentMarker = null;
                    mBaiduMap
                            .setMyLocationConfigeration(new MyLocationConfiguration(
                                    mCurrentMode, true, null));
                }
                if (checkedId == R.id.customicon) {
                    // 修改为自定义marker
                    mCurrentMarker = BitmapDescriptorFactory
                            .fromResource(R.drawable.location_normal);
                    mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                            mCurrentMode, true, mCurrentMarker,
                            accuracyCircleFillColor, accuracyCircleStrokeColor));
                }
            }
        };
        group.setOnCheckedChangeListener(radioButtonListener);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //获得marker中的数据
                markerName = marker.getExtraInfo().getString("marker");
                markerStatus = marker.getExtraInfo().getString("status");
                markerAddress = marker.getExtraInfo().getString("address");
                markerPhone = marker.getExtraInfo().getString("phone");
                markerBegin = marker.getExtraInfo().getString("beginTime");
                markerEnd = marker.getExtraInfo().getString("endTime");
                markerBattery = marker.getExtraInfo().getInt("batteryCount");
                markerLine = marker.getExtraInfo().getInt("lineCount");
                markerLat = marker.getExtraInfo().getDouble("latitude");
                markerLon = marker.getExtraInfo().getDouble("longitude");

                mStationDialog = new Dialog(mContext, R.style.my_dialog);
                LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.station_map, null);
                TextView stationName = (TextView) root.findViewById(R.id.station_name);
                TextView stationStatus = (TextView) root.findViewById(R.id.station_status);
                TextView stationAddress = (TextView) root.findViewById(R.id.station_address);
                TextView stationPhone = (TextView) root.findViewById(R.id.station_phone);
                TextView stationDistance = (TextView) root.findViewById(R.id.station_distance);
                TextView stationTime = (TextView) root.findViewById(R.id.station_time);
                TextView stationBattery = (TextView) root.findViewById(R.id.battery_count);
                TextView stationLine = (TextView) root.findViewById(R.id.line_count);
                root.findViewById(R.id.btn_navigation).setOnClickListener(btnlistener);

                LatLng start = new LatLng(Constants.currentLatitude, Constants.currentLongitude);
                LatLng end = new LatLng(markerLat, markerLon);
                stationName.setText(markerName);
                stationStatus.setText(markerStatus);
                float textLength = Utils.getTextViewLength(stationName,markerName);
                int length = ((int)textLength)+6;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) stationStatus.getLayoutParams();
                params.setMargins(length,6,0,6);
                stationStatus.setLayoutParams(params);
                if(markerStatus.equals("运营中")) {
                    stationStatus.setBackgroundResource(R.drawable.station_status_on_bg);
                }else{
                    stationStatus.setBackgroundResource(R.drawable.station_status_off_bg);
                }
                stationAddress.setText(markerAddress);
                stationDistance.setText(df.format(DistanceUtil.getDistance(start, end)/1000) + "km");
                stationPhone.setText(markerPhone);
                stationTime.setText(markerBegin.substring(0,5) + "-" + markerEnd.substring(0,5));
                if(markerBattery>0){
                    stationBattery.setText("电池库存:有");
                }else{
                    stationBattery.setText("电池库存:无");
                }
                stationLine.setText("排队人数:"+markerLine);
                mStationDialog.setContentView(root);
                Window dialogWindow = mStationDialog.getWindow();
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.setWindowAnimations(R.style.dialogstyle);
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.x = 0;
                lp.y = -20;
                lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
                root.measure(0, 0);
                lp.height = root.getMeasuredHeight();
                lp.alpha = 9f; // 透明度
                dialogWindow.setAttributes(lp);
                mStationDialog.setCanceledOnTouchOutside(true);
                mStationDialog.show();
                //createInfoWindow(baidumap_infowindow, markerName, markerStatus);

                //将marker所在的经纬度的信息转化成屏幕上的坐标
                //final LatLng ll = marker.getPosition();

                //mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(baidumap_infowindow), ll, -47, markerListener);
                //显示InfoWindow
                //mBaiduMap.showInfoWindow(mInfoWindow);
                return true;
            }
        });
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mapCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPickerView();
            }
        });
        mapQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapActivity.this.finish();
            }
        });
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    initJsonData();
                }
            });
            thread.start();
        }
    }

    private View.OnClickListener btnlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mStationDialog.dismiss();
            //跳转到新activity导航
            //Intent intent = new Intent(MainApp.getInstance().getApplicationContext(),BNMainActivity.class);
            //Bundle bundle = new Bundle();
            //bundle.putDouble("sLongitude", mCurrentLon);
            //bundle.putDouble("sLatitude",mCurrentLat);
            //bundle.putString("sName",mCurrentAddr);
            //bundle.putDouble("eLongitude",markerLon);
            //bundle.putDouble("eLatitude",markerLat);
            //bundle.putString("eStationName",markerName);
            //intent.putExtras(bundle);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //MainApp.getInstance().getApplicationContext().startActivity(intent);
            //跳转到外部导航app
            if(Utils.isAvilible(MainApp.getInstance().getApplicationContext(),"com.baidu.BaiduMap")&&!Utils.isAvilible(MainApp.getInstance().getApplicationContext(),"com.autonavi.minimap")) {
                try {
                    Intent intent = Intent.getIntent("intent://map/direction?" +
                            "origin="+Constants.currentAddr+  //起点  此处不传值默认选择当前位置
                            "&destination="+markerAddress+ //"|name:我的目的地"+        //终点
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
                    Intent intent = Intent.getIntent("androidamap://navi?sourceApplication=蓝色大道&poiname=伯坦科技"+"&lat="+markerLat+"&lon="+markerLon+"&dev=0");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainApp.getInstance().getApplicationContext().startActivity(intent); //启动调用
                } catch (Exception e) {
                    Log.e("intent", e.getMessage());
                }
            }
        }
    };

    /**
     * 解析数据
     */
    private void initJsonData() {

        String JsonData = new GetJsonDataUtil().getJson(this, "city.json");

        ArrayList<JsonBean> jsonBean = parseData(JsonData);

        /**
         * 添加省份数据
         */
        option1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<Double> LatList = new ArrayList<>();//该省的城市列表的维度列表
            ArrayList<Double> LonList = new ArrayList<>();//该省的城市列表的经度列表
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                Double Lat = jsonBean.get(i).getCityList().get(c).getLat();
                Double Lon = jsonBean.get(i).getCityList().get(c).getLon();
                CityList.add(CityName);//添加城市
                LatList.add(Lat);//添加城市纬度
                LonList.add(Lon);//添加城市经度

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    for (int d = 0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            option2Items.add(CityList);

            Lat2Items.add(LatList);
            Lon2Items.add(LonList);

            /**
             * 添加地区数据
             */
            option3Items.add(Province_AreaList);
        }
    }

    /**
     * Gson解析
     */
    public ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    private void createInfoWindow(LinearLayout baidumap_infowindow, final String stationName, String status){

        InfoWindowHolder holder = null;
        if(baidumap_infowindow.getTag () == null){
            holder = new InfoWindowHolder();

            holder.tv_entname = (TextView) baidumap_infowindow.findViewById(R.id.bi_info);
            holder.tv_status = (TextView) baidumap_infowindow.findViewById(R.id.bi_status);
            baidumap_infowindow.setTag(holder);
        }

        holder = (InfoWindowHolder) baidumap_infowindow.getTag();

        holder.tv_entname.setText(stationName);
        holder.tv_status.setText(status);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * 定位SDK监听函数, 需实现BDLocationListener里的方法
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAddr = location.getAddrStr();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                for(int i = 0; i < stationInfoList.size(); i++) {
                    LatLng point = new LatLng(stationInfoList.get(i).getLatitude(),stationInfoList.get(i).getLongitude());
                    //构建Marker图标
                    if(stationInfoList.get(i).getStatus().equals("运营中")) {
                        //构建MarkerOption，用于在地图上添加Marker，地图上的自定义的标签和
                        options = new MarkerOptions()
                                .position(point)
                                .icon(bitmap1);
                        //在地图上添加Marker，并显示
                        Marker marker = (Marker) mBaiduMap.addOverlay (options);
                        // 将信息保存
                        Bundle bundle = new Bundle ();
                        bundle.putString("marker", stationInfoList.get(i).getStationName());
                        bundle.putString("status", stationInfoList.get(i).getStatus());
                        bundle.putString("address", stationInfoList.get(i).getAddress());
                        bundle.putString("phone", stationInfoList.get(i).getPhone());
                        bundle.putString("beginTime", stationInfoList.get(i).getBeginTime());
                        bundle.putString("endTime", stationInfoList.get(i).getEndTime());
                        bundle.putDouble("latitude", stationInfoList.get(i).getLatitude());
                        bundle.putDouble("longitude", stationInfoList.get(i).getLongitude());
                        bundle.putInt("lineCount", stationInfoList.get(i).getLineCount());
                        bundle.putInt("batteryCount", stationInfoList.get(i).getBatteryCount()==null?0:stationInfoList.get(i).getBatteryCount());
                        marker.setExtraInfo (bundle);
                    }else{
                        options = new MarkerOptions()
                                .position(point)
                                .icon(bitmap2);
                        Marker marker = (Marker) mBaiduMap.addOverlay (options);
                        // 将信息保存
                        Bundle bundle = new Bundle ();
                        bundle.putString("marker", stationInfoList.get(i).getStationName());
                        bundle.putString("status", stationInfoList.get(i).getStatus());
                        bundle.putString("address", stationInfoList.get(i).getAddress());
                        bundle.putString("phone", stationInfoList.get(i).getPhone());
                        bundle.putString("beginTime", stationInfoList.get(i).getBeginTime());
                        bundle.putString("endTime", stationInfoList.get(i).getEndTime());
                        bundle.putDouble("latitude", stationInfoList.get(i).getLatitude());
                        bundle.putDouble("longitude", stationInfoList.get(i).getLongitude());
                        bundle.putString("lineCount", stationInfoList.get(i).getLineCount().toString());
                        bundle.putString("batteryCount", stationInfoList.get(i).getBatteryCount().toString().equals("")?"0":stationInfoList.get(i).getBatteryCount().toString());
                        marker.setExtraInfo (bundle);
                    }
                }
                LatLng ll;
                if(Constants.change==0) {
                    ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                }else {
                    ll = new LatLng(Constants.lat,
                            Constants.lon);
                }
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(13.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String var1, int var2){}
    }

    private final class  MarkerOnInfoWindowClickListener implements InfoWindow.OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(){
            //隐藏InfoWindow
            mBaiduMap.hideInfoWindow();
            //TODO
            //跳转到新activity导航
            Intent intent = new Intent(MainApp.getInstance().getApplicationContext(),BNMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putDouble("sLongitude", mCurrentLon);
            bundle.putDouble("sLatitude",mCurrentLat);
            bundle.putString("sName",mCurrentAddr);
            bundle.putDouble("eLongitude",markerLon);
            bundle.putDouble("eLatitude",markerLat);
            bundle.putString("eStationName",markerName);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApp.getInstance().getApplicationContext().startActivity(intent);
        }

    }

    private void ShowPickerView() {

        final OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String city = option2Items.get(options1).get(options2);
                final Double lat = Lat2Items.get(options1).get(options2);
                final Double lon = Lon2Items.get(options1).get(options2);
                mapCity.setText(city);
                Constants.city = city;
                //还需更新地图中心
                //TODO
                LatLng ll = new LatLng(lat, lon);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(13.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        })
                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        pvOptions.setPicker(option1Items, option2Items);//二级选择器
        pvOptions.show();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        //mStationDialog.dismiss();//这句导致地图会换电站列表是报空指针并闪退
        super.onDestroy();
    }

}
