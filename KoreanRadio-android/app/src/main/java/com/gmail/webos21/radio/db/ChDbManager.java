package com.gmail.webos21.radio.db;

import android.content.Context;

import com.gmail.webos21.radio.Consts;

public class ChDbManager {

    private static volatile ChDbManager instance;

    private Context context;
    private ChDbHelper dbHelper;

    private ChDbManager() {
    }

    public static ChDbManager getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (ChDbManager.class) {
            if (instance != null) {
                return instance;
            }
            instance = new ChDbManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.dbHelper = new ChDbHelper(context, "radio.db", null, Consts.DB_VERSION);
    }

    public void destroy() {
        if (dbHelper != null) {
            dbHelper.close();
            dbHelper = null;
        }
        if (context != null) {
            this.context = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    public ChDbInterface getPbDbInterface() {
        return this.dbHelper;
    }
}
