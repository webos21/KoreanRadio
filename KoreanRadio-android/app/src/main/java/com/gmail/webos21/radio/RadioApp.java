package com.gmail.webos21.radio;

import android.app.Application;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

public class RadioApp extends Application {

    private static final String TAG = "RadioApp";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private MediaPlayer mp;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Consts.DEBUG) {
            Log.i(TAG, "onCreate!!!!!!");
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (Consts.DEBUG) {
            Log.i(TAG, "onTerminate!!!!!!");
        }
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void playRadio(String url) {
        try {
            if (mp == null) {
                mp = new MediaPlayer();
            }
            mp.setDataSource(url);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRadio() {
        try {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return (mp != null);
    }
}
