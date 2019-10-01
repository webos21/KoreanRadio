package com.gmail.webos21.radio.db;

import java.util.Date;

public class ChRow {
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
