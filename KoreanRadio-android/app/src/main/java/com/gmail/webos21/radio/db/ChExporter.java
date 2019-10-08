package com.gmail.webos21.radio.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gmail.webos21.radio.Consts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class ChExporter extends AsyncTask<Void, Void, Void> {

    private static final String TAG = "ChExporter";

    private Context context;
    private File csvFile;
    private Runnable postRun;

    public ChExporter(Context context, File csvFile, Runnable postRun) {
        this.context = context;
        this.csvFile = csvFile;
        this.postRun = postRun;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        BufferedWriter bwo = null;

        String[] projection = null; /* "null" means ALL */
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor rset = context.getContentResolver().query(
                Uri.parse("content://" + Consts.CHANNEL_PROVIER_AUTHORITY + "/" + Consts.TB_RADIO_CHANNEL),
                projection, selection, selectionArgs, sortOrder
        );

        if (rset != null && rset.getCount() > 0) {
            rset.moveToFirst();
            StringBuffer sb = new StringBuffer();
            try {
                bwo = new BufferedWriter(new FileWriter(csvFile));
                do {
                    ChRow aRow = ChRow.bindCursor(rset);
                    String l = makeLine(aRow, sb);
                    if (Consts.DEBUG) {
                        Log.i(TAG, l);
                    }
                    bwo.write(l);
                } while (rset.moveToNext());
                bwo.close();
                bwo = null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bwo != null) {
                    try {
                        bwo.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bwo = null;
                }
            }
            rset.close();
            sb = null;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        postRun.run();
    }

    private String makeLine(ChRow pbRow, StringBuffer sb) {
        sb.delete(0, sb.length());

        sb.append(Long.toString(pbRow.getId())).append(',');
        sb.append(pbRow.getChFreq()).append(',');
        sb.append(pbRow.getChName()).append(',');
        sb.append(pbRow.getPlayUrl()).append(',');
        sb.append(pbRow.getLogoUrl()).append(',');
        sb.append(Consts.SDF_DATE.format(pbRow.getRegDate())).append(',');
        sb.append(Consts.SDF_DATE.format(pbRow.getFixDate())).append(',');
        String memo = (pbRow.getMemo() == null || pbRow.getMemo().length() == 0) ? "null" : pbRow.getMemo();
        sb.append(memo);
        sb.append("\r\n");

        return sb.toString();
    }
}