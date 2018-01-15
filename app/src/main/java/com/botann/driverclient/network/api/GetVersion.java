package com.botann.driverclient.network.api;

import android.content.Context;
import android.util.Log;

import com.botann.driverclient.UpdateManager;
import com.botann.driverclient.utils.ToastUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.Header;

/**
 * Created by wcc on 2016/9/10.
 */
public class GetVersion {

    public static void getVersion(final Context mContext) {
        String url = API.BASE_URL+API.URL_VERSION_INFO;

        final AsyncHttpClient client = new AsyncHttpClient();
        //保存cookie，自动保存到了sharepreferences
        PersistentCookieStore myCookieStore = new PersistentCookieStore(mContext);
        client.setCookieStore(myCookieStore);
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String json = new String(response);
                Log.d(mContext.getPackageName(), "onSuccess json = " + json);
                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                JsonObject data = obj.get("data").getAsJsonObject();
                Integer versionCode = data.get("versionCode").getAsInt();
                String versionName = data.get("versionName").getAsString();
                String apkPath = data.get("apkPath").getAsString();

                UpdateManager manager = new UpdateManager(mContext, versionCode, versionName,
                        apkPath);
                manager.checkUpdate();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                System.out.println("onFailure");
                Log.e(mContext.getPackageName(), "Exception = " + e.toString());
                ToastUtil.showToast(mContext, "检查更新失败, 无法访问服务器");
            }

        });

    }

}
