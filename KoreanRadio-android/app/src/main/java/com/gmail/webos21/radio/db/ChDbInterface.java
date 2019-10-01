package com.gmail.webos21.radio.db;

import java.util.List;

public interface ChDbInterface {

    List<ChRow> findRows();
    List<ChRow> findRows(String keyString);
    ChRow getRow(Long id);
    ChRow getRow(ChRow aRow);
    boolean updateRow(ChRow newRow);
    int deleteRow(Long id);
    int deleteRow(ChRow aRow);

}
