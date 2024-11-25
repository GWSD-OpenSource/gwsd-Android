package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.bean.VideoStateParam;

public class ChatVideoContentView extends ChatVideoBaseView {

    TextView viewTimer;
    TextView viewVideoState;
    RelativeLayout viewBtnContentTopItem;
    ImageView viewVideoMuteLocal;
    ImageView viewVideoMuteRemote;
    ImageView viewHangup;
    ImageView viewAccept;
    ImageView viewSwitchCamera;
    LinearLayout viewBtnContentRoot;
    LinearLayout viewBtnContentBottomItem;


    public ChatVideoContentView(Context context) {
        super(context);
    }
    public ChatVideoContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ChatVideoContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected int getContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        return R.layout.view_video_control;
    }

    @Override
    protected void onViewInitSuc(Context context, AttributeSet attrs, int defStyleAttr) {
        viewTimer=findViewById(R.id.viewTimer);
        viewVideoState=findViewById(R.id.viewVideoState);
        viewBtnContentTopItem=findViewById(R.id.viewBtnContentTopItem);
        viewBtnContentBottomItem=findViewById(R.id.viewBtnContentBottomItem);
        viewVideoMuteLocal=findViewById(R.id.viewVideoMuteLocal);
        viewVideoMuteRemote=findViewById(R.id.viewVideoMuteRemote);
        viewHangup=findViewById(R.id.viewHangup);
        viewAccept=findViewById(R.id.viewAccept);
        viewSwitchCamera=findViewById(R.id.viewVideoSwitchCamera);
        viewBtnContentRoot=findViewById(R.id.viewBtnContentRoot);;

        viewVideoMuteLocal.setOnClickListener(this);
        viewVideoMuteRemote.setOnClickListener(this);
        viewHangup.setOnClickListener(this);
        viewAccept.setOnClickListener(this);
        viewSwitchCamera.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.viewHangup:
                if(hasQuickClick())return;
                if(getVideoBtnCallback()!=null){
                    getVideoBtnCallback().onVideoBtnHangup();
                }
                break;
            case R.id.viewAccept:
                if(hasQuickClick())return;
                if(getVideoBtnCallback()!=null){
                    getVideoBtnCallback().onVideoBtnAccept();
                }
                break;
            case R.id.viewVideoMuteLocal:
                if(hasQuickClick())return;
                updateMuteState(true);
                break;
            case R.id.viewVideoMuteRemote:
                if(hasQuickClick())return;
                updateMuteState(false);
                break;
            case R.id.viewVideoSwitchCamera:
                if(hasQuickClick())return;
                updateCamera();
                break;
        }
    }

    private void updateCamera(){
        getVideoBtnCallback().onChangeCamera();
    }


    private void updateMuteState(boolean local){
        boolean isSelect;
        if (local){
            isSelect = viewVideoMuteLocal.isSelected();
            viewVideoMuteLocal.setSelected(!isSelect);
        }else{
            isSelect = viewVideoMuteRemote.isSelected();
            viewVideoMuteRemote.setSelected(!isSelect);
        }
        getVideoBtnCallback().onMute(!isSelect, local);

    }
    @Override
    public void setPttSelect(boolean hasSelect) {
//        if(hasSelect){
//            viewVideoPtt.setSelected(true);
//        }else {
//            viewVideoPtt.setSelected(false);
//        }
//        updatePttState();
    }
    @Override
    public void setUpdateVideoVTime(String time) {
        if(viewTimer!=null){
            viewTimer.setText(time);
        }
    }

    @Override
    public void setUpdateBtnRecord(boolean hasSelect) {

    }

    @Override
    public void setHideContentView() {

    }

    @Override
    public void setShowContentView() {

    }

    public void setBtnAllEnabled(boolean enabled) {
        //viewVideoPtt.setEnabled(false);
        viewAccept.setEnabled(false);
        viewHangup.setEnabled(false);
    }

    public void setBtnAcceptVisibility(int visibility){
        if(viewAccept!=null)viewAccept.setVisibility(visibility);
    }
    public void setBtnAcceptEnabled(boolean enable){
        if(viewAccept!=null)viewAccept.setEnabled(enable);
    }

    @Override
    public void setUpdateVideoVState(VideoStateParam vStateParam) {
        if(viewHangup==null){
            return;
        }
        int sessionState=vStateParam.getVideoStatus();
        String remoteUName=vStateParam.getRemoteName();
        boolean isDuplex=vStateParam.isDuplex();
        if(sessionState== ChatVideoViewContracts.VIDEO_View_send_Call){
            viewAccept.setVisibility(GONE);
            if(isDuplex){
                viewVideoState.setText(stringFormat(R.string.request_somebody_video_chat,remoteUName));
                viewSwitchCamera.setVisibility(GONE);
                viewVideoMuteLocal.setVisibility(GONE);
                viewVideoMuteRemote.setVisibility(GONE);
            }else {
                viewVideoState.setText(stringFormat(R.string.request_pull_somebody_video,remoteUName));
                viewVideoMuteLocal.setVisibility(GONE);
            }
        }else if(sessionState== ChatVideoViewContracts.VIDEO_View_send_Accept){
            viewAccept.setVisibility(GONE);
            if(isDuplex){
                viewVideoState.setText(stringFormat(R.string.talking_somebody_video_chat,remoteUName));
                viewSwitchCamera.setVisibility(VISIBLE);
                viewVideoMuteLocal.setVisibility(VISIBLE);
                viewVideoMuteRemote.setVisibility(VISIBLE);
                viewHangup.setVisibility(VISIBLE);
            }else {
                viewVideoState.setText(stringFormat(R.string.see_video,remoteUName));
                viewVideoMuteRemote.setVisibility(GONE);
            }
        } else if(sessionState== ChatVideoViewContracts.VIDEO_View_Receive_OnCall){
            if(isDuplex){
                viewVideoState.setText(stringFormat(R.string.somebody_request_video_chat,remoteUName));
                viewSwitchCamera.setVisibility(GONE);
                viewVideoMuteLocal.setVisibility(GONE);
                viewVideoMuteRemote.setVisibility(GONE);
            }else {
                viewVideoState.setText(stringFormat(R.string.somebody_request_pull_video,remoteUName));
                viewVideoMuteRemote.setVisibility(GONE);
            }
        }else if(sessionState== ChatVideoViewContracts.VIDEO_View_Receive_OnAccept){
            viewAccept.setVisibility(GONE);
            if (isDuplex){
                viewVideoState.setText(stringFormat(R.string.talking_somebody_video_chat,remoteUName));
                viewSwitchCamera.setVisibility(VISIBLE);
                viewVideoMuteLocal.setVisibility(VISIBLE);
                viewVideoMuteRemote.setVisibility(VISIBLE);
                viewHangup.setVisibility(VISIBLE);
            }else{
                viewVideoState.setText(stringFormat(R.string.pulling_somebody_video,remoteUName));
                viewVideoMuteLocal.setVisibility(GONE);
            }
        }else if(sessionState== ChatVideoViewContracts.VIDEO_View_send_Hangup){
            viewVideoState.setText(R.string.hangup_video);
            setBtnAllEnabled(false);
        } else if(sessionState== ChatVideoViewContracts.VIDEO_View_Receive_Hangup){
            viewVideoState.setText(R.string.hint_talkback_state_opposite_hangup);
            setBtnAllEnabled(false);
        }
        else if(sessionState== ChatVideoViewContracts.VIDEO_View_Receive_Err
                ||sessionState== ChatVideoViewContracts.VIDEO_View_Snd_Err ){
            viewVideoState.setText(R.string.hint_chatvideo_err);
            setBtnAllEnabled(false);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
