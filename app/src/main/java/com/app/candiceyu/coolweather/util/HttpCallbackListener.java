package com.app.candiceyu.coolweather.util;

/**
 * Created by candiceyu on 16/5/26.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);

}
