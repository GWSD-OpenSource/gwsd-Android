package com.gwsd.open_ptt.comm_ui.video_record;

import android.text.TextUtils;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.fragment.BaseFragment;
import com.gwsd.open_ptt.comm_ui.video_record.contracts.VideoPlayContracts;
import com.gwsd.open_ptt.comm_ui.video_record.help.EmptyControlVideo;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

public class VideoPlayFragment extends BaseFragment implements VideoPlayContracts.VideoPlayUI {


    EmptyControlVideo videoPlayer;

    EmptyControlVideo.CallbackPlayTimer callbackPlayTimer;
    String pathUrl;


    public static VideoPlayFragment build(){
        return new VideoPlayFragment();
    }


    @Override
    protected int getViewId() {
        return R.layout.fragment_video_play;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        videoPlayer = (EmptyControlVideo) contentView.findViewById(R.id.video_player);
    }

    @Override
    protected void initEvent() {
        videoPlayer.setLooping(true);
        if(videoPlayer!=null && !TextUtils.isEmpty(pathUrl)){
            videoPlayer.setUp(pathUrl, false, "");
            videoPlayer.startPlayLogic();
        }
        videoPlayer.setCallbackPlayTimer(callbackPlayTimer);
    }


    @Override
    public void release() {
        uiStopPlay();
        if(videoPlayer!=null){
            videoPlayer.setCallbackPlayTimer(null);
            videoPlayer.setStandardVideoAllCallBack(null);
            GSYVideoPlayer.releaseAllVideos();
        }
        callbackPlayTimer=null;
    }

    @Override
    public void uiPlay(String path) {
        log("path:"+path);
        this.pathUrl=path;
        if(videoPlayer!=null){
            videoPlayer.setUp(path, false, "");
            videoPlayer.startPlayLogic();
        }
    }

    @Override
    public void uiStopPlay() {
        if(videoPlayer!=null){
            videoPlayer.release();
        }
    }

    @Override
    public void uiAddCallbackPlayTimer(EmptyControlVideo.CallbackPlayTimer callbackPlayTimer) {
        this.callbackPlayTimer=callbackPlayTimer;
        if(videoPlayer!=null){
            videoPlayer.setCallbackPlayTimer(callbackPlayTimer);
        }
    }
}
