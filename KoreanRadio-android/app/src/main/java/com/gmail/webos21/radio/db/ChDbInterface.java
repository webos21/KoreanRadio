package com.gmail.webos21.radio.db;

import android.database.Cursor;

import java.util.List;

public interface ChDbInterface {

    List<ChRow> findRows();

    Cursor findRowsCursor();

    List<ChRow> findRows(String keyString);

    Cursor findRowsCursor(String keyString);

    ChRow getRow(Long id);

    ChRow getRow(ChRow aRow);

    boolean updateRow(ChRow newRow);

    int deleteRow(Long id);

    int deleteRow(ChRow aRow);

}
