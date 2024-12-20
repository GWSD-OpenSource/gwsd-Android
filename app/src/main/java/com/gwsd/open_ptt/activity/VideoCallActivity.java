package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;

import com.gwsd.GWVideoEngine;
import com.gwsd.bean.GWMsgBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.bean.NotifiDataBean;
import com.gwsd.open_ptt.dao.MsgDaoHelp;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;
import com.gwsd.open_ptt.dao.pojo.MsgConversationPojo;
import com.gwsd.open_ptt.manager.AppManager;
import com.gwsd.open_ptt.manager.CallManager;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.service.MainService;
import com.gwsd.open_ptt.utils.VideoWinSwitchUtil;
import com.gwsd.open_ptt.view.ChatVideoViewContracts;

import org.greenrobot.eventbus.EventBus;

public class VideoCallActivity extends VideoCommBaseActivity {

    private VideoWinSwitchUtil videoWinSwitchUtil;

    public static void startAct(Context context, String remoteid, String remotenm, boolean caller, boolean record) {
        if (!AppManager.getInstance().isForeground()) {
            NotifiDataBean notifiDataBean = new NotifiDataBean();
            notifiDataBean.setRecvNm(remotenm);
            notifiDataBean.setRecvIdStr(remoteid);
            notifiDataBean.setRecord(record);
            notifiDataBean.setType(NotifiDataBean.NOTIFI_TYPE_VIDEO_CALL);
            MainService.startServerWithData(AppManager.getApp(), notifiDataBean);
        } else {
            Intent intent = new Intent(context, VideoCallActivity.class);
            intent.putExtra("remoteid", remoteid);
            intent.putExtra("remotenm", remotenm);
            intent.putExtra("caller", caller);
            intent.putExtra("record", record);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static Intent getStartIntent(Context context, String remoteid, String remotenm, boolean record) {
        Intent intent = new Intent(context, VideoCallActivity.class);
        intent.putExtra("remoteid", remoteid);
        intent.putExtra("remotenm", remotenm);
        intent.putExtra("caller", false);
        intent.putExtra("record", record);
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
    protected void doInitVideoParam() {
        videoStateParam.setDuplex(true);
    }

    @Override
    protected void doVideoAction() {
        videoWinSwitchUtil = new VideoWinSwitchUtil(this);
        videoWinSwitchUtil.setVideoView(viewSurfaceGroup, viewRenderRemote, viewRenderLocal);
        videoWinSwitchUtil.addClickListener();
        videoWinSwitchUtil.changeLocalSmall();
        if (caller) {
            GWSDKManager.getSdkManager().callVideo(remoteid, record, GWVideoEngine.GWVideoResolution.GW_VIDEO_RESOLUTION_SMOOTH);
        } else {
            log("recv user "+remoteNm+" video call request");
        }
    }

    @Override
    protected void doAttachRemoteVideoView(boolean video, long uid) {
        if (video) {
            videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_send_Accept);
            videoContentView.setUpdateVideoVState(videoStateParam);
            GWSDKManager.getSdkManager().attachRemoteVideoView(viewRenderRemote, uid);
            CallManager.getManager().changeToSpeaker();
            calltime = 1;
        } else {
            //showToast("remote stream ready not have video");
        }
    }

    @Override
    protected void doAttachLocalVideoView() {
        GWSDKManager.getSdkManager().attachLocalVideoView(viewRenderLocal);
    }

    @Override
    protected void doSwitchCamera() {
        GWSDKManager.getSdkManager().switchCamera();
    }

    @Override
    protected void doAcceptVideo() {
        GWSDKManager.getSdkManager().acceptCallVideo();
    }

    @Override
    protected void doMute(boolean mute, boolean local) {
        if (local){
            if (mute) {//1、down, open，0、up,close
                GWSDKManager.getSdkManager().muteMic(true);
            } else {
                GWSDKManager.getSdkManager().muteMic(false);
            }
        }else {
            if (mute) {//1、down, open，0、up,close
                GWSDKManager.getSdkManager().muteSpk(true);
            } else {
                GWSDKManager.getSdkManager().muteSpk(false);
            }
        }
    }

    @Override
    protected void release() {
        super.release();
        CallManager.getManager().exitAudioVideoCall(1);
        saveCallRecord();
    }

    private void saveCallRecord() {
        GWMsgBean gwMsgBean = null;
        MsgContentPojo msgContentPojo = null;
        MsgConversationPojo msgConversationPojo = null;
        if (calltime < 0) {
            calltime = 0;
        }
        if (caller) {
            int id;
            id = Integer.valueOf(remoteid);
            gwMsgBean = GWSDKManager.getSdkManager().createMsgBean(GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER, id, remoteNm, 1);
            gwMsgBean.getData().setContent(String.valueOf(calltime));
            msgContentPojo = MsgDaoHelp.saveMsgContent(getUid(), gwMsgBean);
            msgConversationPojo = MsgDaoHelp.saveOrUpdateConv(msgContentPojo, false);
        } else {
            gwMsgBean = GWSDKManager.getSdkManager().createMsgBean1(remoteid, remoteNm, GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER, getUid(), getUnm(), 1);
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
}
