package com.botann.driverclient.network.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.botann.driverclient.utils.Constants;
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
 * Created by Orion on 2017/8/3.
 */
public class GetCouponResponse {

    public static void getCouponResponse(final Activity mContext, Integer accountId, Integer pageNo, Integer pageSize) {
        String url = API.BASE_URL+API.URL_COUPON_LIST;
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
                if(res != null) {
                    SharedPreferences sp = mContext.getSharedPreferences("coupon",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("couponInfoList",res);
                    editor.putInt("total",total);
                    editor.commit();

                    Constants.couponRes = res;
                    Constants.couponTotal = total;
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
