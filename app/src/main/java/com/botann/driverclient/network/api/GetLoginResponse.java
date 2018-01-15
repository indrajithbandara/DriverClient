package com.botann.driverclient.network.api;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.botann.driverclient.model.User;
import com.botann.driverclient.ui.activity.LoginActivity;
import com.botann.driverclient.utils.ToastUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by Orion on 2017/7/11.
 */
public class GetLoginResponse {

    public static void getLoginResponse(final Activity mContext, String phone, String password) {
        String url = API.BASE_URL+API.URL_LOGIN;
        RequestParams params = new RequestParams();
        params.add("phone",phone);
        params.add("password",password);

        final AsyncHttpClient client = new AsyncHttpClient();
        //保存cookie，自动保存到了sharepreferences
        PersistentCookieStore myCookieStore = new PersistentCookieStore(mContext);
        client.setCookieStore(myCookieStore);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try{
                    String json = new String(response);
                    Log.d(mContext.getPackageName(), "onSuccess json = " + json);
                    JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                    JsonObject data = obj.get("data").getAsJsonObject();
                    String token = data.get("token").getAsString();
                    Integer accountId = data.get("accountId").getAsInt();
                    Integer driverId = data.get("driverId").getAsInt();
                    Integer code = obj.get("code").getAsInt();
                    User user = new User();
                    if(data != null) {
                        user.setToken(token);
                        user.setAccountId(accountId);
                        user.setDriverId(driverId);
                        if(code == 0){
                            LoginActivity.getInstance().doWithLoginResult(obj.get("msg").getAsString(), user, true);
                        }else{
                            LoginActivity.getInstance().doWithLoginResult(obj.get("msg").getAsString(), user, false);
                        }
                    }else{
                        LoginActivity.getInstance().doWithLoginResult(obj.get("msg").getAsString(), user, false);
                    }
                }catch(Exception e) {
                    String error = new String(response);
                    JsonObject errorObj = new JsonParser().parse(error).getAsJsonObject();
                    ToastUtil.showToast(mContext, errorObj.get("msg").getAsString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                Log.e(mContext.getPackageName(), "Exception = " + e.toString());
                Toast.makeText(mContext, "连接到服务器失败！", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
