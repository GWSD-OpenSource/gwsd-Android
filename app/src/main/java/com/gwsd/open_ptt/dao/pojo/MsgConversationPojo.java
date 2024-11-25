package com.gwsd.open_ptt.dao.pojo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "tb_msg_conversation")
public class MsgConversationPojo {

    @Id(autoincrement = true)
    Long tab_Id;
    String loginUId;
    Integer convId;
    Integer convType;
    String convNm;
    Integer msgCnt;
    Integer msgUnReadCnt;  // user msg or group msg

    Long lastMsgId;
    String lastMsgSenderNm;
    Integer lastMsgType;
    String lastMsgContent;
    Long lastMsgTime;
    @Generated(hash = 2038890172)
    public MsgConversationPojo(Long tab_Id, String loginUId, Integer convId,
            Integer convType, String convNm, Integer msgCnt, Integer msgUnReadCnt,
            Long lastMsgId, String lastMsgSenderNm, Integer lastMsgType,
            String lastMsgContent, Long lastMsgTime) {
        this.tab_Id = tab_Id;
        this.loginUId = loginUId;
        this.convId = convId;
        this.convType = convType;
        this.convNm = convNm;
        this.msgCnt = msgCnt;
        this.msgUnReadCnt = msgUnReadCnt;
        this.lastMsgId = lastMsgId;
        this.lastMsgSenderNm = lastMsgSenderNm;
        this.lastMsgType = lastMsgType;
        this.lastMsgContent = lastMsgContent;
        this.lastMsgTime = lastMsgTime;
    }
    @Generated(hash = 856585911)
    public MsgConversationPojo() {
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
    public String getConvNm() {
        return this.convNm;
    }
    public void setConvNm(String convNm) {
        this.convNm = convNm;
    }
    public Integer getMsgCnt() {
        return this.msgCnt;
    }
    public void setMsgCnt(Integer msgCnt) {
        this.msgCnt = msgCnt;
    }
    public Integer getMsgUnReadCnt() {
        return this.msgUnReadCnt;
    }
    public void setMsgUnReadCnt(Integer msgUnReadCnt) {
        this.msgUnReadCnt = msgUnReadCnt;
    }
    public Long getLastMsgId() {
        return this.lastMsgId;
    }
    public void setLastMsgId(Long lastMsgId) {
        this.lastMsgId = lastMsgId;
    }
    public String getLastMsgSenderNm() {
        return this.lastMsgSenderNm;
    }
    public void setLastMsgSenderNm(String lastMsgSenderNm) {
        this.lastMsgSenderNm = lastMsgSenderNm;
    }
    public Integer getLastMsgType() {
        return this.lastMsgType;
    }
    public void setLastMsgType(Integer lastMsgType) {
        this.lastMsgType = lastMsgType;
    }
    public String getLastMsgContent() {
        return this.lastMsgContent;
    }
    public void setLastMsgContent(String lastMsgContent) {
        this.lastMsgContent = lastMsgContent;
    }
    public Long getLastMsgTime() {
        return this.lastMsgTime;
    }
    public void setLastMsgTime(Long lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }



}

