package com.provider.global.provider.config;

import android.app.Application;

/**
 * Created by MonarchPedo on 8/24/2017.
 */
public class AppController extends Application {

    public  static final String TAG = AppController.class.getSimpleName();

    //private RequestQueue mRequestqueue;

    private static AppController sessionInstance;

    public void  onCreate(){
        super.onCreate();
        sessionInstance = this;
    }

    public static synchronized AppController getInstance(){
        return sessionInstance;
    }

}
