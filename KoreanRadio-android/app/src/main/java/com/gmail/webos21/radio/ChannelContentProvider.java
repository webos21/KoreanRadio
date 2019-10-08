package com.gmail.webos21.radio;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.gmail.webos21.radio.db.ChDbHelper;

public class ChannelContentProvider extends ContentProvider {

    private Uri contentUri = Uri.parse(Consts.CHANNEL_PROVIER_URI);
    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        ChDbHelper dbHelper = new ChDbHelper(context, Consts.DB_FILE, null, Consts.DB_VERSION);
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor c = db.query(Consts.TB_RADIO_CHANNEL, null, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri auri = null;
        long id = db.insert(Consts.TB_RADIO_CHANNEL, null, values);
        if (id > 0) {
            auri = ContentUris.withAppendedId(contentUri, id);
            getContext().getContentResolver().notifyChange(auri, null);
        } else {
            Toast.makeText(getContext(), "INSERT FAILED!!", Toast.LENGTH_SHORT).show();
        }
        return auri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int id = db.delete(Consts.TB_RADIO_CHANNEL, selection, selectionArgs);
        if (id > 0) {
            Uri auri = ContentUris.withAppendedId(contentUri, id);
            getContext().getContentResolver().notifyChange(auri, null);
        } else {
            Toast.makeText(getContext(), "DELETE FAILED!!", Toast.LENGTH_SHORT).show();
        }
        return id;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int id = db.update(Consts.TB_RADIO_CHANNEL, values, selection, selectionArgs);
        if (id > 0) {
            Uri auri = ContentUris.withAppendedId(contentUri, id);
            getContext().getContentResolver().notifyChange(auri, null);
        } else {
            Toast.makeText(getContext(), "UPDATE FAILED!!", Toast.LENGTH_SHORT).show();
        }
        return id;
    }
}
