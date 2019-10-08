package com.gmail.webos21.radio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.gmail.webos21.radio.db.ChRow;

import java.util.ArrayList;

public class RadioServiceHelper {
    private ServiceConnection mServiceConnection;
    private RadioService mService;

    public RadioServiceHelper(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((RadioService.RadioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, RadioService.class).setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void play(ChRow item) {
        if (mService != null) {
            mService.play(item);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void togglePlay() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.play();
        }
    }

    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public void pause() {
        if (mService != null) {
            mService.pause();
        }
    }

    public void forward() {
        if (mService != null) {
            /* Nothing to do */
        }
    }

    public void rewind() {
        if (mService != null) {
            /* Nothing to do */
        }
    }

}
