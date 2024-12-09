package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWDuplexBean;
import com.gwsd.bean.GWMsgBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.bean.NotifiDataBean;
import com.gwsd.open_ptt.bean.OfflineEventBean;
import com.gwsd.open_ptt.dao.MsgDaoHelp;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;
import com.gwsd.open_ptt.dao.pojo.MsgConversationPojo;
import com.gwsd.open_ptt.manager.AppManager;
import com.gwsd.open_ptt.manager.CallManager;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.service.MainService;
import com.gwsd.open_ptt.utils.Utils;

import org.greenrobot.eventbus.EventBus;

public class AudioCallActivity extends CommBusiActivity{

    TextView tVCallUser;
    TextView tVTimer;
    ImageView iVMic;
    ImageView iVBullhorn;
    ImageView iVHungUp;
    ImageView iVAccept;

    int remoteid;
    String remoteNm;
    boolean caller;
    int calltime;

    public static void startAct(Context context, int remoteid, String remotenm, boolean caller, boolean notification) {
        if (notification) {
            NotifiDataBean notifiDataBean = new NotifiDataBean();
            notifiDataBean.setRecvNm(remotenm);
            notifiDataBean.setRecvId(remoteid);
            notifiDataBean.setType(NotifiDataBean.NOTIFI_TYPE_AUDIO_CALL);
            MainService.startServerWithData(AppManager.getApp(), notifiDataBean);
        } else {
            Intent intent = new Intent(context, AudioCallActivity.class);
            intent.putExtra("remoteid", remoteid);
            intent.putExtra("remotenm", remotenm);
            intent.putExtra("caller", caller);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static Intent getStartIntent(Context context, int remoteid, String remotenm) {
        Intent intent = new Intent(context, AudioCallActivity.class);
        intent.putExtra("remoteid", remoteid);
        intent.putExtra("remotenm", remotenm);
        intent.putExtra("caller", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private String getUid() {
        String uid = String.valueOf(GWSDKManager.getSdkManager().getUserInfo().getId());
        return uid;
    }

    private String getUnm() {
        return GWSDKManager.getSdkManager().getUserInfo().getName();
    }

    @Override
    protected int getViewId() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        return R.layout.activity_voicecall;
    }

    protected void initView(){
        tVCallUser = findViewById(R.id.viewAudioCallNm);
        tVTimer = findViewById(R.id.viewAudioCallTime);
        iVMic = findViewById(R.id.viewAudioMuteLocal);
        iVBullhorn = findViewById(R.id.viewAudioHandFree);
        iVHungUp = findViewById(R.id.viewAudioHangup);
        iVAccept = findViewById(R.id.viewAudioAccept);

        iVMic.setVisibility(View.INVISIBLE);
        iVBullhorn.setVisibility(View.INVISIBLE);
        if (caller) {
            iVAccept.setVisibility(View.GONE);
        }
        startTimer(1000);
        tVCallUser.setText(remoteNm);
    }

    protected void initData(){
        calltime = -1;
        Bundle bundle = getIntent().getExtras();
        remoteid = bundle.getInt("remoteid");
        remoteNm = bundle.getString("remotenm");
        caller = bundle.getBoolean("caller");
        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int var3) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_DUPLEX){
                    GWDuplexBean gwDuplexBean = JSON.parseObject(data, GWDuplexBean.class);
                    if (gwDuplexBean.getResult() == 0){
                        if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_ACCEPTED){
                            runOnUiThread(()->{
                                iVMic.setVisibility(View.VISIBLE);
                                iVMic.setSelected(false);
                                iVBullhorn.setVisibility(View.VISIBLE);
                                iVBullhorn.setSelected(true);
                                iVAccept.setVisibility(View.GONE);
                                CallManager.getManager().changeToHandset();
                                calltime = 1;
                            });
                        }else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_END){
                            runOnUiThread(()->{
                                log("call end finish");
                                finish();
                            });
                        }else if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_START) {
                            log("call success");
                        }
                    } else {
                        log("call error:"+gwDuplexBean.getResult());
                        runOnUiThread(()->{
                            finish();
                        });
                    }
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
        if (caller) {
            GWSDKManager.getSdkManager().fullDuplex(remoteid, GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACTION_CREATE);
        } else {
            log("recv user:"+remoteNm+" voice call request");
        }
    }

    protected void initEvent(){
        iVMic.setOnClickListener(v ->{
            if (iVMic.isSelected()) {
                GWSDKManager.getSdkManager().mutePttMic(false);
                iVMic.setSelected(false);
            }else{
                GWSDKManager.getSdkManager().mutePttMic(true);
                iVMic.setSelected(true);
            }
        });
        iVBullhorn.setOnClickListener(v->{
            if (iVBullhorn.isSelected()) {
                CallManager.getManager().changeToSpeaker();
                iVBullhorn.setSelected(false);
            }else{
                CallManager.getManager().changeToHandset();
                iVBullhorn.setSelected(true);
            }
        });
        iVHungUp.setOnClickListener(v->{
            GWSDKManager.getSdkManager().fullDuplex(remoteid, GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACTION_HANGUP);
        });
        iVAccept.setOnClickListener(v -> {
            GWSDKManager.getSdkManager().fullDuplex(remoteid, GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACTION_ACCEPT);
        });
    }

    @Override
    protected void release() {
        super.release();
        stopTimer();
        CallManager.getManager().exitAudioVideoCall();
        GWSDKManager.getSdkManager().registerPttObserver(null);
        saveCallRecord();
    }

    private void saveCallRecord() {
        GWMsgBean gwMsgBean = null;
        MsgContentPojo msgContentPojo = null;
        MsgConversationPojo msgConversationPojo = null;
        if (caller) {
            gwMsgBean = GWSDKManager.getSdkManager().createMsgBean(GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER, remoteid, remoteNm, 0);
            gwMsgBean.getData().setContent(String.valueOf(calltime));
            msgContentPojo = MsgDaoHelp.saveMsgContent(getUid(), gwMsgBean);
            msgConversationPojo = MsgDaoHelp.saveOrUpdateConv(msgContentPojo, false);
        } else {
            gwMsgBean = GWSDKManager.getSdkManager().createMsgBean1(String.valueOf(remoteid), remoteNm, GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER, getUid(), getUnm(), 0);
            gwMsgBean.getData().setContent(String.valueOf(calltime));
            msgContentPojo = MsgDaoHelp.saveMsgContent(getUid(), gwMsgBean);
            boolean unreadflag = false;
            if (calltime > 0) {
                // call establisth should not show unread
                unreadflag = true;
            } else {
                unreadflag = false;
            }
            msgConversationPojo = MsgDaoHelp.saveOrUpdateConv(msgContentPojo, unreadflag);
        }
        EventBus.getDefault().post(msgContentPojo);
        EventBus.getDefault().post(msgConversationPojo);
    }

    @Override
    protected void onTimer(int ts) {
        if(tVTimer!=null){
            tVTimer.setText(Utils.intToTimer(ts));
        }
        if (calltime != -1) {
            calltime++;
        }
    }

    @Override
    protected void processOffline(OfflineEventBean bean) {
        super.processOffline(bean);
        finish();
    }
}

