package com.gwsd.open_ptt.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gwsd.open_ptt.R;


public class SpeakerVoiceDBAnimView extends androidx.appcompat.widget.AppCompatTextView {

    public SpeakerVoiceDBAnimView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public SpeakerVoiceDBAnimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public SpeakerVoiceDBAnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    View viewSpeakerAnim;
    AnimationDrawable speakerAnim;

    boolean curAnimRuning=false;

    private void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        viewSpeakerAnim=this;
        speakerAnim= (AnimationDrawable) getContext().getResources().getDrawable(R.drawable.selector_speaker_voice_db_anim);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }
    public void release(){
        curAnimRuning=true;
        if(speakerAnim!=null){
            speakerAnim.stop();
            speakerAnim=null;
        }
        if(viewSpeakerAnim!=null){
            viewSpeakerAnim.setBackground(null);
            viewSpeakerAnim=null;
        }
    }
    public void startSpeakerAnim(){
        if(curAnimRuning){
            return;
        }
        curAnimRuning=true;
        if(viewSpeakerAnim!=null && speakerAnim!=null){
            viewSpeakerAnim.setBackground(speakerAnim);
            speakerAnim.start();
        }
    }
    public void reset(){
        stopSpeakerAnim();
    }
    public void stopSpeakerAnim(){
        if(!curAnimRuning){
            return;
        }
        curAnimRuning=false;
        if(speakerAnim!=null){
            speakerAnim.stop();
        }
        if(viewSpeakerAnim!=null){
            viewSpeakerAnim.setBackgroundResource(R.drawable.ic_animation_speak_01);
        }
    }
}
