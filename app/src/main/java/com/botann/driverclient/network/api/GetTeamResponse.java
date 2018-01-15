package com.botann.driverclient.network.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.botann.driverclient.model.TeamInfo;
import com.google.gson.JsonNull;
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
public class GetTeamResponse {

    public static void getTeamResponse(final Activity mContext, Integer driverId) {
        String url = API.BASE_URL+API.URL_TEAM_INFO;
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
                JsonObject data = obj.get("data").getAsJsonObject();
                TeamInfo teamInfo = new TeamInfo();
                teamInfo.setName(data.get("name").getAsString());
                teamInfo.setPhone(data.get("phone").getAsString());
                teamInfo.setYesterdayCurrent(data.get("yesterdayCurrent").getAsInt());
                teamInfo.setServerRank(data.get("serverRank").getAsString());
                teamInfo.setTotalCurrent(data.get("totalCurrent").getAsString());
                teamInfo.setDel(data.get("del").getAsInt());
                String teamName = data.get("teamName") != JsonNull.INSTANCE ? data.get("teamName").getAsString() : null;
                teamInfo.setTeamName(teamName);
                Long catchDate = data.get("catchDate") != JsonNull.INSTANCE ? data.get("catchDate").getAsLong() : 0;
                teamInfo.setCatchDate(catchDate/1000);
                teamInfo.setRank(data.get("rank").getAsInt());
                String res = data.toString();
                if(res != null) {
                    SharedPreferences sp = mContext.getSharedPreferences("team",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("name",teamInfo.getName());
                    editor.putString("phone",teamInfo.getPhone());
                    editor.putInt("yesterdayCurrent",teamInfo.getYesterdayCurrent());
                    editor.putString("serverRank",teamInfo.getServerRank());
                    editor.putString("totalCurrent",teamInfo.getTotalCurrent());
                    editor.putString("teamName",teamInfo.getTeamName());
                    editor.putLong("catchDate",teamInfo.getCatchDate());
                    editor.putInt("rank",teamInfo.getRank());
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
