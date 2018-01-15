package com.botann.driverclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.botann.driverclient.R;
import com.botann.driverclient.model.ConsumeInfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Orion on 2017/7/21.
 */
public class MyConsumeInfoAdapter extends BaseAdapter<ConsumeInfo> {
    private static SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DecimalFormat df = new DecimalFormat("######0.00");
    private Context mContext;

    public MyConsumeInfoAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.consume_list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).bind(getDataSet().get(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mOrderNum;
        TextView mSiteName;
        TextView mCarBrand;
        TextView mConsumeDate;
        TextView mTotalMiles;
        TextView mReferMiles;
        TextView mFare;

        public MyViewHolder(View itemView) {
            super(itemView);

            mOrderNum = (TextView) itemView.findViewById(R.id.order_num);
            mSiteName = (TextView) itemView.findViewById(R.id.site_name);
            mCarBrand = (TextView) itemView.findViewById(R.id.car_brand);
            mConsumeDate = (TextView) itemView.findViewById(R.id.consume_date);
            mTotalMiles = (TextView) itemView.findViewById(R.id.total_miles);
            mReferMiles = (TextView) itemView.findViewById(R.id.refer_miles);
            mFare = (TextView) itemView.findViewById(R.id.fare);
        }

        public void bind(ConsumeInfo content) {
            mOrderNum.setText("订单号："+content.getSerialNum());
            mSiteName.setText(content.getStationName());
            mCarBrand.setText("车牌号："+content.getCarNumber());
            mConsumeDate.setText(format.format(content.getCreateDate()));
            mTotalMiles.setText("总里程："+content.getReferMiles()/1000+"km");
            mReferMiles.setText("计费里程："+content.getRealMile()+"km");
            mFare.setText("实际支付／优惠券／余额    "+(content.getRealFare()!=null?df.format(content.getRealFare()/100.00):0)+"/"+(content.getCoupon()!=null?df.format(content.getCoupon()/100.00):0)+"/"+(content.getBalance()!=null?df.format(content.getBalance()/100.00):0));
        }

    }
}
