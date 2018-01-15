package com.botann.driverclient.network.api;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.botann.driverclient.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by Orion on 2017/7/26.
 */
public class GetCityIdResponse {

    public static void getCityIdResponse(final Activity mContext, String name) {
        String url = API.BASE_URL+API.URL_CITYID;
        RequestParams params = new RequestParams();
        params.add("name",name);

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
                Integer cityid = data.get("id").getAsInt();
                Constants.cityId = cityid.toString();
                GetStationResponse.getStationResponse(mContext, 1, 20);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                Log.e(mContext.getPackageName(), "Exception = " + e.toString());
                Toast.makeText(mContext, "连接到服务器失败！", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
