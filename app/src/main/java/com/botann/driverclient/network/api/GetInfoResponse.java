package com.botann.driverclient.network.api;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.botann.driverclient.model.UserInfo;
import com.botann.driverclient.ui.activity.LoginActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by Orion on 2017/7/17.
 */

public class GetInfoResponse {

    public static void getInfoResponse(final Activity mContext, String accountId) {
        String url = API.BASE_URL+API.URL_INFO;
        RequestParams params = new RequestParams();
        params.add("accountId",accountId);

        final AsyncHttpClient client = new AsyncHttpClient();
        //保存cookie，自动保存到了sharepreferences
        PersistentCookieStore myCookieStore = new PersistentCookieStore(mContext);
        client.setCookieStore(myCookieStore);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String json = new String(response);
                Log.d(mContext.getPackageName(), "onSuccess json = " + json);
                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                JsonObject data = obj.get("data").getAsJsonObject();
                UserInfo userInfo = new UserInfo();
                userInfo.setAccount(data.get("account").getAsString());
                userInfo.setName(data.get("name").getAsString());
                userInfo.setBalance(data.get("balance").getAsFloat()/100);
                userInfo.setCouponNum(data.get("couponNum").getAsInt());
                userInfo.setMessageNum(data.get("messageNum").getAsInt());
                userInfo.setCity(data.get("city").getAsString());
                if(userInfo != null) {
                    LoginActivity.getInstance().doWithUserInfoResult(obj.get("msg").getAsString(), userInfo, true);
                }else{
                    LoginActivity.getInstance().doWithUserInfoResult(obj.get("msg").getAsString(), userInfo, false);
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
