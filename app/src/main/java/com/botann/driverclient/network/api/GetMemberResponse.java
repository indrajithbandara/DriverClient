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
 * Created by Orion on 2017/9/20.
 */
public class GetMemberResponse {

    public static void getMemberResponse(final Activity mContext, Integer driverId) {
        String url = API.BASE_URL+API.URL_MEMBER_INFO;
        RequestParams params = new RequestParams();
        params.add("driverId",driverId.toString());

        final AsyncHttpClient client = new AsyncHttpClient();
        //保存cookie，自动保存到了sharepreferences
        PersistentCookieStore myCookieStore = new PersistentCookieStore(mContext);
        client.setCookieStore(myCookieStore);
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String json = new String(response);
                Log.d(mContext.getPackageName(), "onSuccess json = " + json);
                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                JsonArray data = obj.get("data").getAsJsonArray();
                String res = data.toString();
                if(res != null) {
                    SharedPreferences sp = mContext.getSharedPreferences("member",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("memberInfoList",res);
                    editor.commit();
                }else{
                    Toast.makeText(mContext, obj.get("msg").getAsString(), Toast.LENGTH_SHORT).show();
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
