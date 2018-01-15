package com.botann.driverclient.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botann.driverclient.MainApp;
import com.botann.driverclient.QrcodeDialog;
import com.botann.driverclient.R;
import com.botann.driverclient.adapter.BaseAdapter;
import com.botann.driverclient.adapter.ILoadCallback;
import com.botann.driverclient.adapter.MemberInfoAdapter;
import com.botann.driverclient.adapter.MyMemberInfoAdapter;
import com.botann.driverclient.adapter.OnLoad;
import com.botann.driverclient.model.MemberInfo;
import com.botann.driverclient.network.api.API;
import com.botann.driverclient.utils.Constants;
import com.botann.driverclient.utils.EncryptionUtil;
import com.botann.driverclient.utils.QRCodeUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.apache.http.Header;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Orion on 2017/9/21.
 */
public class TeamFragment extends Fragment implements View.OnClickListener {
    private static SimpleDateFormat format =  new SimpleDateFormat("MM-dd");
    private String name;
    private String teamName;
    private Integer yesterdayCurrent;
    private String serverRank;
    private String totalCurrent;
    private Long catchDate;
    private Integer rank;

    private TextView mChiefName;
    private TextView mTeamName;
    private TextView mYesterdayCurrent;
    private TextView mServerRank;
    private TextView mTotalCurrent;
    private TextView mChief;
    private TextView mQrcode;

    RefreshLayout teamRefresh;
    RecyclerView memberInfo;
    BaseAdapter mAdapter;
    int loadCount;
    int totalRecords;

    //成员信息数据
    private List<MemberInfo> memberInfoList = new ArrayList<MemberInfo>();

