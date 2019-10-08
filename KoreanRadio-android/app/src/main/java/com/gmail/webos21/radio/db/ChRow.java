package com.gmail.webos21.radio.db;

import android.database.Cursor;

import java.util.Date;

public class ChRow {
    public static final String ID = "id";
    public static final String CH_FREQ = "ch_freq";
    public static final String CH_NAME = "ch_name";
    public static final String PLAY_URL = "play_url";
    public static final String LOGO_URL = "logo_url";
    public static final String REG_DATE = "reg_date";
    public static final String FIX_DATE = "fix_date";
    public static final String MEMO = "memo";

    private Long id;
    private String chFreq;
    private String chName;
    private String playUrl;
    private String logoUrl;
    private Date regDate;
    private Date fixDate;
    private String memo;

    public ChRow(Long id, String chFreq, String chName, String playUrl, String logoUrl,
                 Long regDate, Long fixDate, String memo) {
        this.id = id;
        this.chFreq = chFreq;
        this.chName = chName;
        this.playUrl = playUrl;
        this.logoUrl = logoUrl;
        this.regDate = new Date(regDate);
        this.fixDate = new Date(fixDate);
        this.memo = memo;
    }

    public static ChRow bindCursor(Cursor cursor) {
        ChRow aRow = new ChRow(
                /* id ------------- */cursor.getLong(0),
                /* ch_freq -------- */cursor.getString(1),
                /* ch_name -------- */cursor.getString(2),
                /* play_url ------- */cursor.getString(3),
                /* logo_url ------- */cursor.getString(4),
                /* reg_date ------- */cursor.getLong(5),
                /* fix_date ------- */cursor.getLong(6),
                /* memo ----------- */cursor.getString(7));
        return aRow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChFreq() {
        return chFreq;
    }

    public void setChFreq(String chFreq) {
        this.chFreq = chFreq;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getFixDate() {
        return fixDate;
    }

    public void setFixDate(Date fixDate) {
        this.fixDate = fixDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
