package com.yuri.vpbanner;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.yuri.xlog.Log;
import com.yuri.xlog.Settings;

/**
 * Created by Yuri on 2016/6/3.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //log init
        Log.initialize(
                Settings.getInstance()
                .isDebug(true)
                .isShowMethodLink(true)
                .isShowThreadInfo(true)
                .setAppTag("Yuri")
        );

        LeakCanary.install(this);
    }
}
