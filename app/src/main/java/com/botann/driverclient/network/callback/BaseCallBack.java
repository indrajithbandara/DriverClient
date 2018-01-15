package com.botann.driverclient.network.callback;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Orion on 2017/7/12.
 */
public abstract class BaseCallBack<T> {
    public Type mType;

    static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }


    public BaseCallBack() {
        mType = getSuperclassTypeParameter(getClass());
    }

    public abstract void OnRequestBefore(Request request);

    public abstract void onFailure(Call call, IOException e);

    public abstract void onSuccess(Call call, Response response, T t);

    public abstract void onResponse(Response response);

    public abstract void onError(Call call, int statusCode, Exception e);

    public abstract void inProgress(int progress, long total , int id);
}
