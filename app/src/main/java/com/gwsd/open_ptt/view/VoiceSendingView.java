package com.gwsd.open_ptt.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gwsd.open_ptt.R;

public class VoiceSendingView extends RelativeLayout {
    private AnimationDrawable frameAnimation;
    TextView view_textmsg;
    ImageView imageView;
    public VoiceSendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_voice_sending, this);
        imageView = (ImageView)findViewById(R.id.microphone);
         view_textmsg = (TextView) findViewById(R.id.view_textmsg);
//        img.setBackgroundResource(R.drawable.animation_voice);
//        frameAnimation = (AnimationDrawable) img.getBackground();
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.VoiceSendingView);
        int res=typedArray.getResourceId(R.styleable.VoiceSendingView_thc_AnimationDrawable, R.drawable.animation_voice);
        float height=typedArray.getDimension(R.styleable.VoiceSendingView_thc_microphone_height,context.getResources().getDimension(R.dimen.group_list_ptt_VoiceSendingView_size));
        float width=typedArray.getDimension(R.styleable.VoiceSendingView_thc_microphone_width,context.getResources().getDimension(R.dimen.group_list_ptt_VoiceSendingView_size));
        typedArray.recycle();

        imageView.setLayoutParams(new LinearLayout.LayoutParams((int) width,(int) height));
        imageView.setImageResource(res);
        frameAnimation= (AnimationDrawable) imageView.getDrawable();
    }

    public void setTextStr(String textStr){
        if(view_textmsg!=null){
            view_textmsg.setText(textStr);
        }
    }
    public void setTextStr(int resStrId){
        if(view_textmsg!=null){
            view_textmsg.setText(resStrId);
        }
    }
    public void setTextColor(int color){
        if(view_textmsg!=null){
            view_textmsg.setTextColor(color);
        }
    }

    public void setFrameAnimation(int resId){
        if(imageView!= null){
            imageView.setImageResource(resId);
            frameAnimation= (AnimationDrawable) imageView.getDrawable();
        }
    }

    public void showRecording(){
        if(frameAnimation!=null)frameAnimation.start();
    }
    public void showCancel(){
        if(frameAnimation!=null)frameAnimation.stop();
    }
    public void release(){
        if(frameAnimation!=null)frameAnimation.stop();
        frameAnimation=null;
    }
    public void cancalText(){
        view_textmsg.setVisibility(GONE);
        setTextStr(R.string.hint_press_record);
        int txColor = getResources().getColor(R.color.textColorBlack);
        view_textmsg.setTextColor(txColor);
    }
}