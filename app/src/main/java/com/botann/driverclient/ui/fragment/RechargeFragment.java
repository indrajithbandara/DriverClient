package com.botann.driverclient.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.botann.driverclient.MainApp;
import com.botann.driverclient.R;
import com.botann.driverclient.adapter.BaseAdapter;
import com.botann.driverclient.adapter.ILoadCallback;
import com.botann.driverclient.adapter.MyRechargeInfoAdapter;
import com.botann.driverclient.adapter.OnLoad;
import com.botann.driverclient.adapter.RechargeInfoAdapter;
import com.botann.driverclient.model.RechargeInfo;
import com.botann.driverclient.network.api.API;
import com.botann.driverclient.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Orion on 2017/7/24.
 */
public class RechargeFragment extends Fragment {
    RefreshLayout rechargeRefresh;
    RecyclerView rechargeInfo;
    BaseAdapter mAdapter;
    int loadCount;
    int totalRecords;

    //充值记录数据
    private List<RechargeInfo> rechargeInfoList = new ArrayList<RechargeInfo>();

    public static RechargeFragment newInstance(String rechargeInfoList, Integer total) {
        RechargeFragment fragment = new RechargeFragment();
        Bundle bundle = new Bundle();

        bundle.putString("rechargeInfoList",rechargeInfoList);
        bundle.putInt("total",total);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        Gson gson = new Gson();
        rechargeInfoList = gson.fromJson(getArguments().getString("rechargeInfoList"),new TypeToken<List<RechargeInfo>>(){}.getType());
        totalRecords = getArguments().getInt("total");

        //创建被装饰者实例
        final MyRechargeInfoAdapter adapter = new MyRechargeInfoAdapter(getActivity());
        //创建装饰者实例，并传入被装饰者和回调接口
        mAdapter = new RechargeInfoAdapter(adapter, new OnLoad() {
            @Override
            public void load(int pagePosition, int pageSize, final ILoadCallback callback) {
                //此处模拟做网络操作，0.5s延迟，将拉取的数据更新到adapter中
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<RechargeInfo> dataSet = rechargeInfoList;
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
        View view = inflater.inflate(R.layout.recharge_fragment, container, false);
        rechargeRefresh = (RefreshLayout) view.findViewById(R.id.recharge_swipe_refresh);
        rechargeInfo = (RecyclerView) view.findViewById(R.id.recharge_list);
        rechargeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final List<RechargeInfo> dataSet = new ArrayList<RechargeInfo>();
                        String url = API.BASE_URL+API.URL_RECHARGE_LIST;
                        RequestParams params = new RequestParams();
                        params.add("accountId", Constants.accountId.toString());
                        params.add("pageNo","1");
                        params.add("pageSize","20");

                        final AsyncHttpClient client = new AsyncHttpClient();
                        //保存cookie，自动保存到了sharepreferences
                        PersistentCookieStore myCookieStore = new PersistentCookieStore(MainApp.getInstance().getApplicationContext());
                        client.setCookieStore(myCookieStore);
                        client.post(url, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                                String json = new String(response);
                                Integer total;
                                String res;
                                Log.d(MainApp.getInstance().getApplicationContext().getPackageName(), "onSuccess json = " + json);
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                //刷新完成
                                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                                Integer code = obj.get("code").getAsInt();
                                if(code == 0) {
                                    JsonArray data = obj.get("data").getAsJsonArray();
                                    total = obj.get("total").getAsInt();
                                    res = data.toString();
                                }else{
                                    total = 0;
                                    res = null;
                                }
                                RechargeFragment RF = new RechargeFragment().newInstance(res,total);
                                transaction.replace(R.id.fragment_container,RF);
                                transaction.commit();
                                if(res != null) {
                                    SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("recharge",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();

                                    editor.putString("rechargeInfoList",res);
                                    editor.putInt("total",total);
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
                }, 500);
                rechargeRefresh.finishRefresh(500);
            }
        });
        rechargeInfo.setAdapter(mAdapter);
        rechargeInfo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

}
