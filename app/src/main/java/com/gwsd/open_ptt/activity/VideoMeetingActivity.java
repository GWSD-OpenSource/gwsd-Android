package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.bean.MeetingUserBean;
import com.gwsd.open_ptt.dialog.CancelConfirmDialog;
import com.gwsd.open_ptt.manager.CallManager;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.view.MeetingTopView;
import com.gwsd.rtc.view.GWRtcSurfaceVideoRender;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class VideoMeetingActivity extends CommBusiActivity {

    private MeetingTopView meetingTopView;
    private LinearLayout linearLayoutMeetingVideoView;

    GWRtcSurfaceVideoRender surfaceVideoRenderLocal;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote1;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote2;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote3;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote4;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote5;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote6;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote7;
    GWRtcSurfaceVideoRender surfaceVideoRenderRemote8;

    private Stack<GWRtcSurfaceVideoRender> availableRemoteRenderers = new Stack<>();
    private HashMap<Long, GWRtcSurfaceVideoRender> remoteRenderers = new HashMap<>();
    ArrayList<MeetingUserBean> enterMeetingMembers = new ArrayList<>();

    private String creater;
    private String topic;
    private boolean canSpeak = false;
    private boolean readyLeave = false;
    private boolean audioOnly = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            log("exit activity");
            //normal confirm

            finish();
        }
    };

    public static void navToAct(Context context, String creater, String topic) {
        Intent intent = new Intent(context, VideoMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("creater", creater);
        intent.putExtra("topic", topic);
        context.startActivity(intent);
    }

    public static Intent getStartIntent(Context context, String creater, String topic) {
        Intent intent = new Intent(context, VideoMeetingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("creater", creater);
        intent.putExtra("topic", topic);
        return intent;
    }

    @Override
    protected int getViewId() {
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        return R.layout.activity_videomeeting;
    }

    @Override
    protected void initData() {
        super.initData();
        creater = getIntent().getStringExtra("creater");
        topic = getIntent().getStringExtra("topic");
        log(creater + " invite join meeting " + topic);
        CallManager.getManager().enterAudioVideoCall();
    }

    @Override
    protected void initView() {
        meetingTopView = findViewById(R.id.viewAppTopView);
        if (TextUtils.isEmpty(topic)) {
            meetingTopView.setTopTitle(creater);
        } else {
            meetingTopView.setTopTitle(topic);
        }
        linearLayoutMeetingVideoView = findViewById(R.id.viewMeetingVideoWin);

        linearLayoutMeetingVideoView.removeAllViews();
        linearLayoutMeetingVideoView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT, 1);
        LinearLayout linearLayoutView = new LinearLayout(this);
        linearLayoutView.setOrientation(LinearLayout.HORIZONTAL);
        surfaceVideoRenderRemote1 = new GWRtcSurfaceVideoRender(this);
        availableRemoteRenderers.push(surfaceVideoRenderRemote1);
        linearLayoutView.addView(surfaceVideoRenderRemote1, params);
        View view = new View(this);
        linearLayoutView.addView(view, new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT));
        surfaceVideoRenderRemote2 = new GWRtcSurfaceVideoRender(this);
        linearLayoutView.addView(surfaceVideoRenderRemote2, params);
        availableRemoteRenderers.push(surfaceVideoRenderRemote2);
        view = new View(this);
        linearLayoutView.addView(view, new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT));
        surfaceVideoRenderRemote3 = new GWRtcSurfaceVideoRender(this);
        linearLayoutView.addView(surfaceVideoRenderRemote3, params);
        availableRemoteRenderers.push(surfaceVideoRenderRemote3);
        linearLayoutMeetingVideoView.addView(linearLayoutView, params);

        view = new View(this);
        linearLayoutMeetingVideoView.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));

        linearLayoutView = new LinearLayout(this);
        linearLayoutView.setOrientation(LinearLayout.HORIZONTAL);
        surfaceVideoRenderRemote4 = new GWRtcSurfaceVideoRender(this);
        availableRemoteRenderers.push(surfaceVideoRenderRemote4);
        linearLayoutView.addView(surfaceVideoRenderRemote4, params);
        view = new View(this);
        linearLayoutView.addView(view, new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT));
        surfaceVideoRenderRemote5 = new GWRtcSurfaceVideoRender(this);
        linearLayoutView.addView(surfaceVideoRenderRemote5, params);
        availableRemoteRenderers.push(surfaceVideoRenderRemote5);
        view = new View(this);
        linearLayoutView.addView(view, new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT));
        surfaceVideoRenderRemote6 = new GWRtcSurfaceVideoRender(this);
        linearLayoutView.addView(surfaceVideoRenderRemote6, params);
        availableRemoteRenderers.push(surfaceVideoRenderRemote6);
        linearLayoutMeetingVideoView.addView(linearLayoutView, params);

        view = new View(this);
        linearLayoutMeetingVideoView.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));

        linearLayoutView = new LinearLayout(this);
        linearLayoutView.setOrientation(LinearLayout.HORIZONTAL);
        surfaceVideoRenderRemote7 = new GWRtcSurfaceVideoRender(this);
        linearLayoutView.addView(surfaceVideoRenderRemote7, params);
        availableRemoteRenderers.push(surfaceVideoRenderRemote7);
        view = new View(this);
        linearLayoutView.addView(view, new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT));
        surfaceVideoRenderRemote8 = new GWRtcSurfaceVideoRender(this);
        linearLayoutView.addView(surfaceVideoRenderRemote8, params);
        availableRemoteRenderers.push(surfaceVideoRenderRemote8);
        view = new View(this);
        linearLayoutView.addView(view, new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT));
        surfaceVideoRenderLocal = new GWRtcSurfaceVideoRender(this);
        linearLayoutView.addView(surfaceVideoRenderLocal, params);
        linearLayoutMeetingVideoView.addView(linearLayoutView, params);
    }

    @Override
    protected void initEvent() {
        meetingTopView.setLeftImg1(R.drawable.selector_btn_mic);
        meetingTopView.setLeftImg1Click((v) -> {
            // selected mutelocal  not select open
            boolean isSelect;
            isSelect = v.isSelected();
            if (updateMuteLocal(isSelect) == true) {
                v.setSelected(!isSelect);
            }
        });
        meetingTopView.setLeft1Select(true);
        meetingTopView.setLeftImg2(R.drawable.selector_btn_spk);
        meetingTopView.setLeftImg2Click((v) -> {
            boolean isSelect;
            isSelect = v.isSelected();
            if (updateMuteRemote(isSelect) == true) {
                v.setSelected(!isSelect);
            }
        });
        meetingTopView.setLeft2Select(false);
        meetingTopView.setTopRightTx(R.string.exit);
        meetingTopView.setRightClick((v) -> {
            leaveVideoMeeting();
        });
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
                runOnUiThread(()->{
                    log("meeting cancel leave");
                    leaveVideoMeeting();
                });
            }

            @Override
            public void onVideoMeetingSpeak() {
                canSpeak = true;
            }

            @Override
            public void onVideoMeetingMute() {
                canSpeak = false;
            }

            @Override
            public void onVideoMeetingUserJoin(long videoid, String id, String name, boolean video) {
                runOnUiThread(()->{
                    log(id + " join meeting name " + name + " video:" + video);
                    showToast(String.format(getString(R.string.somebody_join_videomeeting), name));
                    //CallManager.getManager().changeToSpeaker();
                    MeetingUserBean meetingUserBean = new MeetingUserBean();
                    meetingUserBean.setUserId(Long.valueOf(id));
                    meetingUserBean.setUserName(name);
                    meetingUserBean.setUserVideoId(videoid);
                    meetingUserBean.setHasVideo(video);
                    enterMeetingMembers.add(meetingUserBean);
                    log("have " + enterMeetingMembers.size() + " nums");
                    if (video) {
                        GWRtcSurfaceVideoRender rtcSurfaceVideoRender = availableRemoteRenderers.pop();
                        if (rtcSurfaceVideoRender != null) {
                            GWSDKManager.getSdkManager().attachRemoteVideoView(rtcSurfaceVideoRender, videoid);
                            remoteRenderers.put(videoid, rtcSurfaceVideoRender);
                        }
                    }
                    CallManager.getManager().changeToSpeaker();
                });
            }

            @Override
            public void onVideoMeetingSelfJoin() {
                GWSDKManager.getSdkManager().updateBitrate(256);
                runOnUiThread(()->{
                    if (audioOnly) {

                    } else {
                        GWSDKManager.getSdkManager().attachLocalVideoView(surfaceVideoRenderLocal);
                    }
                });
            }

            @Override
            public void onVideoMeetingUserLeave(long userId) {
                runOnUiThread(()->{
                    MeetingUserBean tmp = new MeetingUserBean();
                    int index = 0;
                    boolean find = false;
                    for (MeetingUserBean meetingUserBean : enterMeetingMembers) {
                        if (meetingUserBean.getUserVideoId() == userId) {
                            tmp = meetingUserBean;
                            find = true;
                            break;
                        }
                        index++;
                    }
                    if (find) {
                        enterMeetingMembers.remove(index);
                        log("left " + enterMeetingMembers.size() + " nums");
                        log(userId + " leaved name "+tmp.getUserName());
                        String leaveUserNm = tmp.getUserName();
                        GWRtcSurfaceVideoRender surfaceVideoRender = remoteRenderers.get(userId);
                        if (surfaceVideoRender != null) {
                            availableRemoteRenderers.push(surfaceVideoRender);
                            remoteRenderers.remove(userId);
                            GWSDKManager.getSdkManager().clearVideoView(surfaceVideoRender);
                        }
                        showToast(String.format(getString(R.string.somebody_leave_videomeeting), leaveUserNm));
                    }
                });
            }

            @Override
            public void onVideoMeetingKickout() {
                runOnUiThread(()->{
                    log("member kickout");
                    leaveVideoMeeting();
                });
            }

            @Override
            public void onLocalStreamReady() {

            }

            @Override
            public void onRemoteStreamReady(boolean b, long l) {

            }

            @Override
            public void onRemoteStreamRemove() {

            }

            @Override
            public void onLocalStreamRemove() {

            }

            @Override
            public void onVideoData(byte[] bytes, int i, int i1, int i2, int i3) {

            }

            @Override
            public void onHangup(String s) {

            }

            @Override
            public void onError(int i, String s) {
                GWSDKManager.getSdkManager().rejectVideoMeeting(creater, "error");
                runOnUiThread(()->{
                    leaveVideoMeeting();
                });
            }
        });

        showMeetingInviteDialog(creater, topic);
    }

    @Override
    protected void release() {
        super.release();
        GWSDKManager.getSdkManager().registerVideoObserver(null);
        CallManager.getManager().exitAudioVideoCall(1);
    }

    private void showMeetingInviteDialog(String name, String desc) {
        final CancelConfirmDialog cancelConfirmDialog;
        cancelConfirmDialog = CancelConfirmDialog.build(this);

        cancelConfirmDialog.setImagLog(MyApp.getAppResLog());
        String hintCon = String.format(getString(R.string.invite_somebody_videomeeting), name, desc);
        String leftBtnStr = getString(R.string.btn_cancel);
        String rightBtnStr = getString(R.string.btn_affirm);

        cancelConfirmDialog.setContentText(hintCon, leftBtnStr, rightBtnStr);
        cancelConfirmDialog.setImagLog(MyApp.getAppResLog());
        cancelConfirmDialog.show();
        cancelConfirmDialog.setOnClickTypeListener(new CancelConfirmDialog.OnClickTypeListener() {
            @Override
            public void onClick(View view, int type) {
                if (type == CancelConfirmDialog.OnClickTypeListener.CLICK_TYPE_RIFGHT_BTN) {
                    log("agree join meeting");
                    GWSDKManager.getSdkManager().joinVideoMeeting();
                } else {
                    log("reject join meeting");
                    GWSDKManager.getSdkManager().rejectVideoMeeting(creater, "user");
                    finish();
                }
                cancelConfirmDialog.dismiss();
            }
        });
        cancelConfirmDialog.setCanceledOnTouchOutside(false);
    }

    private boolean updateMuteLocal(boolean b) {
        if (b) {
            log("local not mute");
            if (canSpeak) {
                GWSDKManager.getSdkManager().muteMic(false);
                return true;
            } else {
                showToast(R.string.hint_refuse_speak_in_video_meeting);
                return false;
            }
        } else {
            //close local
            GWSDKManager.getSdkManager().muteMic(true);
            log("local mute");
            return true;
        }
    }

    private boolean updateMuteRemote(boolean b) {
        if (b) {
            log("remote not mute");
            GWSDKManager.getSdkManager().muteSpk(false);
            return true;
        } else {
            //close remote
            GWSDKManager.getSdkManager().muteSpk(true);
            log("remote mute");
            return true;
        }
    }

    private void leaveVideoMeeting() {
        if (readyLeave) {
            log("leaving...");
            return;
        }
        Iterator iter = remoteRenderers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            GWRtcSurfaceVideoRender val = (GWRtcSurfaceVideoRender) entry.getValue();
            val.release();
        }
        log("not creater meeting user leave ready exit act");
        GWSDKManager.getSdkManager().leaveVideoMeeting();
        handler.sendEmptyMessageDelayed(0, 1000);
        readyLeave = true;
    }
}
