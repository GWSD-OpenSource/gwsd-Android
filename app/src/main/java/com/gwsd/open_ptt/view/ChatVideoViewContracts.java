package com.gwsd.open_ptt.view;

import com.gwsd.open_ptt.bean.VideoStateParam;

public interface ChatVideoViewContracts {

    public final int VIDEO_View_send_Call=11;//
    public final int VIDEO_View_send_Accept=12;//
    public final int VIDEO_View_send_Hangup=13;//
    public final int VIDEO_View_Receive_OnCall=14;//
    public final int VIDEO_View_Receive_OnRing=15;//
    public final int VIDEO_View_Receive_OnAccept=16;//
    public final int VIDEO_View_Receive_Hangup=17;//

    public final int VIDEO_View_Snd_Err=18;//
    public final int VIDEO_View_Receive_Err=19;//

    public final int VIDEO_View_Snd_Conn=20;//
    public final int VIDEO_View_Receive_Conn=21;//

    public final int VIDEO_View_CallTo_TimerOut=30;//

    interface PVideoVUpdate{
        void setOnVideoBtnClick(OnVideoBtnCallback videoVBtnClick);
        void setUpdateVideoVTime(String time);
        void setUpdateVideoVState(VideoStateParam vStateParam);
        void setPttSelect(boolean hasSelect);
        void setUpdateBtnRecord(boolean hasSelect);
        void setHideContentView();
        void setShowContentView();
    }
    interface OnVideoBtnCallback{
        void onVideoBtnAccept();
        void onVideoBtnHangup();
        void onChangeCamera();
        void onMute(boolean mute, boolean local);
    }
}
