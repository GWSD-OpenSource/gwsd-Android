package com.gwsd.open_ptt.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.bean.OfflineEventBean;
import com.gwsd.open_ptt.bean.VideoStateParam;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.utils.Utils;
import com.gwsd.open_ptt.view.ChatVideoContentView;
import com.gwsd.open_ptt.view.ChatVideoViewContracts;
import com.gwsd.rtc.view.GWRtcSurfaceVideoRender;

public abstract class VideoCommBaseActivity extends CommBusiActivity implements ChatVideoViewContracts.OnVideoBtnCallback {

    protected RelativeLayout viewSurfaceGroup;
    protected GWRtcSurfaceVideoRender viewRenderLocal;
    protected GWRtcSurfaceVideoRender viewRenderRemote;
    protected FrameLayout viewFrameLayout;
    protected ChatVideoContentView videoContentView=null;

    protected String remoteid;
    protected String remoteNm;
    protected boolean caller;
    protected boolean record;
    protected int calltime;
    protected VideoStateParam videoStateParam;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                acceptVideo();
            } else {
                log("exit activity");
                //normal confirm
                finish();
            }
        }
    };

    @Override
    protected int getViewId() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        return R.layout.activity_video;
    }

    protected abstract void doInitVideoParam();

    protected abstract void doVideoAction();

    protected abstract void doAttachRemoteVideoView(boolean video, long uid);

    protected abstract void doAttachLocalVideoView();

    protected abstract void doSwitchCamera();

    protected abstract void doAcceptVideo();

    protected abstract void doMute(boolean mute, boolean local);

    @Override
    protected void initData() {
        super.initData();
        calltime = -1;
        Bundle bundle = getIntent().getExtras();
        remoteid = bundle.getString("remoteid");
        remoteNm = bundle.getString("remotenm");
        caller = bundle.getBoolean("caller");
        record = bundle.getBoolean("record");
        videoStateParam = new VideoStateParam();
        videoStateParam.setRemoteName(remoteNm)
                .setRemoteUID(remoteid);
        doInitVideoParam();
        if (caller) {
            videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_send_Call);
        } else {
            videoStateParam.setVideoStatus(ChatVideoViewContracts.VIDEO_View_Receive_OnCall);
        }
        GWSDKManager.getSdkManager().registerVideoObserver(new GWSDKManager.GWSDKVideoEngineObserver() {
            @Override
            public void onVideoPull(String s, String s1, int i, boolean b) {

            }

            @Override
            public void onVideoCall(String s, String s1) {

            }

            @Override
            public void onVideoMeetingInvite(String s, String s1) {

            }

            @Override
            public void onVideoMeetingCancel() {

            }

            @Override
            public void onVideoMeetingSpeak() {

            }

            @Override
            public void onVideoMeetingMute() {

            }

            @Override
            public void onVideoMeetingUserJoin(long videoId, String id, String name, boolean video) {

            }

            @Override
            public void onVideoMeetingSelfJoin() {

            }

            @Override
            public void onVideoMeetingUserLeave(long l) {

            }

            @Override
            public void onVideoMeetingKickout() {

            }

            @Override
            public void onLocalStreamReady() {
                runOnUiThread(()->{
                    doAttachLocalVideoView();
                });
            }

            @Override
            public void onRemoteStreamReady(boolean video, long uid) {
                runOnUiThread(()->{
                    doAttachRemoteVideoView(video, uid);
                });
            }

            @Override
            public void onRemoteStreamRemove() {
                runOnUiThread(()->{
                    GWSDKManager.getSdkManager().clearVideoView(viewRenderRemote);
                });
            }

            @Override
            public void onLocalStreamRemove() {
                runOnUiThread(()->{
                    GWSDKManager.getSdkManager().clearVideoView(viewRenderLocal);
                });
            }

            @Override
            public void onVideoData(byte[] bytes, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onHangup(String remoteid) {
                runOnUiThread(()->{
                    receiveVideoHangup();
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    protected void initView() {
        viewSurfaceGroup = findViewById(R.id.viewSurfaceGroup);
        viewRenderLocal = findViewById(R.id.viewRenderLocal);
        viewRenderRemote = findViewById(R.id.viewRenderRemote);
        viewFrameLayout = findViewById(R.id.viewFrameLayout);

        videoContentView=new ChatVideoContentView(getContext());
        viewFrameLayout.addView(videoContentView);
        doVideoAction();
        videoContentView.setUpdateVideoVState(videoStateParam);
        startTimer(1000);
    }

    @Override
    protected void initEvent() {
        videoContentView.setOnVideoBtnClick(this);
    }

    private void readyExit() {
        // delay some time
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    private void sendVideoHangup() {
        GWSDKManager.getSdkManager().hangupVideo(remoteid);
        stopTimer();
        videoContentView.setUpdateVideoVState(new VideoStateParam().setVideoStatus(ChatVideoViewContracts.VIDEO_View_send_Hangup));
        readyExit();
    }

    private void receiveVideoHangup() {
        stopTimer();
        videoContentView.setUpdateVideoVState(new VideoStateParam().setVideoStatus(ChatVideoViewContracts.VIDEO_View_Receive_Hangup));
        readyExit();
    }

    protected void acceptVideo() {
        doAcceptVideo();
    }

    @Override
    protected void release() {
        super.release();
        videoContentView = null;
        videoStateParam = null;
        GWSDKManager.getSdkManager().registerVideoObserver(null);
    }

    @Override
    public void onVideoBtnAccept() {
        handler.sendEmptyMessageDelayed(1, 300);
    }

    @Override
    public void onVideoBtnHangup() {
        sendVideoHangup();
    }

    @Override
    public void onChangeCamera() {
        doSwitchCamera();
    }

    @Override
    public void onMute(boolean mute, boolean local) {
        doMute(mute, local);
    }

    @Override
    protected void onTimer(int ts) {
        if(videoContentView!=null){
            videoContentView.setUpdateVideoVTime(Utils.intToTimer(ts));
        }
        if (calltime != -1) {
            calltime++;
        }
    }

    @Override
    protected void processOffline(OfflineEventBean bean) {
        super.processOffline(bean);
        sendVideoHangup();
    }
}
