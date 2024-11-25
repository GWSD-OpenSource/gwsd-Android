package com.gwsd.open_ptt.dao.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "tb_msg_content")
public class MsgContentPojo {

    @Id(autoincrement = true)
    Long tab_Id;

    String loginUId;
    Integer convId;
    Integer convType;

    String senderId;
    String senderNm;
    Integer senderType;
    String recvId;
    String recvNm;
    Integer recvType;

    Integer msgType;
    String content;
    String url;
    String thumburl;
    Integer playtime;
    Long time;
    @Generated(hash = 319642586)
    public MsgContentPojo(Long tab_Id, String loginUId, Integer convId,
            Integer convType, String senderId, String senderNm, Integer senderType,
            String recvId, String recvNm, Integer recvType, Integer msgType,
            String content, String url, String thumburl, Integer playtime,
            Long time) {
        this.tab_Id = tab_Id;
        this.loginUId = loginUId;
        this.convId = convId;
        this.convType = convType;
        this.senderId = senderId;
        this.senderNm = senderNm;
        this.senderType = senderType;
        this.recvId = recvId;
        this.recvNm = recvNm;
        this.recvType = recvType;
        this.msgType = msgType;
        this.content = content;
        this.url = url;
        this.thumburl = thumburl;
        this.playtime = playtime;
        this.time = time;
    }
    @Generated(hash = 1441264849)
    public MsgContentPojo() {
    }
    public Long getTab_Id() {
        return this.tab_Id;
    }
    public void setTab_Id(Long tab_Id) {
        this.tab_Id = tab_Id;
    }
    public String getLoginUId() {
        return this.loginUId;
    }
    public void setLoginUId(String loginUId) {
        this.loginUId = loginUId;
    }
    public Integer getConvId() {
        return this.convId;
    }
    public void setConvId(Integer convId) {
        this.convId = convId;
    }
    public Integer getConvType() {
        return this.convType;
    }
    public void setConvType(Integer convType) {
        this.convType = convType;
    }
    public String getSenderId() {
        return this.senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getSenderNm() {
        return this.senderNm;
    }
    public void setSenderNm(String senderNm) {
        this.senderNm = senderNm;
    }
    public Integer getSenderType() {
        return this.senderType;
    }
    public void setSenderType(Integer senderType) {
        this.senderType = senderType;
    }
    public String getRecvId() {
        return this.recvId;
    }
    public void setRecvId(String recvId) {
        this.recvId = recvId;
    }
    public String getRecvNm() {
        return this.recvNm;
    }
    public void setRecvNm(String recvNm) {
        this.recvNm = recvNm;
    }
    public Integer getRecvType() {
        return this.recvType;
    }
    public void setRecvType(Integer recvType) {
        this.recvType = recvType;
    }
    public Integer getMsgType() {
        return this.msgType;
    }
    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getThumburl() {
        return this.thumburl;
    }
    public void setThumburl(String thumburl) {
        this.thumburl = thumburl;
    }
    public Integer getPlaytime() {
        return this.playtime;
    }
    public void setPlaytime(Integer playtime) {
        this.playtime = playtime;
    }
    public Long getTime() {
        return this.time;
    }
    public void setTime(Long time) {
        this.time = time;
    }
}
