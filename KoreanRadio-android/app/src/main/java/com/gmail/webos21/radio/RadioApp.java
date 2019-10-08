package com.gmail.webos21.radio;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

public class RadioApp extends Application {

    private static final String TAG = "RadioApp";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private RadioServiceHelper rsh;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Consts.DEBUG) {
            Log.i(TAG, "onCreate!!!!!!");
        }
        rsh = new RadioServiceHelper(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (Consts.DEBUG) {
            Log.i(TAG, "onTerminate!!!!!!");
        }
        if (rsh != null) {
            if (rsh.isPlaying()) {
                rsh.pause();
            }
            rsh = null;
        }
    }

    public RadioServiceHelper getRadioServiceHelper() {
        return rsh;
    }

}
