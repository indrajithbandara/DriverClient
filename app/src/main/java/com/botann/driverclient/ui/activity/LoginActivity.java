package com.botann.driverclient.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.botann.driverclient.MainApp;
import com.botann.driverclient.R;
import com.botann.driverclient.model.User;
import com.botann.driverclient.model.UserInfo;
import com.botann.driverclient.network.api.API;
import com.botann.driverclient.network.api.GetConsumeResponse;
import com.botann.driverclient.network.api.GetInfoResponse;
import com.botann.driverclient.network.api.GetLoginResponse;
import com.botann.driverclient.network.api.GetMemberResponse;
import com.botann.driverclient.network.api.GetMessageResponse;
import com.botann.driverclient.network.api.GetRechargeResponse;
import com.botann.driverclient.network.api.GetTeamResponse;
import com.botann.driverclient.network.api.GetVersion;
import com.botann.driverclient.utils.Constants;
import com.botann.driverclient.utils.ToastUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etPassword;
    private Button btnLogin;
    private Context mContext;

    private static LoginActivity mInstance = null;

    public static LoginActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mInstance = this;
        initView();
        GetVersion.getVersion(this);
    }

    private void initView() {
        setContentView(R.layout.login_main);
        etPhone = (EditText) findViewById(R.id.account_username);
        etPassword = (EditText) findViewById(R.id.account_validation);
        btnLogin = (Button) findViewById(R.id.login);
        if(checkForAccount() == 0){
            SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
            etPhone.setText(preferences.getString("phone", ""));
            etPassword.setText(preferences.getString("password", ""));
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                String password = etPassword.getText().toString();
                if(checkCredential(phone, password) == 0) {
                    GetLoginResponse.getLoginResponse(LoginActivity.this, phone, password);
                }else{
                    ToastUtil.showToast(mContext, "用户名和密码不能为空");
                }
            }
        });

    }

    private int checkForAccount() {
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        String token = preferences.getString("token","");
        if(!token.equals("")){
            return 0;
        }
        return 1;
    }

    private void createNewAccount(String phone, String password, User user) {
        Constants.accountId = user.getAccountId();
        Constants.driverId = user.getDriverId();
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();

        editor.putString("phone",phone);
        editor.putString("password",password);
        editor.putInt("accountId",user.getAccountId());
        editor.putInt("driverId",user.getDriverId());
        editor.putString("token",user.getToken());
        editor.commit();
    }

    private void createNewUserInfo(UserInfo userInfo) {
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();

        editor.putString("account",userInfo.getAccount());
        editor.putString("name",userInfo.getName());
        editor.putFloat("balance",userInfo.getBalance());
        editor.putInt("couponNum",userInfo.getCouponNum());
        editor.putInt("messageNum",userInfo.getMessageNum());
        editor.putString("city",userInfo.getCity());
        editor.commit();
    }

    public void doWithLoginResult(String message, User user, boolean status) {
        if(status) {
            if(user.getAccountId() != 0) {
                Constants.isTeam = 0;
                ToastUtil.showToast(mContext, "登录成功");
                // save data
                String url = API.BASE_URL+API.URL_STATION_LIST;
                RequestParams param = new RequestParams();
                param.add("pageNo","1");
                param.add("pageSize","100");

                AsyncHttpClient client2 = new AsyncHttpClient();
                //保存cookie，自动保存到了sharepreferences
                PersistentCookieStore CookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                client2.setCookieStore(CookieStore);
                client2.post(url, param, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        if(statusCode == 200) {
                            String json = new String(response);
                            Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                            JsonArray data = obj.get("data").getAsJsonArray();
                            final Integer total = obj.get("total").getAsInt();
                            final String res = data.toString();
                            Constants.stationT = total;
                            Constants.stationR = res;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                        Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                        //Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                    }

                });
                GetInfoResponse.getInfoResponse(LoginActivity.this, user.getAccountId().toString());
                GetConsumeResponse.getConsumeResponse(LoginActivity.this, user.getAccountId(),1,20);
                GetRechargeResponse.getRechargeResponse(LoginActivity.this, user.getAccountId(),1,20);
                GetMessageResponse.getPostMessage(LoginActivity.this, user.getAccountId(),1,20);
                //GetTeamResponse.getTeamResponse(LoginActivity.this, user.getDriverId());
                //GetMemberResponse.getMemberResponse(LoginActivity.this, user.getDriverId());
                createNewAccount(etPhone.getText().toString(), etPassword.getText().toString(), user);
                Intent toMain = new Intent(mContext, MainActivity.class);
                toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toMain);
            }else{
                Constants.isTeam = 1;
                ToastUtil.showToast(mContext, "登录成功");
                // save data
                String url = API.BASE_URL+API.URL_STATION_LIST;
                RequestParams param = new RequestParams();
                param.add("pageNo","1");
                param.add("pageSize","100");

                AsyncHttpClient client2 = new AsyncHttpClient();
                //保存cookie，自动保存到了sharepreferences
                PersistentCookieStore CookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                client2.setCookieStore(CookieStore);
                client2.post(url, param, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        if(statusCode == 200) {
                            String json = new String(response);
                            Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                            JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                            JsonArray data = obj.get("data").getAsJsonArray();
                            final Integer total = obj.get("total").getAsInt();
                            final String res = data.toString();
                            Constants.stationT = total;
                            Constants.stationR = res;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                        Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                        //Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                    }

                });
                //GetInfoResponse.getInfoResponse(LoginActivity.this, user.getAccountId().toString());
                //GetConsumeResponse.getConsumeResponse(LoginActivity.this, user.getAccountId(),1,20);
                //GetRechargeResponse.getRechargeResponse(LoginActivity.this, user.getAccountId(),1,20);
                //GetMessageResponse.getPostMessage(LoginActivity.this, user.getAccountId(),1,20);
                GetTeamResponse.getTeamResponse(LoginActivity.this, user.getDriverId());
                GetMemberResponse.getMemberResponse(LoginActivity.this, user.getDriverId());
                createNewAccount(etPhone.getText().toString(), etPassword.getText().toString(), user);
                Intent toMain = new Intent(mContext, MainActivity.class);
                toMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toMain);
            }
        }else{
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void doWithUserInfoResult(String message, UserInfo userInfo, boolean status) {
        if(status) {
            createNewUserInfo(userInfo);
        }else{
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void doWithConsumeRecordsResult(String message, String res, boolean status) {
        if(status) {
            //createConsumeInfoList(res);
        }else{
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void doWithRechargeRecordsResult(String message, String res, boolean status) {
        if(status) {
            //createRechargeInfoList(res);
        }else{
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    private int checkCredential(String phone, String password) {
        if(!phone.isEmpty() && !password.isEmpty()) {
            return 0;
        }
        return 1;
    }

    /**
     * 实现点击空白处，软键盘消失事件
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
