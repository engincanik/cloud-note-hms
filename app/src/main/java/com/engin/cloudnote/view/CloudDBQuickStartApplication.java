package com.engin.cloudnote.view;

import android.app.Application;

import com.engin.cloudnote.model.CloudDBZoneWrapper;

public class CloudDBQuickStartApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CloudDBZoneWrapper.initAGConnectCloudDB(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
