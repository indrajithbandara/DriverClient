package com.botann.driverclient.network.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Orion on 2017/8/7.
 */
public class GetMessageResponse {

    public static void getSystemMessage(final Activity mContext, Integer accountId, Integer pageNo, Integer pageSize) {
        String url = API.BASE_URL+API.URL_SYSTEM_MESSAGE;
        RequestParams params = new RequestParams();
        params.add("accountId",accountId.toString());
        params.add("pageNo",pageNo.toString());
        params.add("pageSize",pageSize.toString());

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
                JsonArray data = obj.get("data").getAsJsonArray();
                Integer total = obj.get("total").getAsInt();
                String res = data.toString();
                SharedPreferences sp = mContext.getSharedPreferences("message",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("systemMessageList",res);
                editor.putInt("system_total",total);
                editor.commit();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                Log.e(mContext.getPackageName(), "Exception = " + e.toString());
                Toast.makeText(mContext, "连接到服务器失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getPostMessage(final Activity mContext, Integer accountId, Integer pageNo, Integer pageSize) {
        String url = API.BASE_URL+API.URL_POST_MESSAGE;
        RequestParams params = new RequestParams();
        params.add("accountId",accountId.toString());
        params.add("pageNo",pageNo.toString());
        params.add("pageSize",pageSize.toString());

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
                JsonArray data = obj.get("data").getAsJsonArray();
                Integer total = obj.get("total").getAsInt();
                String res = data.toString();
                SharedPreferences sp = mContext.getSharedPreferences("message",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                editor.putString("postMessageList",res);
                editor.putInt("post_total",total);
                editor.commit();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                Log.e(mContext.getPackageName(), "Exception = " + e.toString());
                Toast.makeText(mContext, "连接到服务器失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
