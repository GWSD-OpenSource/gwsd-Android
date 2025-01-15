package com.gwsd.open_ptt.bean;

import java.io.Serializable;

public class NotifiDataBean implements Serializable {

    public static final int NOTIFI_TYPE_MSG = 0;
    public static final int NOTIFI_TYPE_AUDIO_CALL = 1;
    public static final int NOTIFI_TYPE_VIDEO_CALL = 2;
    public static final int NOTIFI_TYPE_VIDEO_PULL = 3;
    public static final int NOTIFI_TYPE_VIDEO_MEETING = 4;
    public static final int NOTIFI_TYPE_CALL_END = 5;

    private String sendNm;
    private int recvType;  // user msg or group msg
    private String recvNm;
    private int recvId;
    private int msgType;
    private String content;
    private String recvIdStr;
    private boolean record;
    private int type = NOTIFI_TYPE_MSG; // 0 msg   1 audio call request  2 video call request
    private boolean forceNotice = false;

    public String getSendNm() {
        return sendNm;
    }

    public void setSendNm(String sendNm) {
        this.sendNm = sendNm;
    }

    public int getRecvType() {
        return recvType;
    }

    public void setRecvType(int recvType) {
        this.recvType = recvType;
    }

    public String getRecvNm() {
        return recvNm;
    }

    public void setRecvNm(String recvNm) {
        this.recvNm = recvNm;
    }

    public int getRecvId() {
        return recvId;
    }

    public void setRecvId(int recvId) {
        this.recvId = recvId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getRecvIdStr() {
        return recvIdStr;
    }

    public void setRecvIdStr(String recvIdStr) {
        this.recvIdStr = recvIdStr;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isForceNotice() {
        return forceNotice;
    }

    public void setForceNotice(boolean forceNotice) {
        this.forceNotice = forceNotice;
    }
}
