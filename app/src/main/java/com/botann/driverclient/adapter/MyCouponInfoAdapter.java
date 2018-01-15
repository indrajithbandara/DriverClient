package com.botann.driverclient.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.botann.driverclient.R;
import com.botann.driverclient.model.CouponInfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Orion on 2017/8/3.
 */
public class MyCouponInfoAdapter extends BaseAdapter<CouponInfo> {
    private static SimpleDateFormat format =  new SimpleDateFormat("yyyy.MM.dd");
    private static DecimalFormat df = new DecimalFormat("######0.00");
    private Context mContext;

    public MyCouponInfoAdapter(Context context) {
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.coupon_list_item, parent, false);
        return new MyCouponInfoAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).bind(getDataSet().get(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout mCoupon;
        TextView mFare;
        TextView mTitle;
        TextView mLeft;
        TextView mTime;
        TextView mUsed;

        public MyViewHolder(View itemView) {
            super(itemView);

            mCoupon = (FrameLayout) itemView.findViewById(R.id.coupon_list_item);
            mFare = (TextView) itemView.findViewById(R.id.coupon_fare);
            mTitle = (TextView) itemView.findViewById(R.id.coupon_title);
            mLeft = (TextView) itemView.findViewById(R.id.left_amount);
            mTime = (TextView) itemView.findViewById(R.id.coupon_time);
            mUsed = (TextView) itemView.findViewById(R.id.coupon_used);
        }

        public void bind(CouponInfo content) {
            mFare.setText(df.format(content.getFare()/100.00));
            mTitle.setText(content.getCouponTitle());
            mLeft.setText("剩余"+df.format((content.getFare()-content.getUsedAmouont())/100.00)+"元未使用");
            mTime.setText(content.getEndDate()==0? "永久有效":format.format(content.getCreateDate())+"至"+format.format(content.getEndDate()));
            if(content.getUsed()==1) {
                mCoupon.setBackgroundColor(Color.parseColor("#757575"));
                mUsed.setVisibility(View.VISIBLE);
            }else{
                mCoupon.setBackgroundColor(Color.parseColor("#FFA500"));
                mUsed.setVisibility(View.INVISIBLE);
            }
        }
    }
}
