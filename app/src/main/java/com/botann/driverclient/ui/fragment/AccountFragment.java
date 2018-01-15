package com.botann.driverclient.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.botann.driverclient.MainApp;
import com.botann.driverclient.R;
import com.botann.driverclient.network.api.API;
import com.botann.driverclient.ui.activity.CouponActivity;
import com.botann.driverclient.ui.activity.MessageActivity;
import com.botann.driverclient.utils.Constants;
import com.botann.driverclient.utils.QRCodeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.apache.http.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Orion on 2017/7/13.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {
    private String account;
    private String name;
    private Float balance;
    private Integer couponNum;
    private Integer messageNum;

    private View rootView;//缓存Fragment view

    private TextView mUserName;
    private TextView mUserAccount;
    private TextView mLeftMoney;
    private TextView mCouponNum;
    private TextView mMessageNum;
    private TextView mCouponLargeNum;
    private TextView mMessageLargeNum;
    private TextView mCoupon;
    private TextView mMessage;
    private ImageView mQRCodeView;
    private RefreshLayout accountRefresh;

    public static AccountFragment newInstance(String account, String name, Float balance, Integer couponNum, Integer messageNum) {
        AccountFragment fragment = new AccountFragment();
        Bundle bundle = new Bundle();
        bundle.putString("account", account);
        bundle.putString("name", name);
        bundle.putFloat("balance", balance);
        bundle.putInt("couponNum", couponNum);
        bundle.putInt("messageNum", messageNum);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        account = getArguments().getString("account");
        name = getArguments().getString("name");
        balance = getArguments().getFloat("balance");
        couponNum = getArguments().getInt("couponNum");
        messageNum = getArguments().getInt("messageNum");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment,container,false);
        mUserName = (TextView) view.findViewById(R.id.username);
        mUserAccount = (TextView) view.findViewById(R.id.user_account);
        mLeftMoney = (TextView) view.findViewById(R.id.left_money);
        mCouponNum = (TextView) view.findViewById(R.id.couponNum);
        mMessageNum = (TextView) view.findViewById(R.id.messageNum);
        mCouponLargeNum = (TextView) view.findViewById(R.id.coupon_largeNum);
        mMessageLargeNum = (TextView) view.findViewById(R.id.message_largeNum);
        mCoupon = (TextView) view.findViewById(R.id.coupon);
        mMessage = (TextView) view.findViewById(R.id.message);
        mQRCodeView = (ImageView) view.findViewById(R.id.iv);
        accountRefresh = (RefreshLayout) view.findViewById(R.id.account_refreshLayout);

        if(name.length() > 3) {
            mUserName.setTextSize(16);
        }
        mUserName.setText(name);
        mUserAccount.setText(account);
        mLeftMoney.setText("账户余额："+balance+"元");
        if(couponNum == 0) {
            mCouponNum.setText("");
            mCouponNum.setVisibility(View.INVISIBLE);
            mCouponLargeNum.setVisibility(View.INVISIBLE);
        }else if(couponNum > 9) {
            mCouponLargeNum.setText(couponNum.toString());
            mCouponLargeNum.setVisibility(View.VISIBLE);
            mCouponNum.setVisibility(View.INVISIBLE);
        }else{
            mCouponNum.setText(couponNum.toString());
            mCouponNum.setVisibility(View.VISIBLE);
            mCouponLargeNum.setVisibility(View.INVISIBLE);
        }
        if(messageNum == 0) {
            mMessageNum.setText("");
            mMessageNum.setVisibility(View.INVISIBLE);
            mMessageLargeNum.setVisibility(View.INVISIBLE);
        }else if(messageNum > 9) {
            mMessageLargeNum.setText(messageNum.toString());
            mMessageLargeNum.setVisibility(View.VISIBLE);
            mMessageNum.setVisibility(View.INVISIBLE);
        }else{
            mMessageNum.setText(messageNum.toString());
            mMessageNum.setVisibility(View.VISIBLE);
            mMessageLargeNum.setVisibility(View.INVISIBLE);
        }
        Bitmap qrBitmap = QRCodeUtil.createQRCode(account, 500, 500);
        mQRCodeView.setImageBitmap(qrBitmap);
        mCoupon.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        accountRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
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
                                String json = new String(response);
                                Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                JsonObject data = obj.get("data").getAsJsonObject();
                                mUserName.setText(data.get("name").getAsString());
                                mUserAccount.setText(data.get("account").getAsString());
                                mLeftMoney.setText("账户余额："+data.get("balance").getAsFloat()/100+"元");
                                Integer couponNum = data.get("couponNum").getAsInt();
                                Integer messageNum = data.get("messageNum").getAsInt();
                                if(couponNum == 0) {
                                    mCouponNum.setText("");
                                    mCouponNum.setVisibility(View.INVISIBLE);
                                    mCouponLargeNum.setVisibility(View.INVISIBLE);
                                }else if(couponNum > 9) {
                                    mCouponLargeNum.setText(couponNum.toString());
                                    mCouponLargeNum.setVisibility(View.VISIBLE);
                                    mCouponNum.setVisibility(View.INVISIBLE);
                                }else{
                                    mCouponNum.setText(couponNum.toString());
                                    mCouponNum.setVisibility(View.VISIBLE);
                                    mCouponLargeNum.setVisibility(View.INVISIBLE);
                                }
                                if(messageNum == 0) {
                                    mMessageNum.setText("");
                                    mMessageNum.setVisibility(View.INVISIBLE);
                                    mMessageLargeNum.setVisibility(View.INVISIBLE);
                                }else if(messageNum > 9) {
                                    mMessageLargeNum.setText(messageNum.toString());
                                    mMessageLargeNum.setVisibility(View.VISIBLE);
                                    mMessageNum.setVisibility(View.INVISIBLE);
                                }else{
                                    mMessageNum.setText(messageNum.toString());
                                    mMessageNum.setVisibility(View.VISIBLE);
                                    mMessageLargeNum.setVisibility(View.INVISIBLE);
                                }
                                Bitmap qrBitmap = QRCodeUtil.createQRCode(data.get("account").getAsString(), 500, 500);
                                mQRCodeView.setImageBitmap(qrBitmap);

                                //刷新完成
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
                                Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },500);
                accountRefresh.finishRefresh(500);
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coupon:
                String url = API.BASE_URL+API.URL_COUPON_LIST;
                RequestParams params = new RequestParams();
                params.add("accountId",Constants.accountId.toString());
                params.add("pageNo","1");
                params.add("pageSize","20");

                final AsyncHttpClient client = new AsyncHttpClient();
                //保存cookie，自动保存到了sharepreferences
                PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                client.setCookieStore(myCookieStore);
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        String json = new String(response);
                        Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                        JsonArray data = obj.get("data").getAsJsonArray();
                        Integer total = obj.get("total").getAsInt();
                        String res = data.toString();
                        if(res != null) {
                            SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("coupon",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("couponInfoList",res);
                            editor.putInt("total",total);
                            editor.commit();

                            Constants.couponRes = res;
                            Constants.couponTotal = total;
                            Intent intent = new Intent(MainApp.getInstance().getApplicationContext(),CouponActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainApp.getInstance().getApplicationContext().startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                        Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                        Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.message:
                String Url = API.BASE_URL+API.URL_SYSTEM_MESSAGE;
                RequestParams Params = new RequestParams();
                Params.add("accountId",Constants.accountId.toString());
                Params.add("pageNo","1");
                Params.add("pageSize","20");

                final AsyncHttpClient Client = new AsyncHttpClient();
                //保存cookie，自动保存到了sharepreferences
                PersistentCookieStore MyCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                Client.setCookieStore(MyCookieStore);
                Client.post(Url, Params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        String json = new String(response);
                        Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                        JsonArray data = obj.get("data").getAsJsonArray();
                        Integer total = obj.get("total").getAsInt();
                        String res = data.toString();
                        SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("message",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();

                        editor.putString("systemMessageList",res);
                        editor.putInt("system_total",total);
                        editor.commit();

                        Constants.messageRes = res;
                        Constants.messageTotal = total;
                        String Url = API.BASE_URL+API.URL_POST_MESSAGE;
                        RequestParams Params = new RequestParams();
                        Params.add("accountId",Constants.accountId.toString());
                        Params.add("pageNo","1");
                        Params.add("pageSize","20");

                        final AsyncHttpClient Client = new AsyncHttpClient();
                        //保存cookie，自动保存到了sharepreferences
                        PersistentCookieStore MyCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                        Client.setCookieStore(MyCookieStore);
                        Client.post(Url, Params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                String json = new String(response);
                                Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                JsonArray data = obj.get("data").getAsJsonArray();
                                Integer total = obj.get("total").getAsInt();
                                String res = data.toString();
                                SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("message",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                editor.putString("postMessageList",res);
                                editor.putInt("post_total",total);
                                editor.commit();

                                Constants.postMessageRes = res;
                                Constants.postMessageTotal = total;
                                Intent message = new Intent(MainApp.getInstance().getApplicationContext(),MessageActivity.class);
                                message.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MainApp.getInstance().getApplicationContext().startActivity(message);
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
                break;
        }
    }

}
