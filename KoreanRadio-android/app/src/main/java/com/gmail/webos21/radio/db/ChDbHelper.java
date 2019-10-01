package com.gmail.webos21.radio.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gmail.webos21.radio.Consts;

import java.util.ArrayList;
import java.util.List;

public class ChDbHelper extends SQLiteOpenHelper implements ChDbInterface {

    private static final String TAG = "ChDbHelper";

    private static final String TB_RADIO_CHANNEL = "radio_channel";

    private static final String CREATE_TB_RADIO_CHANNEL =
            /* Indent */"CREATE TABLE IF NOT EXISTS " + TB_RADIO_CHANNEL + " (" +
            /* Indent */"	id               INTEGER  PRIMARY KEY  AUTOINCREMENT, " +
            /* Indent */"	ch_freq          VARCHAR(6), " +
            /* Indent */"	ch_name          VARCHAR(100), " +
            /* Indent */"	play_url         VARCHAR(100), " +
            /* Indent */"	logo_url         VARCHAR(100), " +
            /* Indent */"	reg_date         INTEGER, " +
            /* Indent */"	fix_date         INTEGER, " +
            /* Indent */"	memo             VARCHAR(4000) " +
            /* Indent */");";

    private static final String DROP_TB_RADIO_CHANNEL =
            /* Indent */"DROP TABLE IF EXISTS " + TB_RADIO_CHANNEL + ";";

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

    @Override
    public List<ChRow> findRows() {
        List<ChRow> aList = new ArrayList<ChRow>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM "
                + TB_RADIO_CHANNEL, null);
        if (rset == null || rset.getCount() == 0) {
            return aList;
        }

        rset.moveToFirst();
        do {
            ChRow aRow = new ChRow(
                    /* id ------------- */rset.getLong(0),
                    /* ch_freq -------- */rset.getString(1),
                    /* ch_name -------- */rset.getString(2),
                    /* play_url ------- */rset.getString(3),
                    /* logo_url ------- */rset.getString(4),
                    /* reg_date ------- */rset.getLong(5),
                    /* fix_date ------- */rset.getLong(6),
                    /* memo ----------- */rset.getString(7));
            aList.add(aRow);
        } while (rset.moveToNext());

        if (rset != null) {
            rset.close();
        }
        db.close();

        if (Consts.DB_DEBUG) {
            debugDump(TB_RADIO_CHANNEL);
        }

        return aList;
    }

    @Override
    public List<ChRow> findRows(String keyString) {
        List<ChRow> aList = new ArrayList<ChRow>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor rset = db.rawQuery(
                /* intent ---------- */ "SELECT * " +
                        /* intent -------- */ " FROM " + TB_RADIO_CHANNEL + " " +
                        /* intent -------- */ " WHERE (surl LIKE ?) OR " +
                        /* intent -------- */ "        (sname LIKE ?) OR " +
                        /* intent -------- */ "        (stype LIKE ?)"
                , new String[]{"%" + keyString + "%", "%" + keyString + "%", "%" + keyString + "%"});
        if (rset == null || rset.getCount() == 0) {
            return aList;
        }

        rset.moveToFirst();
        do {
            ChRow aRow = new ChRow(
                    /* id ------------- */rset.getLong(0),
                    /* ch_freq -------- */rset.getString(1),
                    /* ch_name -------- */rset.getString(2),
                    /* play_url ------- */rset.getString(3),
                    /* logo_url ------- */rset.getString(4),
                    /* reg_date ------- */rset.getLong(5),
                    /* fix_date ------- */rset.getLong(6),
                    /* memo ----------- */rset.getString(7));
            aList.add(aRow);
        } while (rset.moveToNext());

        if (rset != null) {
            rset.close();
        }
        db.close();

        return aList;
    }

    @Override
    public ChRow getRow(Long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + TB_RADIO_CHANNEL
                + " WHERE id = " + id, null);
        if (rset == null || rset.getCount() == 0) {
            return null;
        }
        rset.moveToFirst();
        ChRow aRow = new ChRow(
                /* id ------------- */rset.getLong(0),
                /* ch_freq -------- */rset.getString(1),
                /* ch_name -------- */rset.getString(2),
                /* play_url ------- */rset.getString(3),
                /* logo_url ------- */rset.getString(4),
                /* reg_date ------- */rset.getLong(5),
                /* fix_date ------- */rset.getLong(6),
                /* memo ----------- */rset.getString(7));
        rset.close();
        db.close();
        return aRow;
    }

    @Override
    public ChRow getRow(ChRow aRow) {
        return getRow(aRow.getId());
    }

    @Override
    public boolean updateRow(ChRow newRow) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor rset = db.rawQuery("SELECT * FROM " + TB_RADIO_CHANNEL
                + " WHERE id = " + newRow.getId(), null);
        if (rset == null || rset.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("id", newRow.getId());
            cv.put("ch_freq", newRow.getChFreq());
            cv.put("ch_name", newRow.getChName());
            cv.put("play_url", newRow.getPlayUrl());
            cv.put("logo_url", newRow.getLogoUrl());
            cv.put("reg_date", newRow.getRegDate().getTime());
            cv.put("fix_date", newRow.getFixDate().getTime());
            cv.put("memo", newRow.getMemo());
            db.insert(TB_RADIO_CHANNEL, null, cv);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("ch_freq", newRow.getChFreq());
            cv.put("ch_name", newRow.getChName());
            cv.put("play_url", newRow.getPlayUrl());
            cv.put("logo_url", newRow.getLogoUrl());
            cv.put("reg_date", newRow.getRegDate().getTime());
            cv.put("fix_date", newRow.getFixDate().getTime());
            cv.put("memo", newRow.getMemo());
            db.update(TB_RADIO_CHANNEL, cv, " id = ? ",
                    new String[]{Long.toString(newRow.getId())});
        }

        if (rset != null) {
            rset.close();
        }
        db.close();

        return true;
    }

    @Override
    public int deleteRow(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TB_RADIO_CHANNEL, "id = " + id, null);
        db.close();

        return result;
    }

    @Override
    public int deleteRow(ChRow aRow) {
        return deleteRow(aRow.getId());
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
