package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.gwsd.GWVideoEngine;
import com.gwsd.open_ptt.manager.CallManager;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.view.ChatVideoViewContracts;

public class VideoViewActivity extends VideoCommBaseActivity {

    public static void startAct(Context context, String remoteid, String remotenm, boolean caller, boolean record) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra("remoteid", remoteid);
        intent.putExtra("remotenm", remotenm);
        intent.putExtra("caller", caller);
        intent.putExtra("record", record);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    public static Intent getStartIntent(Context context, String remoteid, String remotenm, boolean record) {
        Intent intent = new Intent(context, VideoViewActivity.class);
        intent.putExtra("remoteid", remoteid);
        intent.putExtra("remotenm", remotenm);
        intent.putExtra("caller", false);
        intent.putExtra("record", record);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void doInitVideoParam() {
        videoStateParam.setDuplex(false);
        CallManager.getManager().enterAudioVideoCall();
    }

    @Override
    protected void doVideoAction() {
        viewRenderRemote.setVisibility(View.GONE);
        if (caller) {
            GWSDKManager.getSdkManager().pullVideo(remoteid, false, false,
                    GWVideoEngine.GWVideoPriority.GW_VIDEO_PRIORITY_RESOLUTION,
                    GWVideoEngine.GWVideoResolution.GW_VIDEO_RESOLUTION_HD);
        } else {
            log("recv user "+remoteNm+" video pull request");
        }
    }

    @Override
    protected void doAttachRemoteVideoView(boolean video, long uid) {
        videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_Receive_OnAccept);
        videoContentView.setUpdateVideoVState(videoStateParam);
        GWSDKManager.getSdkManager().attachRemoteVideoView(viewRenderLocal, uid);
    }

    @Override
    protected void doAttachLocalVideoView() {
        videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_send_Accept);
        videoContentView.setUpdateVideoVState(videoStateParam);
        GWSDKManager.getSdkManager().attachLocalVideoView(viewRenderLocal);
    }

    @Override
    protected void doSwitchCamera() {
        if (caller) {

        } else {
            GWSDKManager.getSdkManager().switchCamera();
        }
    }

    @Override
    protected void doAcceptVideo() {
        GWSDKManager.getSdkManager().acceptPullVideo(2, false);
    }

    @Override
    protected void doMute(boolean mute, boolean local) {
        log("mute="+mute+",local="+local);
    }

    @Override
    protected void release() {
        super.release();
        CallManager.getManager().exitAudioVideoCall(1);
    }
}
