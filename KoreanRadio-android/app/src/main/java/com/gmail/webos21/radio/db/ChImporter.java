package com.gmail.webos21.radio.db;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gmail.webos21.radio.Consts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class ChImporter extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "ChImporter";

    private Context context;
    private File csvFile;
    private Runnable postRun;

    public ChImporter(Context context, File csvFile, Runnable postRun) {
        this.context = context;
        this.csvFile = csvFile;
        this.postRun = postRun;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        BufferedReader bri = null;
        try {
            bri = new BufferedReader(new FileReader(csvFile));
            String s;

            while ((s = bri.readLine()) != null) {
                if (Consts.DEBUG) {
                    Log.i(TAG, "[FileRead] " + s);
                }
                processLine(context, s);
            }

            bri.close();
            bri = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bri != null) {
                try {
                    bri.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bri = null;
            }
        }
        context = null;

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        postRun.run();
    }

    private void processLine(Context context, String s) {
        String[] strArr = s.split(",");

        Long id = null;
        String ch_freq = null;
        String ch_name = null;
        String play_url = null;
        String logo_url = null;
        Date regdate = null;
        Date fixdate = null;
        String memo = null;

        if (strArr.length == 7) {
            ch_freq = strArr[0];
            ch_name = strArr[1];
            play_url = strArr[2];
            logo_url = strArr[3];
            if ("null".equals(strArr[4])) {
                regdate = new Date(0);
            } else {
                try {
                    regdate = Consts.SDF_DATE.parse(strArr[4]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if ("null".equals(strArr[5])) {
                fixdate = new Date(0);
            } else {
                try {
                    fixdate = Consts.SDF_DATE.parse(strArr[5]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            memo = ((strArr[6] == null || strArr[6].length() == 0 || "null".equals(strArr[6])) ? "" : strArr[6]);
        } else if (strArr.length == 8) {
            id = Long.parseLong(strArr[0]);
            ch_freq = strArr[1];
            ch_name = strArr[2];
            play_url = strArr[3];
            logo_url = strArr[4];
            if ("null".equals(strArr[5])) {
                regdate = new Date(0);
            } else {
                try {
                    regdate = Consts.SDF_DATE.parse(strArr[5]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if ("null".equals(strArr[6])) {
                fixdate = new Date(0);
            } else {
                try {
                    fixdate = Consts.SDF_DATE.parse(strArr[6]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            memo = ((strArr[7] == null || strArr[7].length() == 0 || "null".equals(strArr[7])) ? "" : strArr[7]);
        } else {
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("ch_freq", ch_freq);
        cv.put("ch_name", ch_name);
        cv.put("play_url", play_url);
        cv.put("logo_url", logo_url);
        cv.put("reg_date", (regdate != null) ? regdate.getTime() : System.currentTimeMillis());
        cv.put("fix_date", (fixdate != null) ? fixdate.getTime() : System.currentTimeMillis());
        cv.put("memo", memo);

        Uri addUri = context.getContentResolver().insert(
                Uri.parse("content://" + Consts.CHANNEL_PROVIER_AUTHORITY + "/" + Consts.TB_RADIO_CHANNEL),
                cv
        );
    }
}
