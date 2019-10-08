package com.gmail.webos21.radio;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.gmail.webos21.radio.db.ChRow;

import java.util.ArrayList;

public class RadioService extends Service {
    private final IBinder mBinder = new RadioServiceBinder();
    private ArrayList<Long> mChannelIds = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;
    private int mCurrentPosition;
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
                rewind();
            } else if (Consts.FORWARD.equals(action)) {
                forward();
            } else if (Consts.CLOSE.equals(action)) {
                pause();
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
        }
    }

    private void queryChannelItem(int position) {
        mCurrentPosition = position;
        long chId = mChannelIds.get(position);
        Uri uri = Uri.parse("content://" + Consts.CHANNEL_PROVIER_URI + "/" + Consts.TB_RADIO_CHANNEL);
        String[] projection = new String[]{
                ChRow.ID,
                ChRow.CH_FREQ,
                ChRow.CH_NAME,
                ChRow.PLAY_URL,
                ChRow.LOGO_URL,
                ChRow.REG_DATE,
                ChRow.FIX_DATE,
                ChRow.MEMO
        };
        String selection = ChRow.ID + " = ?";
        String[] selectionArgs = {String.valueOf(chId)};
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mChannel = ChRow.bindCursor(cursor);
            }
            cursor.close();
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

    public void setPlayList(ArrayList<Long> channelIds) {
        if (!mChannelIds.equals(channelIds)) {
            mChannelIds.clear();
            mChannelIds.addAll(channelIds);
        }
    }

    public ChRow getChannelItem() {
        return mChannel;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void play(int position) {
        queryChannelItem(position);
        stop();
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

    public void forward() {
        if (mChannelIds.size() - 1 > mCurrentPosition) {
            mCurrentPosition++; // 다음 포지션으로 이동.
        } else {
            mCurrentPosition = 0; // 처음 포지션으로 이동.
        }
        play(mCurrentPosition);
    }

    public void rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mChannelIds.size() - 1; // 마지막 포지션으로 이동.
        }
        play(mCurrentPosition);
    }

    public class RadioServiceBinder extends Binder {
        RadioService getService() {
            return RadioService.this;
        }
    }
}
