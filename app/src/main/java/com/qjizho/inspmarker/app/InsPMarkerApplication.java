package com.qjizho.inspmarker.app;

import android.app.Application;

import in.srain.cube.Cube;

/**
 * Created by qjizho on 15-7-21.
 */
public class InsPMarkerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Cube.onCreate(this);
    }

}
