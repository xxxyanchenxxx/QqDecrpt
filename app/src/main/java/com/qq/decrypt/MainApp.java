package com.qq.decrypt;

import android.app.Application;
import android.content.Context;

/**
 * Created by yanchen on 17-11-28.
 */

public class MainApp extends Application {
    private static Context sApp = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
    }

    public static Context getApp(){
        return sApp;
    }
}
