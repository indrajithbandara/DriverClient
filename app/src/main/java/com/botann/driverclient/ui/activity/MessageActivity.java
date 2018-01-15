package com.botann.driverclient.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.botann.driverclient.R;
import com.botann.driverclient.ui.fragment.MessageFragment;
import com.botann.driverclient.ui.fragment.PostMessageFragment;
import com.botann.driverclient.utils.Constants;

/**
 * Created by Orion on 2017/8/4.
 */
public class MessageActivity extends BaseActivity implements View.OnClickListener {

    private TextView messageBack;
    private TextView systemMessage;
    private TextView postMessage;
    private View systemBottom;
    private View postBottom;

    private FrameLayout ly_content;

    private MessageFragment system_fragment,default_fragment;
    private PostMessageFragment post_fragment;

    private Context mContext;
    private static MessageActivity mInstance = null;

    public static MessageActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mInstance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);

        bindView();
        setDefaultFragment();
    }

    //UI组件初始化与事件绑定
    private void bindView() {
        messageBack = (TextView) this.findViewById(R.id.message_back);
        systemMessage = (TextView) this.findViewById(R.id.system_message);
        postMessage = (TextView) this.findViewById(R.id.post_message);
        systemBottom = (View) this.findViewById(R.id.system_bottom);
        postBottom = (View) this.findViewById(R.id.post_bottom);
        ly_content = (FrameLayout) findViewById(R.id.message_fragment_container);

        messageBack.setOnClickListener(this);
        systemMessage.setOnClickListener(this);
        postMessage.setOnClickListener(this);
    }

    //设置默认的Fragment
    private void setDefaultFragment() {
        systemMessage = (TextView) this.findViewById(R.id.system_message);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        systemMessage.setSelected(true);
        systemBottom.setBackgroundColor(getResources().getColor(R.color.text_blue));
        postBottom.setBackgroundColor(getResources().getColor(R.color.text_white));
        //SharedPreferences def = getSharedPreferences("message", MODE_PRIVATE);
        String defStr = Constants.messageRes;
        Integer defTotal = Constants.messageTotal;
        default_fragment = new MessageFragment().newInstance(defStr,defTotal);
        transaction.replace(R.id.message_fragment_container,default_fragment);
        transaction.commit();
    }

    //重置所有文本的选中状态
    public void selected() {
        systemMessage.setSelected(false);
        postMessage.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (system_fragment != null) {
            transaction.hide(system_fragment);
        }
        if (post_fragment != null) {
            transaction.hide(post_fragment);
        }
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch (view.getId()) {
            case R.id.system_message:
                selected();
                systemMessage.setSelected(true);
                systemBottom.setBackgroundColor(getResources().getColor(R.color.text_blue));
                postBottom.setBackgroundColor(getResources().getColor(R.color.text_white));
                SharedPreferences system = getSharedPreferences("message", MODE_PRIVATE);
                String sysStr = system.getString("systemMessageList", "");
                Integer sysTotal = system.getInt("system_total", 0);
                system_fragment = new MessageFragment().newInstance(sysStr,sysTotal);
                transaction.add(R.id.message_fragment_container,system_fragment);
                break;
            case R.id.post_message:
                selected();
                postMessage.setSelected(true);
                systemBottom.setBackgroundColor(getResources().getColor(R.color.text_white));
                postBottom.setBackgroundColor(getResources().getColor(R.color.text_blue));
                SharedPreferences post = getSharedPreferences("message", MODE_PRIVATE);
                String posStr = post.getString("postMessageList", "");
                Integer posTotal = post.getInt("post_total", 0);
                post_fragment = new PostMessageFragment().newInstance(posStr,posTotal);
                transaction.add(R.id.message_fragment_container,post_fragment);
                break;
            case R.id.message_back:
                finish();
                break;
            default:
                break;
        }
        transaction.commit();
    }
}
