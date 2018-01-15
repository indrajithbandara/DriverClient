package com.botann.driverclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.botann.driverclient.R;
import com.botann.driverclient.model.MessageInfo;

import java.text.SimpleDateFormat;

/**
 * Created by Orion on 2017/8/4.
 */
public class MyMessageInfoAdapter extends BaseAdapter<MessageInfo> {
    private static SimpleDateFormat format =  new SimpleDateFormat("yyyy.MM.dd");
    private Context mContext;

    public MyMessageInfoAdapter(Context context) {
        mContext = context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.message_list_item, parent, false);
        return new MyMessageInfoAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyMessageInfoAdapter.MyViewHolder) holder).bind(getDataSet().get(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        //TextView mUnread;
        TextView mContent;
        TextView mTime;

        public MyViewHolder(View itemView) {
            super(itemView);

            //mUnread = (TextView) itemView.findViewById(R.id.unread);
            mContent = (TextView) itemView.findViewById(R.id.content);
            mTime = (TextView) itemView.findViewById(R.id.message_time);
        }

        public void bind(MessageInfo content) {
            mContent.setText(content.getContent());
            mTime.setText(format.format(content.getCreateDate()));
        }
    }
}
