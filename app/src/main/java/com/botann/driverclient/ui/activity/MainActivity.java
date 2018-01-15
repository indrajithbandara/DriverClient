package com.botann.driverclient.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.bigkoo.pickerview.OptionsPickerView;
import com.botann.driverclient.MainApp;
import com.botann.driverclient.R;
import com.botann.driverclient.model.Bean.JsonBean;
import com.botann.driverclient.model.StationInfo;
import com.botann.driverclient.network.api.API;
import com.botann.driverclient.ui.fragment.AccountFragment;
import com.botann.driverclient.ui.fragment.ConsumeFragment;
import com.botann.driverclient.ui.fragment.RechargeFragment;
import com.botann.driverclient.ui.fragment.StationFragment;
import com.botann.driverclient.ui.fragment.TeamFragment;
import com.botann.driverclient.utils.Constants;
import com.botann.driverclient.utils.GetJsonDataUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Orion on 2017/6/19.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {

    public LocationClient mLocationClient = new LocationClient(MainActivity.this);
    public MyLocationListener mMyLocationListener = new MyLocationListener();

    private ArrayList<JsonBean> option1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> option2Items = new ArrayList<>();
    private ArrayList<ArrayList<Double>> Lat2Items = new ArrayList<>();
    private ArrayList<ArrayList<Double>> Lon2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> option3Items = new ArrayList<>();
    //换电站数据
    private List<StationInfo> stationInfoList = new ArrayList<StationInfo>();
    private Thread thread;
    private TextView topBar;
    private TextView cityName;
    private TextView Quit;
    private TextView tabConsume;
    private TextView tabRecharge;
    private TextView tabStation;
    private TextView tabAccount;
    private Dialog mQuitDialog;

    private FrameLayout ly_content;

    private String name;
    private String teamName;
    private Integer yesterdayCurrent;
    private String serverRank;
    private String totalCurrent;
    private Long catchDate;
    private Integer rank;

    private ConsumeFragment f1;
    private RechargeFragment f2;
    private StationFragment f3, station_fragment;
    private AccountFragment f4, init_fragment;
    private TeamFragment f5, team_fragment;
    private FragmentManager fragmentManager;

    private Context mContext;
    private static MainActivity mInstance = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    public static MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mInstance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        bindView();
        setDefaultFragment();
        startLocate();
        if (thread == null) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    initJsonData();
                }
            });
            thread.start();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

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

    /**
     * 定位
     */
    private void startLocate() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
        //开启定位
        int checkPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            Log.d("请授权定位", "弹出提示");
            return;
        } else {
            mLocationClient.start();
        }
        //mLocationClient.start();
    }

    //UI组件初始化与事件绑定
    private void bindView() {
        topBar = (TextView) this.findViewById(R.id.txt_top);
        cityName = (TextView) this.findViewById(R.id.city_name);
        Quit = (TextView) this.findViewById(R.id.quit);
        tabConsume = (TextView) this.findViewById(R.id.txt_consume);
        tabRecharge = (TextView) this.findViewById(R.id.txt_recharge);
        tabStation = (TextView) this.findViewById(R.id.txt_station);
        tabAccount = (TextView) this.findViewById(R.id.txt_account);
        ly_content = (FrameLayout) findViewById(R.id.fragment_container);

        tabConsume.setOnClickListener(this);
        tabRecharge.setOnClickListener(this);
        tabStation.setOnClickListener(this);
        tabAccount.setOnClickListener(this);
        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPickerView();
            }
        });
    }

    private View.OnClickListener btnlistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 确认退出
                case R.id.btn_confirm_quit:
                    SharedPreferences.Editor consume = getSharedPreferences("consume",MODE_PRIVATE).edit();
                    consume.clear();
                    consume.commit();
                    SharedPreferences.Editor recharge = getSharedPreferences("recharge",MODE_PRIVATE).edit();
                    recharge.clear();
                    recharge.commit();
                    SharedPreferences.Editor coupon = getSharedPreferences("coupon",MODE_PRIVATE).edit();
                    coupon.clear();
                    coupon.commit();
                    Intent toLogin = new Intent(mContext, LoginActivity.class);
                    toLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(toLogin);
                    break;
                // 取消
                case R.id.btn_cancel:
                    if (mQuitDialog != null) {
                        mQuitDialog.dismiss();
                    }
                    break;
            }
        }
    };

    //设置默认的Fragment
    private void setDefaultFragment() {
        topBar = (TextView) this.findViewById(R.id.txt_top);
        cityName = (TextView) this.findViewById(R.id.city_name);
        tabAccount = (TextView) this.findViewById(R.id.txt_account);
        Quit = (TextView) this.findViewById(R.id.quit);

        tabAccount.setSelected(true);//设置默认选中我的账户
        tabAccount.setClickable(false);
        if(Constants.isTeam == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String url = API.BASE_URL+API.URL_INFO;
                    RequestParams params = new RequestParams();
                    params.add("accountId", Constants.accountId.toString());

                    final AsyncHttpClient client = new AsyncHttpClient();
                    //保存cookie，自动保存到了sharepreferences
                    PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                    client.setCookieStore(myCookieStore);
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            String json = new String(response);
                            Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                            JsonObject data = obj.get("data").getAsJsonObject();
                            String name = data.get("name").getAsString();
                            String account = data.get("account").getAsString();
                            float balance = data.get("balance").getAsFloat()/100;
                            Integer couponNum = data.get("couponNum").getAsInt();
                            Integer messageNum = data.get("messageNum").getAsInt();
                            String city = data.get("city").getAsString();
                            cityName.setText(city);
                            topBar.setText("我的账户");
                            Quit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mQuitDialog = new Dialog(mContext, R.style.my_dialog);
                                    LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.quit_account, null);
                                    root.findViewById(R.id.btn_confirm_quit).setOnClickListener(btnlistener);
                                    root.findViewById(R.id.btn_cancel).setOnClickListener(btnlistener);
                                    mQuitDialog.setContentView(root);
                                    Window dialogWindow = mQuitDialog.getWindow();
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
                                    mQuitDialog.setCanceledOnTouchOutside(true);
                                    mQuitDialog.show();
                                }
                            });
                            init_fragment = new AccountFragment().newInstance(account, name, balance, couponNum, messageNum);
                            transaction.replace(R.id.fragment_container, init_fragment);
                            transaction.commit();

                            SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("data",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("account",data.get("account").getAsString());
                            editor.putString("name",data.get("name").getAsString());
                            editor.putFloat("balance",data.get("balance").getAsFloat()/100);
                            editor.putInt("couponNum",data.get("couponNum").getAsInt());
                            editor.putInt("messageNum",data.get("messageNum").getAsInt());
                            editor.putString("city",data.get("city").getAsString());
                            editor.commit();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                            Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                            //Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            },200);
        }else{
            //团队司机
            tabConsume.setVisibility(View.INVISIBLE);
            tabRecharge.setVisibility(View.INVISIBLE);
            tabConsume.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0));
            tabRecharge.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String url = API.BASE_URL+API.URL_TEAM_INFO;
                    RequestParams params = new RequestParams();
                    params.add("driverId", Constants.driverId.toString());

                    final AsyncHttpClient client = new AsyncHttpClient();
                    //保存cookie，自动保存到了sharepreferences
                    PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                    client.setCookieStore(myCookieStore);
                    client.get(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            String json = new String(response);
                            Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                            JsonObject data = obj.get("data").getAsJsonObject();

                            //团队司机信息刷新完成
                            SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("team",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("name",data.get("name").getAsString());
                            editor.putString("phone",data.get("phone").getAsString());
                            editor.putInt("yesterdayCurrent",data.get("yesterdayCurrent").getAsInt());
                            editor.putString("serverRank",data.get("serverRank").getAsString());
                            editor.putString("totalCurrent",data.get("totalCurrent").getAsString());
                            String tName = data.get("teamName") != JsonNull.INSTANCE ? data.get("teamName").getAsString() : null;
                            editor.putString("teamName",tName);
                            Long cDate = data.get("catchDate") != JsonNull.INSTANCE ? data.get("catchDate").getAsLong() : 0;
                            editor.putLong("catchDate",cDate/1000);
                            editor.putInt("rank",data.get("rank").getAsInt());
                            editor.commit();

                            name = data.get("name").getAsString();
                            teamName = data.get("teamName") != JsonNull.INSTANCE ? data.get("teamName").getAsString() : null;
                            yesterdayCurrent = data.get("yesterdayCurrent").getAsInt();
                            serverRank = data.get("serverRank").getAsString();
                            totalCurrent = data.get("totalCurrent").getAsString();
                            catchDate = data.get("catchDate")!= JsonNull.INSTANCE ? data.get("catchDate").getAsLong() : 0;
                            rank = data.get("rank").getAsInt();

                            String url = API.BASE_URL+API.URL_MEMBER_INFO;
                            RequestParams params = new RequestParams();
                            params.add("driverId",Constants.driverId.toString());

                            final AsyncHttpClient client = new AsyncHttpClient();
                            //保存cookie，自动保存到了sharepreferences
                            PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                            client.setCookieStore(myCookieStore);
                            client.get(url, params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                    String json = new String(response);
                                    Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                    JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                    JsonArray data = obj.get("data").getAsJsonArray();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                                    //刷新完成
                                    String res = data.toString();
                                    cityName.setClickable(false);
                                    cityName.setText("");
                                    topBar.setText("我的团队");
                                    Quit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            mQuitDialog = new Dialog(mContext, R.style.my_dialog);
                                            LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.quit_account, null);
                                            root.findViewById(R.id.btn_confirm_quit).setOnClickListener(btnlistener);
                                            root.findViewById(R.id.btn_cancel).setOnClickListener(btnlistener);
                                            mQuitDialog.setContentView(root);
                                            Window dialogWindow = mQuitDialog.getWindow();
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
                                            mQuitDialog.setCanceledOnTouchOutside(true);
                                            mQuitDialog.show();
                                        }
                                    });
                                    team_fragment = new TeamFragment().newInstance(name, teamName, yesterdayCurrent, serverRank, totalCurrent, catchDate, rank, res);
                                    transaction.replace(R.id.fragment_container, team_fragment);
                                    transaction.commit();
                                    if(res != null) {
                                        SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("member",MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sp.edit();

                                        editor.putString("memberInfoList",res);
                                        editor.commit();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                                    Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                                    Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                                }

                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                            Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                            Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            },200);
        }
    }

    //重置所有文本的选中状态
    public void selected() {
        tabConsume.setSelected(false);
        tabRecharge.setSelected(false);
        tabStation.setSelected(false);
        tabAccount.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (f1 != null) {
            transaction.hide(f1);
        }
        if (f2 != null) {
            transaction.hide(f2);
        }
        if (f3 != null) {
            transaction.hide(f3);
        }
        if (f4 != null) {
            transaction.hide(f4);
        }
    }

    @Override
    public void onClick(View view) {
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences team = getSharedPreferences("team", MODE_PRIVATE);
        SharedPreferences member = getSharedPreferences("member", MODE_PRIVATE);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (view.getId()) {
            case R.id.txt_consume:
                selected();
                tabConsume.setSelected(true);
                tabAccount.setClickable(true);
                SharedPreferences consume = getSharedPreferences("consume", MODE_PRIVATE);
                String testStr = consume.getString("consumeInfoList", "");
                Integer total = consume.getInt("total", 0);
                f1 = new ConsumeFragment().newInstance(testStr, total);
                cityName.setClickable(false);
                cityName.setText("");
                Quit.setClickable(false);
                Quit.setText("");
                topBar.setText("消费记录");
                transaction.add(R.id.fragment_container, f1);
                break;

            case R.id.txt_recharge:
                selected();
                tabRecharge.setSelected(true);
                tabAccount.setClickable(true);
                //GetRechargeResponse.getRechargeResponse(MainActivity.this, Constants.accountId, 1, 20);
                SharedPreferences recharge = getSharedPreferences("recharge", MODE_PRIVATE);
                testStr = recharge.getString("rechargeInfoList", "");
                total = recharge.getInt("total", 0);
                f2 = new RechargeFragment().newInstance(testStr, total);
                cityName.setClickable(false);
                cityName.setText("");
                Quit.setClickable(false);
                Quit.setText("");
                topBar.setText("充值记录");
                transaction.add(R.id.fragment_container, f2);
                break;

            case R.id.txt_station:
                selected();
                tabStation.setSelected(true);
                tabAccount.setClickable(true);
                SDKInitializer.initialize(getApplicationContext());
                Constants.change = 0;
                f3 = StationFragment.newInstance(Constants.stationRes, Constants.stationTotal);
                cityName.setClickable(true);
                cityName.setText(Constants.city);
                Quit.setClickable(true);
                Quit.setText("显示地图");
                Quit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Gson gson = new Gson();
                        stationInfoList = gson.fromJson(Constants.stationR,new TypeToken<List<StationInfo>>(){}.getType());
                        Intent intent = new Intent(MainApp.getInstance().getApplicationContext(),MapActivity.class);
                        intent.putExtra("list", (Serializable)stationInfoList);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainApp.getInstance().getApplicationContext().startActivity(intent);
                    }
                });
                topBar.setText("换电站");
                transaction.add(R.id.fragment_container, f3);
                break;

            case R.id.txt_account:
                selected();
                tabAccount.setSelected(true);
                tabAccount.setClickable(false);
                if(Constants.isTeam == 0) {
                    String account = preferences.getString("account", "");
                    String name = preferences.getString("name", "");
                    Float balance = preferences.getFloat("balance", 0.00f);
                    Integer couponNum = preferences.getInt("couponNum", 0);
                    Integer messageNum = preferences.getInt("messageNum", 0);
                    String city = preferences.getString("city", "杭州市");
                    f4 = new AccountFragment().newInstance(account, name, balance, couponNum, messageNum);
                    cityName.setClickable(false);
                    cityName.setText(city);
                    Quit.setClickable(true);
                    Quit.setText("退出账户");
                    Quit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mQuitDialog = new Dialog(mContext, R.style.my_dialog);
                            LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.quit_account, null);
                            root.findViewById(R.id.btn_confirm_quit).setOnClickListener(btnlistener);
                            root.findViewById(R.id.btn_cancel).setOnClickListener(btnlistener);
                            mQuitDialog.setContentView(root);
                            Window dialogWindow = mQuitDialog.getWindow();
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
                            mQuitDialog.show();
                        }
                    });
                    topBar.setText("我的账户");
                    transaction.add(R.id.fragment_container, f4);
                }else{
                    f5 = new TeamFragment().newInstance(team.getString("name",""),team.getString("teamName",null),team.getInt("yesterdayCurrent",0),team.getString("serverRank",""),team.getString("totalCurrent",""),team.getLong("catchDate",0),team.getInt("rank",0),member.getString("memberInfoList",""));
                    cityName.setClickable(false);
                    cityName.setText("");
                    Quit.setClickable(true);
                    Quit.setText("退出账户");
                    Quit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mQuitDialog = new Dialog(mContext, R.style.my_dialog);
                            LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.quit_account, null);
                            root.findViewById(R.id.btn_confirm_quit).setOnClickListener(btnlistener);
                            root.findViewById(R.id.btn_cancel).setOnClickListener(btnlistener);
                            mQuitDialog.setContentView(root);
                            Window dialogWindow = mQuitDialog.getWindow();
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
                            mQuitDialog.show();
                        }
                    });
                    topBar.setText("我的团队");
                    transaction.add(R.id.fragment_container, f5);
                }
                break;

            default:
                break;

        }
        transaction.commit();
    }

    private void ShowPickerView() {

        final OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                final String city = option2Items.get(options1).get(options2);
                final Double lat = Lat2Items.get(options1).get(options2);
                final Double lon = Lon2Items.get(options1).get(options2);
                cityName.setText(city);
                Constants.cityname = city;
                Constants.lat = lat;
                Constants.lon = lon;
                Constants.change = 1;
                //还需刷新换电站列表
                //TODO
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final List<StationInfo> dataSet = new ArrayList<StationInfo>();
                        //网络请求更新数据
                        String url = API.BASE_URL+API.URL_CITYID;
                        RequestParams params = new RequestParams();
                        params.add("name", city);

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(url, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                String json = new String(response);
                                Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                JsonObject data = obj.get("data").getAsJsonObject();
                                Integer cityid = data.get("id").getAsInt();
                                Constants.cityId = cityid.toString();

                                String url = API.BASE_URL+API.URL_STATION_LIST;
                                RequestParams params = new RequestParams();
                                params.add("cityId", cityid.toString());
                                params.add("pageNo","1");
                                params.add("pageSize","20");

                                AsyncHttpClient client = new AsyncHttpClient();
                                //保存cookie，自动保存到了sharepreferences
                                PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                                client.setCookieStore(myCookieStore);
                                client.post(url, params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                        if(statusCode == 200) {
                                            FragmentTransaction fm = getFragmentManager().beginTransaction();
                                            String json = new String(response);
                                            Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                            JsonArray data = obj.get("data").getAsJsonArray();
                                            final Integer total = obj.get("total").getAsInt();
                                            final String res = data.toString();
                                            station_fragment = new StationFragment().newInstance(res, total);
                                            cityName.setClickable(true);
                                            cityName.setText(city);
                                            Quit.setText("地图列表");
                                            topBar.setText("换电站");
                                            fm.add(R.id.fragment_container,station_fragment);
                                            fm.commit();
                                            Constants.refreshRes = res;
                                            Constants.refreshTotal = total;
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                                        Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                                        //Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                                Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                                //Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },1000);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Constants.city = bdLocation.getCity();
            Constants.province = bdLocation.getProvince();
            Constants.currentLongitude = bdLocation.getLongitude();
            Constants.currentLatitude = bdLocation.getLatitude();
            Constants.currentAddr = bdLocation.getAddrStr();
            String url = API.BASE_URL+API.URL_CITYID;
            RequestParams params = new RequestParams();
            params.add("name",Constants.city);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    String json = new String(response);
                    Log.d(mContext.getPackageName(), "onSuccess json = " + json);
                    JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                    JsonObject data = obj.get("data").getAsJsonObject();
                    Integer cityid = data.get("id").getAsInt();
                    Constants.cityId = cityid.toString();

                    String url = API.BASE_URL+API.URL_STATION_LIST;
                    RequestParams params = new RequestParams();
                    params.add("cityId", cityid.toString());
                    params.add("pageNo","1");
                    params.add("pageSize","20");

                    AsyncHttpClient client = new AsyncHttpClient();
                    //保存cookie，自动保存到了sharepreferences
                    PersistentCookieStore myCookieStore = new PersistentCookieStore(mContext);
                    client.setCookieStore(myCookieStore);
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            if(statusCode == 200) {
                                String json = new String(response);
                                Log.d(mContext.getPackageName(), "onSuccess json = " + json);
                                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                JsonArray data = obj.get("data").getAsJsonArray();
                                final Integer total = obj.get("total").getAsInt();
                                final String res = data.toString();
                                Constants.stationTotal = total;
                                Constants.stationRes = res;
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                            Log.e(mContext.getPackageName(), "Exception = " + e.toString());
                            //Toast.makeText(mContext, "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                        }

                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                    Log.e(mContext.getPackageName(), "Exception = " + e.toString());
                    //Toast.makeText(mContext, "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                }

            });
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @Override
    public boolean onLongClick(View view) {
        //重写
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Constants.change=0;
        mLocationClient.stop();
        //防止附属在activity上的dialog因为finish()而内存溢出
        if(mQuitDialog != null) {
            mQuitDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setDefaultFragment();
        //Constants.change=0;
        //startLocate();
    }

}
