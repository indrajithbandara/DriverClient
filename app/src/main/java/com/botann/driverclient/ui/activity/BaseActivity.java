package com.botann.driverclient.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;

/**
 * Created by Orion on 2017/6/19.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //当前按下返回键的系统时间
    private long currentBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // converts the default values to dp to be compatible with different screen sizes
        FlatUI.initDefaultValues(this);

    }

    @Override
    public void onBackPressed()
    {
        currentBackTime = System.currentTimeMillis();
        //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
        if(currentBackTime - lastBackTime > 2 * 1000){
            Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
            lastBackTime = currentBackTime;
        }else{ //如果两次按下的时间差小于2秒，则退出程序
            finish();
        }
    }

}
