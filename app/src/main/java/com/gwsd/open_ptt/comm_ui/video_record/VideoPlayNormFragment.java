package com.gwsd.open_ptt.comm_ui.video_record;

import android.os.Bundle;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.fragment.BaseFragment;
import com.gwsd.open_ptt.comm_ui.video_record.help.VideoPlayerView;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;


public class VideoPlayNormFragment extends BaseFragment {

    VideoPlayerView videoPlayer;

    String videoUrl;
    String thumbUrl;

    public static VideoPlayNormFragment build(String videoUrl,String thumbUrl){
        VideoPlayNormFragment fragment=new VideoPlayNormFragment();
        Bundle bundle=new Bundle();
        bundle.putString("videoUrl",videoUrl);
        bundle.putString("thumbUrl",thumbUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_video_play_normal;
    }

    @Override
    protected void initData() {
        Bundle bundle=getArguments();
        videoUrl=bundle.getString("videoUrl");
        thumbUrl=bundle.getString("thumbUrl");
    }

    @Override
    protected void initView() {
        videoPlayer = contentView.findViewById(R.id.video_player);
    }

    @Override
    protected void initEvent() {
        videoPlayer.setLooping(true);
        videoPlayer.setUp(videoUrl, true, "");

        videoPlayer.setOnViewClick(v -> {
            releasePlay();
            finishAct();
        });

        videoPlayer.startPlayLogic();
    }


    @Override
    public void release() {
        releasePlay();
    }

    public void releasePlay(){
        try{
            if(videoPlayer!=null){
                videoPlayer.release();
                videoPlayer.setStandardVideoAllCallBack(null);
                GSYVideoPlayer.releaseAllVideos();
                videoPlayer=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
