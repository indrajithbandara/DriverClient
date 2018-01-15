package com.botann.driverclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.botann.driverclient.R;
import com.botann.driverclient.model.RechargeInfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Orion on 2017/7/21.
 */
public class MyRechargeInfoAdapter extends BaseAdapter<RechargeInfo> {
    private static SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DecimalFormat df = new DecimalFormat("######0.00");
    private Context mContext;

    public MyRechargeInfoAdapter(Context context) { mContext = context; }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.recharge_list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyRechargeInfoAdapter.MyViewHolder) holder).bind(getDataSet().get(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mRechargeDate;
        TextView mRechargeSite;
        TextView mRechargeAmount;
        TextView mRechargeType;

        public MyViewHolder(View itemView) {
            super(itemView);

            mRechargeDate = (TextView) itemView.findViewById(R.id.recharge_date);
            mRechargeSite = (TextView) itemView.findViewById(R.id.recharge_site);
            mRechargeAmount = (TextView) itemView.findViewById(R.id.recharge_amount);
            mRechargeType = (TextView) itemView.findViewById(R.id.recharge_type);
        }

        public void bind(RechargeInfo content) {
            mRechargeDate.setText(format.format(content.getCreateDate()));
            mRechargeSite.setText(content.getStationName());
            mRechargeAmount.setText(df.format(content.getRechargeAmount()/100.00)+"元");
            if(content.getRechargeType()==1){
                mRechargeType.setText("支付宝充值");
            }else if(content.getRechargeType()==2){
                mRechargeType.setText("财务充值");
            }else{
                mRechargeType.setText("微信充值");
            }
        }
    }
}
