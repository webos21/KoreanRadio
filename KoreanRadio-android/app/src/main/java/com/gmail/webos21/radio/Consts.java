package com.gmail.webos21.radio;

import java.text.SimpleDateFormat;

public class Consts {

    public static final boolean DEBUG = true;
    public static final boolean DB_DEBUG = false;

    public static final int DB_VERSION = 1;

    public static final int ACTION_ADD = 3;
    public static final int ACTION_MODIFY = 4;

    public static final int PERM_REQ_EXTERNAL_STORAGE = 101;

    public final static int NOTIFICATION_PLAYER_ID = 0x342;

    public static final String EXTRA_ARG_ID = "com.gmail.webos21.radio.id";

    public static final String PREF_FILE = "radio_pref";
    public static final String PREF_PASSKEY = "pref_passkey";
    public static final String PREF_SHOW_ICON = "pref_show_icon";

    public static final String NOTI_CHANNEL = "com.gmail.webos21.radio.channel";

    public static final String TOGGLE_PLAY = "com.gmail.webos21.radio.action.TOGGLE_PLAY";
    public static final String FORWARD = "com.gmail.webos21.radio.action.FORWARD";
    public static final String REWIND = "com.gmail.webos21.radio.action.REWIND";
    public static final String CLOSE = "com.gmail.webos21.radio.action.CLOSE";

    public static final String PREPARED = "com.gmail.webos21.radio.action.PREPARED";
    public static final String PLAY_STATE_CHANGED = "com.gmail.webos21.radio.action.PLAY_STATE_CHANGED";

    public static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat SDF_DATETIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

}