    public static TeamFragment newInstance(String name, String teamName, Integer yesterdayCurrent, String serverRank, String totalCurrent, Long catchDate, Integer rank, String memberInfoList) {
        TeamFragment fragment = new TeamFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("teamName", teamName);
        bundle.putInt("yesterdayCurrent", yesterdayCurrent);
        bundle.putString("serverRank", serverRank);
        bundle.putString("totalCurrent", totalCurrent);
        bundle.putLong("catchDate", catchDate);
        bundle.putInt("rank", rank);
        bundle.putString("memberInfoList",memberInfoList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        name = getArguments().getString("name");
        teamName = getArguments().getString("teamName");
        yesterdayCurrent = getArguments().getInt("yesterdayCurrent");
        serverRank = getArguments().getString("serverRank");
        totalCurrent = getArguments().getString("totalCurrent");
        catchDate = getArguments().getLong("catchDate");
        rank = getArguments().getInt("rank");
        Gson gson = new Gson();
        memberInfoList = gson.fromJson(getArguments().getString("memberInfoList"),new TypeToken<List<MemberInfo>>(){}.getType());
        totalRecords = memberInfoList.size();

        //创建被装饰者实例
        final MyMemberInfoAdapter adapter = new MyMemberInfoAdapter(getActivity());
        //创建装饰者实例，并传入被装饰者和回调接口
        mAdapter = new MemberInfoAdapter(adapter, new OnLoad() {
            @Override
            public void load(int pagePosition, int pageSize, final ILoadCallback callback) {
                //此处模拟做网络操作，0.5s延迟，将拉取的数据更新到adapter中
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<MemberInfo> dataSet = memberInfoList;
                        //数据的处理最终还是交给被装饰的adapter来处理
                        adapter.appendData(dataSet);
                        callback.onSuccess();
                        //模拟加载到没有更多数据的情况，触发onFailure
                        if(loadCount++==(totalRecords/20)){
                            callback.onFailure();
                        }
                    }
                },500);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.team_fragment,container,false);
        mChiefName = (TextView) view.findViewById(R.id.chief_name);
        mTeamName = (TextView) view.findViewById(R.id.team_name);
        mYesterdayCurrent = (TextView) view.findViewById(R.id.yesterday_current);
        mServerRank = (TextView) view.findViewById(R.id.server_rank);
        mTotalCurrent = (TextView) view.findViewById(R.id.total_current);
        mChief = (TextView) view.findViewById(R.id.chief);
        mQrcode = (TextView) view.findViewById(R.id.team_qrcode);
        teamRefresh = (RefreshLayout) view.findViewById(R.id.team_swipe_refresh);
        memberInfo = (RecyclerView) view.findViewById(R.id.team_list);

        mChiefName.setText(name);
        if(rank <= 0) {
            if(teamName!=null){
                mTeamName.setText(teamName);
            }else{
                mTeamName.setText("我还没有团队");
            }
            mChief.setText("");
            mTeamName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2.3f));
            mChief.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0));
            mQrcode.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1.7f));
            mQrcode.setOnClickListener(this);
        }else{
            mTeamName.setText(teamName);
            mChief.setText("队长");
            mTeamName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1.3f));
            mChief.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1.0f));
            mQrcode.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1.7f));
            mQrcode.setOnClickListener(this);
        }
        if(catchDate!=0){
            if(yesterdayCurrent%100>=50) {
                mYesterdayCurrent.setText((yesterdayCurrent/100+1)+"元/"+format.format(catchDate));
            }else{
                mYesterdayCurrent.setText(yesterdayCurrent/100+"元/"+format.format(catchDate));
            }
        }else{
            if(yesterdayCurrent%100>=50) {
                mYesterdayCurrent.setText((yesterdayCurrent/100+1)+"元");
            }else{
                mYesterdayCurrent.setText(yesterdayCurrent/100+"元");
            }
        }
        mServerRank.setText(serverRank);
        mTotalCurrent.setText(totalCurrent);
        teamRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = API.BASE_URL+API.URL_TEAM_INFO;
                        RequestParams params = new RequestParams();
                        params.add("driverId", Constants.driverId.toString());

                        final AsyncHttpClient client = new AsyncHttpClient();
                        //保存cookie，自动保存到了sharepreferences
                        PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                        client.setCookieStore(myCookieStore);
                        client.get(url, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                String json = new String(response);
                                Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                JsonObject data = obj.get("data").getAsJsonObject();
                                String tName = data.get("teamName") != JsonNull.INSTANCE ? data.get("teamName").getAsString() : null;
                                Long cDate = data.get("catchDate") != JsonNull.INSTANCE ? data.get("catchDate").getAsLong() : 0;
                                //团队司机信息刷新完成
                                SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("team",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                editor.putString("name",data.get("name").getAsString());
                                editor.putString("phone",data.get("phone").getAsString());
                                editor.putInt("yesterdayCurrent",data.get("yesterdayCurrent").getAsInt());
                                editor.putString("serverRank",data.get("serverRank").getAsString());
                                editor.putString("totalCurrent",data.get("totalCurrent").getAsString());
                                editor.putString("teamName",tName);
                                editor.putLong("catchDate",cDate);
                                editor.putInt("rank", data.get("rank").getAsInt());
                                editor.commit();

                                name = data.get("name").getAsString();
                                teamName = data.get("teamName") != JsonNull.INSTANCE ? data.get("teamName").getAsString() : null;
                                yesterdayCurrent = data.get("yesterdayCurrent").getAsInt();
                                serverRank = data.get("serverRank").getAsString();
                                totalCurrent = data.get("totalCurrent").getAsString();
                                catchDate = data.get("catchDate") != JsonNull.INSTANCE ? data.get("catchDate").getAsLong() : 0;
                                rank = data.get("rank").getAsInt();

                                String url = API.BASE_URL+API.URL_MEMBER_INFO;
                                RequestParams params = new RequestParams();
                                params.add("driverId",Constants.driverId.toString());

                                final AsyncHttpClient client = new AsyncHttpClient();
                                //保存cookie，自动保存到了sharepreferences
                                PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                                client.setCookieStore(myCookieStore);
                                client.get(url, params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                        String json = new String(response);
                                        Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                        JsonArray data = obj.get("data").getAsJsonArray();
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();

                                        //刷新完成
                                        String res = data.toString();
                                        TeamFragment TF = new TeamFragment().newInstance(name,teamName,yesterdayCurrent,serverRank,totalCurrent,catchDate,rank,res);
                                        transaction.replace(R.id.fragment_container,TF);
                                        transaction.commit();
                                        if(res != null) {
                                            SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("member",MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sp.edit();

                                            editor.putString("memberInfoList",res);
                                            editor.commit();
                                        }
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
                    }
                }, 500);
                teamRefresh.finishRefresh(500);
            }
        });
        memberInfo.setAdapter(mAdapter);
        memberInfo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.team_qrcode:
                Bitmap qrBitmap = null;
                try {
                    qrBitmap = QRCodeUtil.createQRCode("http://www.botann.com:8085/carteam/driver/toRegister?iviteId="+Constants.driverId+"&data="+ EncryptionUtil.Encrypt("{\"iviteId\":\"" + Constants.driverId + "\"}"), 600, 600);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                QrcodeDialog.Builder dialogBuild = new QrcodeDialog.Builder(this.getActivity());
                dialogBuild.setImage(qrBitmap);
                QrcodeDialog dialog = dialogBuild.create();
                dialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
                dialog.show();
                break;
        }
    }
}
