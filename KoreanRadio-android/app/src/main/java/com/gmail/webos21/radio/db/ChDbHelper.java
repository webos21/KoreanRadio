package com.gmail.webos21.radio.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gmail.webos21.radio.Consts;

public class ChDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ChDbHelper";

    private static final String CREATE_TB_RADIO_CHANNEL =
            /* Indent */"CREATE TABLE IF NOT EXISTS " + Consts.TB_RADIO_CHANNEL + " (" +
            /* Indent */"	" + ChRow.ID + "       INTEGER  PRIMARY KEY  AUTOINCREMENT, " +
            /* Indent */"	" + ChRow.CH_FREQ + "  VARCHAR(6), " +
            /* Indent */"	" + ChRow.CH_NAME + "  VARCHAR(100), " +
            /* Indent */"	" + ChRow.PLAY_URL + " VARCHAR(100), " +
            /* Indent */"	" + ChRow.LOGO_URL + " VARCHAR(100), " +
            /* Indent */"	" + ChRow.REG_DATE + " INTEGER, " +
            /* Indent */"	" + ChRow.FIX_DATE + " INTEGER, " +
            /* Indent */"	" + ChRow.MEMO + "      VARCHAR(4000) " +
            /* Indent */");";

    private static final String DROP_TB_RADIO_CHANNEL =
            /* Indent */"DROP TABLE IF EXISTS " + Consts.TB_RADIO_CHANNEL + ";";

    public ChDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (Consts.DB_DEBUG) {
            Log.d(TAG, "onCreate [" + db.getPath() + "]");
        }
        db.execSQL(CREATE_TB_RADIO_CHANNEL);
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL(DROP_TB_RADIO_CHANNEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (Consts.DB_DEBUG) {
            Log.d(TAG, "onUpgrade [" + db.getPath() + "] oldVer = "
                    + oldVersion + ", newVer = " + newVersion);
        }
        if (oldVersion != newVersion) {
            onCreate(db);
        }
    }

    private void debugDump(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + tableName, null);
        if (rset == null) {
            return;
        }

        int nCol = rset.getColumnCount();
        int nRow = rset.getCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nCol; i++) {
            sb.append(rset.getColumnName(i)).append('(').append(i)
                    .append(')').append('\t').append('|').append('\t');
        }
        if (Consts.DB_DEBUG) {
            Log.d(TAG, sb.toString());
        }

        sb.delete(0, sb.length());

        rset.moveToFirst();
        for (int r = 0; r < nRow; r++) {
            for (int c = 0; c < nCol; c++) {
                sb.append(rset.getString(c)).append('\t');
            }
            sb.append('\n');
            rset.moveToNext();
        }
        if (Consts.DB_DEBUG) {
            Log.d(TAG, sb.toString());
        }

        rset.close();
        db.close();
    }
}
