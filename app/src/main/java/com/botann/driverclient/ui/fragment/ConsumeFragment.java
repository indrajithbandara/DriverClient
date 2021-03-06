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
import com.botann.driverclient.adapter.ConsumeInfoAdapter;
import com.botann.driverclient.adapter.ILoadCallback;
import com.botann.driverclient.adapter.MyConsumeInfoAdapter;
import com.botann.driverclient.adapter.OnLoad;
import com.botann.driverclient.model.ConsumeInfo;
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
 * Created by Orion on 2017/7/18.
 */
public class ConsumeFragment extends Fragment {
    RefreshLayout consumeRefresh;
    RecyclerView consumeInfo;
    BaseAdapter mAdapter;
    RecyclerView.ViewHolder holder;
    private List<ConsumeInfo> dataSet = new ArrayList<ConsumeInfo>();
    int loadCount;
    int totalRecords;

    //消费记录数据
    private List<ConsumeInfo> consumeInfoList = new ArrayList<ConsumeInfo>();

    public static ConsumeFragment newInstance(String consumeInfoList, Integer total) {
        ConsumeFragment fragment = new ConsumeFragment();
        Bundle bundle = new Bundle();

        bundle.putString("consumeInfoList",consumeInfoList);
        bundle.putInt("total",total);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Gson gson = new Gson();
        consumeInfoList = gson.fromJson(getArguments().getString("consumeInfoList"),new TypeToken<List<ConsumeInfo>>(){}.getType());
        totalRecords = getArguments().getInt("total");

        //创建被装饰者实例
        final MyConsumeInfoAdapter adapter = new MyConsumeInfoAdapter(getActivity());
        //创建装饰者实例，并传入被装饰者和回调接口
        mAdapter = new ConsumeInfoAdapter(adapter, new OnLoad() {
            @Override
            public void load(int pagePosition, int pageSize, final ILoadCallback callback) {
                //此处模拟做网络操作，0.5s延迟，将拉取的数据更新到adapter中
                new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<ConsumeInfo> dataSet = consumeInfoList;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.consume_fragment, container, false);
        consumeRefresh = (RefreshLayout) view.findViewById(R.id.consume_swipe_refresh);
        consumeInfo = (RecyclerView) view.findViewById(R.id.consume_list);
        consumeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = API.BASE_URL+API.URL_EXCHANGE_LIST;
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
                                ConsumeFragment cf = new ConsumeFragment().newInstance(res,total);
                                transaction.replace(R.id.fragment_container,cf);
                                transaction.commit();
                                if(res != null) {
                                    SharedPreferences sp = MainApp.getInstance().getApplicationContext().getSharedPreferences("consume",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();

                                    editor.putString("consumeInfoList",res);
                                    editor.putInt("total",total);
                                    editor.commit();
                                }
                                consumeRefresh.finishRefresh(500);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
                                Log.e(MainApp.getInstance().getApplicationContext().getPackageName(), "Exception = " + e.toString());
                                Toast.makeText(MainApp.getInstance().getApplicationContext(), "连接到服务器失败！", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                },500);
            }
        });
        consumeInfo.setAdapter(mAdapter);
        consumeInfo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }
}
