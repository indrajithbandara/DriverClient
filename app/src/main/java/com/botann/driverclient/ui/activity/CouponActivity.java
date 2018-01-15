package com.botann.driverclient.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.botann.driverclient.R;
import com.botann.driverclient.ui.fragment.CouponFragment;
import com.botann.driverclient.utils.Constants;

/**
 * Created by Orion on 2017/8/3.
 */
public class CouponActivity extends BaseActivity implements View.OnClickListener {

    private TextView couponBack;

    private FrameLayout ly_content;

    private CouponFragment coupon_fragment;

    private Context mContext;
    private static CouponActivity mInstance = null;

    public static CouponActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mInstance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coupon);

        bindView();
    }

    //UI组件初始化与事件绑定
    private void bindView() {
        couponBack = (TextView) this.findViewById(R.id.coupon_back);
        ly_content = (FrameLayout) findViewById(R.id.coupon_fragment_container);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //SharedPreferences coupon = getSharedPreferences("coupon", MODE_PRIVATE);
        String resStr = Constants.couponRes;//coupon.getString("couponInfoList", "");
        Integer total = Constants.couponTotal;//coupon.getInt("total", 0);

        if(coupon_fragment == null) {
            coupon_fragment = new CouponFragment().newInstance(resStr, total);
            transaction.add(R.id.coupon_fragment_container,coupon_fragment);
        }else{
            transaction.show(coupon_fragment);
        }
        transaction.commit();

        couponBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.coupon_back:
                finish();
                break;
        }
    }
}
