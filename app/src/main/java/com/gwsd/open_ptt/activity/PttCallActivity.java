package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWTempGroupBean;
import com.gwsd.bean.GWTempGroupNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.bean.OfflineEventBean;
import com.gwsd.open_ptt.manager.CallManager;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.view.AppTopView;
import com.gwsd.open_ptt.view.SpeakerVoiceDBAnimView;
import com.gwsd.open_ptt.view.VoiceSendingView;

public class PttCallActivity extends CommBusiActivity implements View.OnTouchListener{

    AppTopView aTHalfDuplex;
    SpeakerVoiceDBAnimView viewSpeakerAnim;
    TextView viewGroupName;
    TextView viewGroupId;
    TextView viewSpeakStatus;
    ImageView viewSpeakLed;
    ImageView viewVoiceCtrl;
    VoiceSendingView viewVoiceSendingView;

    private long id;
    private String name;
    private int type;
    private boolean create;
    private boolean voiceOpen = true;

    private boolean speakSucc = false;

    public static void startAct(Context context,long gid,String name, int type, boolean create) {
        Intent intent = new Intent(context, PttCallActivity.class);
        intent.putExtra("pttId", gid);
        intent.putExtra("pttName",name);
        intent.putExtra("pttType", type);
        intent.putExtra("pttCreate", create);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_pttcall;
    }

    @Override
    protected void release() {
        super.release();
        CallManager.getManager().exitPttTmpGroupCall();
        if (type == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER) {
            int[] ids = new int[1];
            ids[0] = 0;
            GWSDKManager.getSdkManager().tempGroup(ids, 1);
        }
        GWSDKManager.getSdkManager().registerPttObserver(null);
    }

    @Override
    protected void initData() {
        id = getIntent().getLongExtra("pttId", -1);
        name = getIntent().getStringExtra("pttName");
        type = getIntent().getIntExtra("pttType", 0);
        create = getIntent().getBooleanExtra("pttCreate" , false);
        if (type == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_GROUP
                || type == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_SELFGROUP) {
            log("ptt call group");
            GWSDKManager.getSdkManager().joinGroup(id,type);
        } else if (type == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER) {
            log("ptt call user");
            if (create) {
                int[] ids = new int[1];
                ids[0] = (int) id;
                GWSDKManager.getSdkManager().tempGroup(ids, 1);
            }
        }
        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_JOIN_GROUP){
                    runOnUiThread(() -> {
                        GWJoinGroupBean gwJoinGroupBean = JSON.parseObject(data, GWJoinGroupBean.class);
                        if (gwJoinGroupBean.getResult() == 0) {
                           log("jonin group success");
                        } else {
                            showToast(R.string.failure);
                            finish();
                        }
                    });
                }else if(event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK){
                    runOnUiThread(()->{
                        GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
                        if (gwSpeakNotifyBean.getUid() != 0) {
                            updateSpeaker(1, gwSpeakNotifyBean.getName());
                        } else {
                            updateSpeaker(2, "");
                        }
                    });
                }else if(event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_REQUEST_MIC){
                    runOnUiThread(()->{
                        GWRequestSpeakBean gwRequestSpeakBean = JSON.parseObject(data, GWRequestSpeakBean.class);
                        if (gwRequestSpeakBean.getResult() != 0) {
                            log("request speak fail");
                            showToast(R.string.failure);
                        } else {
                            log("request speak success");
                            updateSpeaker(1, getString(R.string.local_equipment_speak));
                            speakSucc = true;
                        }
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_ACTIVE) {
                    runOnUiThread(()->{
                        GWTempGroupBean gwTempGroupBean = JSON.parseObject(data, GWTempGroupBean.class);
                        if (gwTempGroupBean.getResult() == 0) {
                            log("tmp group success");
                        } else if (gwTempGroupBean.getResult() == 1) {
                            log("exit temp group");
                            finish();
                        } else {
                            log("tmp group fail");
                            finish();
                        }
                    });
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_PASSIVE) {
                    runOnUiThread(()->{
                        GWTempGroupNotifyBean gwTempGroupNotifyBean = JSON.parseObject(data, GWTempGroupNotifyBean.class);
                        if (gwTempGroupNotifyBean.getStatus() == 0) {
                            log("releast tmp group");
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }

    @Override
    protected void initView() {
        aTHalfDuplex = findViewById(R.id.viewHalfDuplexTopView);
        if (type == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER) {
            aTHalfDuplex.setTopTitle(R.string.title_TalkbackSoundActivity);
        } else {
            aTHalfDuplex.setTopTitle(R.string.group_voice_intercom);
        }
        viewGroupName = findViewById(R.id.viewGroupName);
        viewGroupId = findViewById(R.id.viewGroupId);
        viewSpeakerAnim = findViewById(R.id.viewSpeakerAnim);
        viewSpeakerAnim.setVisibility(View.VISIBLE);
        viewSpeakStatus = findViewById(R.id.viewSpeakStatus);
        viewSpeakLed = findViewById(R.id.viewSpeakLed);
        viewVoiceCtrl = findViewById(R.id.viewVoiceCtrl);
        viewSpeakStatus.setText("");
        viewGroupName.setText(String.format(getString(R.string.hint_poc_current_group), name));
        viewGroupId.setText(String.valueOf(id));
        viewVoiceSendingView = findViewById(R.id.view_VoiceSendingView);
        viewVoiceSendingView.cancalText();
        viewSpeakLed.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void initEvent() {
        viewVoiceSendingView.setOnTouchListener(this);
        aTHalfDuplex.setLeftClick(v ->{
            finish();
        });
        viewVoiceCtrl.setOnClickListener(v -> {
            if (voiceOpen) {
                viewVoiceCtrl.setBackgroundResource(R.mipmap.ic_close_voice);
                GWSDKManager.getSdkManager().mutePttSpk(true);
            } else {
                viewVoiceCtrl.setBackgroundResource(R.mipmap.ic_open_voice);
                GWSDKManager.getSdkManager().mutePttSpk(false);
            }
            voiceOpen = !voiceOpen;
        });
    }

    private void updateSpeaker(int state, String txt) {
        if (state == 1) {
            viewSpeakerAnim.startSpeakerAnim();
            viewSpeakLed.setVisibility(View.VISIBLE);
            if (txt.equals(getString(R.string.local_equipment_speak))) {
                viewSpeakLed.setBackgroundResource(R.drawable.selector_oval_red);
            } else {
                viewSpeakLed.setBackgroundResource(R.drawable.selector_oval_green);
            }
            viewSpeakStatus.setText(txt);
        } else {
            viewSpeakerAnim.stopSpeakerAnim();
            viewSpeakLed.setVisibility(View.INVISIBLE);
            viewSpeakStatus.setText(R.string.hint_Idle_waiting);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        touchHandler(event.getAction());
        return true;
    }

    private void touchHandler(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            GWSDKManager.getSdkManager().startSpeak();
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            GWSDKManager.getSdkManager().stopSpeak();
            if (speakSucc) {
                updateSpeaker(2, "");
            }
        }
    }

    @Override
    protected void processOffline(OfflineEventBean bean) {
        super.processOffline(bean);
        finish();
    }
}
