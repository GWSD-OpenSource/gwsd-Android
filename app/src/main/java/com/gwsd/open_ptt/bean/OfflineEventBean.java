package com.gwsd.open_ptt.bean;

public class OfflineEventBean {

    public static int OFFLINE_REASON_LOGOUT_CODE = -1;
    public static int OFFLINE_REASON_KICKOUT_CODE = -2;
    public static int OFFLINE_REASON_ERROR_CODE = -3;

    int code;
    String reason;

    public OfflineEventBean(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
