package com.gmail.webos21.radio;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.gmail.webos21.radio.db.ChRow;

import java.util.ArrayList;

public class RadioService extends Service {
    private final IBinder mBinder = new RadioServiceBinder();

    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;

    private ChRow mChannel;

    private NotificationPlayer mNotificationPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
                sendBroadcast(new Intent(Consts.PREPARED)); // prepared 전송
                updateNotificationPlayer();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                sendBroadcast(new Intent(Consts.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                updateNotificationPlayer();
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                sendBroadcast(new Intent(Consts.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                updateNotificationPlayer();
                return false;
            }
        });
        mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
        mNotificationPlayer = new NotificationPlayer(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (Consts.TOGGLE_PLAY.equals(action)) {
                if (isPlaying()) {
                    pause();
                } else {
                    play();
                }
            } else if (Consts.REWIND.equals(action)) {
                /* Nothing to do */
            } else if (Consts.FORWARD.equals(action)) {
                /* Nothing to do */
            } else if (Consts.CLOSE.equals(action)) {
                stop();
                removeNotificationPlayer();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        removeNotificationPlayer();
    }

    private void updateNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.updateNotificationPlayer();
        }
    }

    private void removeNotificationPlayer() {
        if (mNotificationPlayer != null) {
            mNotificationPlayer.removeNotificationPlayer();
            mNotificationPlayer = null;
        }
    }

    private void prepare() {
        try {
            mMediaPlayer.setDataSource(mChannel.getPlayUrl());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }

    public ChRow getChannelItem() {
        return mChannel;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void play(ChRow item) {
        this.mChannel = item;
        stop();
        if (mNotificationPlayer == null) {
            mNotificationPlayer = new NotificationPlayer(this);
        }
        prepare();
    }

    public void play() {
        if (isPrepared) {
            mMediaPlayer.start();
            sendBroadcast(new Intent(Consts.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            updateNotificationPlayer();
        }
    }

    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
            sendBroadcast(new Intent(Consts.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            updateNotificationPlayer();
        }
    }

    public class RadioServiceBinder extends Binder {
        RadioService getService() {
            return RadioService.this;
        }
    }
}
