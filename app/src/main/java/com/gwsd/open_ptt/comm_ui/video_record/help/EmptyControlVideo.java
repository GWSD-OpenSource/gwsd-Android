package com.gwsd.open_ptt.comm_ui.video_record.help;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * 无任何控制ui的播放
 * Created by guoshuyu on 2017/8/6.
 */

public class EmptyControlVideo extends StandardGSYVideoPlayer {

    public EmptyControlVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public EmptyControlVideo(Context context) {
        super(context);
    }

    public EmptyControlVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void log(String msg){
        Log.i(MyApp.TAG,this.getClass().getSimpleName()+"="+msg);
    }
    @Override
    public int getLayoutId() {
        return R.layout.view_video_empty_control;
    }

    @Override
    protected void touchSurfaceMoveFullLogic(float absDeltaX, float absDeltaY) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY);
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = true;

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false;

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false;

        mLooping=true;
    }

    @Override
    protected void showProgressDialog(float deltaX, String seekTime, int seekTimePosition, String totalTime, int totalTimeDuration) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);

    }

    @Override
    protected void setProgressAndTime(int progress, int secProgress, int currentTime, int totalTime) {
        super.setProgressAndTime(progress, secProgress, currentTime, totalTime);
//        LogHelp.log("==setProgressAndTime=progress:"+progress+""+"secProgress:"+secProgress+"currentTime:"+currentTime+"totalTime:"+totalTime);
        log("==setProgressAndTime=progress:"+progress+""+"currentTime:"+(currentTime/1000)+"totalTime:"+totalTime+"==");
        if(callbackPlayTimer!=null){
            callbackPlayTimer.onPlayTimer(progress,currentTime,totalTime);
        }
    }
    @Override
    protected void touchDoubleUp() {
        //super.touchDoubleUp();
        //不需要双击暂停
    }
    CallbackPlayTimer callbackPlayTimer;
    public interface CallbackPlayTimer{
        void onPlayTimer(int progress, int currentTime, int totalTime);
    }

    public CallbackPlayTimer getCallbackPlayTimer() {
        return callbackPlayTimer;
    }

    public void setCallbackPlayTimer(CallbackPlayTimer callbackPlayTimer) {
        this.callbackPlayTimer = callbackPlayTimer;
    }
}