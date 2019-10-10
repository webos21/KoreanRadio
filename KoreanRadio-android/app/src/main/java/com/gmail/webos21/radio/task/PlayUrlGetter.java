package com.gmail.webos21.radio.task;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import com.gmail.webos21.http.HttpHelper;
import com.gmail.webos21.http.HttpMethod;
import com.gmail.webos21.http.HttpResult;

public class PlayUrlGetter extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "PlayUrlGetter";
    private static final boolean DEBUG = false;

    private String url;
    private MediaPlayer player;
    private String realUrl;

    public PlayUrlGetter(String url, MediaPlayer player) {
        this.url = url;
        this.player = player;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            HttpResult hr = HttpHelper.httpRequest(HttpMethod.GET, url, null, null, null, null, null);
            if (hr != null) {
                byte[] hrb = hr.getResponseBody();
                String shrb = new String(hrb, "UTF-8");
                if (DEBUG) {
                    Log.d(TAG, shrb);
                }
                String[] lines = shrb.split("\n");
                for (String line : lines) {
                    if (DEBUG) {
                        Log.d(TAG, line);
                    }
                    if (line.startsWith("File1=")) {
                        realUrl = line.substring(6);
                        if (DEBUG) {
                            Log.d(TAG, realUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (realUrl != null) {
            try {
                player.setDataSource(realUrl);
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
