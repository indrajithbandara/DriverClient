package com.botann.driverclient.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.botann.driverclient.R;
import com.botann.driverclient.adapter.BaseAdapter;
import com.botann.driverclient.adapter.ILoadCallback;
import com.botann.driverclient.adapter.MyStationInfoAdapter;
import com.botann.driverclient.adapter.OnLoad;
import com.botann.driverclient.adapter.StationInfoAdapter;
import com.botann.driverclient.model.StationInfo;
import com.botann.driverclient.utils.Constants;
import com.botann.driverclient.utils.sortList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Orion on 2017/7/26.
 */
public class StationFragment extends Fragment {
    RefreshLayout stationRefresh;
    RecyclerView stationInfo;
    BaseAdapter mAdapter;
    int loadCount;
    int totalRecords;

    //换电站数据
    private List<StationInfo> stationInfoList = new ArrayList<StationInfo>();

    public static StationFragment newInstance(String stationInfoList, Integer total) {
        StationFragment fragment = new StationFragment();
        Bundle bundle = new Bundle();

        bundle.putString("stationInfoList",stationInfoList);
        bundle.putInt("total",total);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        Gson gson = new Gson();
        LatLng start = new LatLng(Constants.currentLatitude, Constants.currentLongitude);
        stationInfoList = gson.fromJson(getArguments().getString("stationInfoList"),new TypeToken<List<StationInfo>>(){}.getType());
        try{
            for(int i = 0; i < stationInfoList.size(); i++) {
                LatLng end = new LatLng(stationInfoList.get(i).getLatitude(), stationInfoList.get(i).getLongitude());
                stationInfoList.get(i).setDistance(DistanceUtil.getDistance(start, end)/1000.00);
            }
            Collections.sort(stationInfoList, new sortList<StationInfo>("Distance",true));
        }catch (Exception e) {
            e.printStackTrace();
        }
        totalRecords = getArguments().getInt("total");

        //创建被装饰者实例
        final MyStationInfoAdapter adapter = new MyStationInfoAdapter(getActivity());
        //创建装饰者实例，并传入被装饰者和回调接口
        mAdapter = new StationInfoAdapter(adapter, new OnLoad() {
            @Override
            public void load(int pagePosition, int pageSize, final ILoadCallback callback) {
                //此处模拟做网络操作，0.5s延迟，将拉取的数据更新到adapter中
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<StationInfo> dataSet = stationInfoList;
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
        View view = inflater.inflate(R.layout.station_fragmnet, container, false);
        stationRefresh = (RefreshLayout) view.findViewById(R.id.station_swipe_refresh);
        stationInfo = (RecyclerView) view.findViewById(R.id.station_list);
        stationRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        StationFragment SF;
                        if(Constants.change == 0){
                            SF = new StationFragment().newInstance(Constants.stationRes,Constants.stationTotal);
                        }else{
                            SF = new StationFragment().newInstance(Constants.refreshRes,Constants.refreshTotal);
                        }
                        transaction.replace(R.id.fragment_container,SF);
                        transaction.commit();
                    }
                },500);
                stationRefresh.finishRefresh(500);
            }
        });
        stationInfo.setAdapter(mAdapter);
        stationInfo.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return view;
    }

}
