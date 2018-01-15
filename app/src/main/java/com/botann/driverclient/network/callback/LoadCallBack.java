package com.botann.driverclient.network.callback;

import android.content.Context;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Orion on 2017/7/12.
 */
public abstract class LoadCallBack<T> extends BaseCallBack<T> {
    private Context context;
    private SpotsDialog spotsDialog;

    public LoadCallBack(Context context) {
        this.context = context;
        spotsDialog = new SpotsDialog(context);
    }

    private void showDialog() {
        spotsDialog.show();
    }

    private void hideDialog() {
        if (spotsDialog != null) {
            spotsDialog.dismiss();
        }
    }

    public void setMsg(String str) {
        spotsDialog.setMessage(str);
    }

    public void setMsg(int  resId) {
        spotsDialog.setMessage(context.getString(resId));
    }


    @Override
    public void OnRequestBefore(Request request) {
        showDialog();

    }

    @Override
    public void onFailure(Call call, IOException e) {
        hideDialog();
    }

    @Override
    public void onResponse(Response response) {
        hideDialog();
    }

    @Override
    public void inProgress(int progress, long total, int id) {

    }

    public abstract void onSuccess(Call call, Response response, String s);
}
