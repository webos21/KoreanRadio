package com.gmail.webos21.radio;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.gmail.webos21.android.ild.ImageLoader;

public class NotificationPlayer {

    private RadioService mService;
    private boolean isForeground;

    private ImageLoader imgLoader;

    public NotificationPlayer(RadioService service) {
        mService = service;
    }

    @SuppressLint("StaticFieldLeak")
    public void updateNotificationPlayer() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (imgLoader == null) {
                    imgLoader = new ImageLoader(mService.getApplicationContext(), R.drawable.ic_gt);
                }

                Bitmap largIcon = imgLoader.getBitmap(mService.getChannelItem().getLogoUrl());

                Intent actionTogglePlay = new Intent(Consts.TOGGLE_PLAY);
                Intent actionForward = new Intent(Consts.FORWARD);
                Intent actionRewind = new Intent(Consts.REWIND);
                Intent actionClose = new Intent(Consts.CLOSE);
                PendingIntent togglePlay = PendingIntent.getService(mService, 0, actionTogglePlay, 0);
                PendingIntent forward = PendingIntent.getService(mService, 0, actionForward, 0);
                PendingIntent rewind = PendingIntent.getService(mService, 0, actionRewind, 0);
                PendingIntent close = PendingIntent.getService(mService, 0, actionClose, 0);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(mService, Consts.NOTI_CHANNEL);
                builder
                        .setContentTitle("[" + mService.getChannelItem().getChFreq() + "]")
                        .setContentText(mService.getChannelItem().getChName())
                        .setLargeIcon(largIcon)
                        .setContentIntent(PendingIntent.getActivity(mService, 0, new Intent(mService, MainActivity.class), 0));

                builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_rew, "", rewind));
                builder.addAction(new NotificationCompat.Action(mService.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play, "", togglePlay));
                builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_ff, "", forward));
                builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel, "", close));
                int[] actionsViewIndexs = new int[]{1, 2, 3};
                builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(actionsViewIndexs));
                builder.setSmallIcon(R.drawable.ic_gt);

                Notification notification = builder.build();

                NotificationManagerCompat.from(mService).notify(Consts.NOTIFICATION_PLAYER_ID, notification);

                if (!isForeground) {
                    isForeground = true;
                    // 서비스를 Foreground 상태로 만든다
                    mService.startForeground(Consts.NOTIFICATION_PLAYER_ID, notification);
                }

                return null;
            }
        }.execute();
    }

    public void removeNotificationPlayer() {
        mService.stopForeground(true);
        isForeground = false;
    }

}
