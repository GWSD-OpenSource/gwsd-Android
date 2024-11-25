package com.gwsd.open_ptt.bean;

import java.io.Serializable;

public class NotifiDataBean implements Serializable {

    private String sendNm;
    private int recvType;  // user msg or group msg
    private String recvNm;
    private int recvId;
    private int msgType;
    private String content;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
