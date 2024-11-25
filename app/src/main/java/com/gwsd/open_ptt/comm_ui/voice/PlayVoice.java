package com.gwsd.open_ptt.comm_ui.voice;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import com.gwsd.open_ptt.R;

public class PlayVoice {
    static PlayVoice playVoice=new PlayVoice();
    public static PlayVoice getInstance(){
        return playVoice;
    }
    ImageView lastPlayImageView;
    boolean isLeft=false;
    int lastPosition=-1;
    PlayTaskV2 playTask;

    public void play(String url, ImageView imageView, int position){
        releasePlayTask();
        if(position==lastPosition){
            releaseAnimByView();
            return;
        }
        playTask=new PlayTaskV2();
        playTask.setOnPlayStateCallback((playState, music) -> {
            if(playState==PlayState.STATE_IDLE){
                releaseAnimByView();
            }
        });
        playAnimByView(imageView);
        lastPosition=position;
        Music music=new Music();
        music.setType(Music.MusicType_ONLINE);
        music.setPath(url);
        playTask.play(music);
    }
    private void releasePlayTask(){
        if(playTask!=null){
            playTask.setOnPlayStateCallback(null);
            playTask.stop();
            playTask=null;
        }
    }
    public void release(){
        releaseAnimByView();
        releasePlayTask();
    }

    /**
     * @param imageView
     */
    private void playAnimByView(ImageView imageView){
        if(imageView==null)return;
        this.isLeft=isLeft;
        releaseAnimByView();
        imageView.setImageResource(R.drawable.animation_chat_play_voice);
        AnimationDrawable animationDrawable= (AnimationDrawable) imageView.getDrawable();
        if(animationDrawable!=null)animationDrawable.start();
        this.lastPlayImageView=imageView;
    }
    private void releaseAnimByView(){
        if(lastPlayImageView==null)return;
//        if(isLeft){
//            lastPlayImageView.setImageResource(R.mipmap.voice_other_3);
//        }else {
//            lastPlayImageView.setImageResource(R.mipmap.voice_self_3);
//        }
        lastPlayImageView.setImageResource(R.drawable.yida_ic_msg_viice_me);
        lastPlayImageView=null;
        lastPosition=-1;
    }
}
