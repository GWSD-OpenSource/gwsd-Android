package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.comm_ui.voice.PlayVoice;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;

public class ChatFromVoiceView extends ChatBaseFromMsgView {

    Context context;

    public ChatFromVoiceView(Context context) {
        super(context);
        this.context = context;
    }

    public ChatFromVoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ChatFromVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }
    ImageView viewVoiceFlag;
    TextView viewVoiceTime;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewVoiceFlag=findViewById(R.id.viewVoiceFlag);
        viewVoiceTime=findViewById(R.id.viewVoiceTime);
    }

    @Override
    public void displayMessage(int position, MsgContentPojo dbBean, String userId) {
        super.displayMessage(position,dbBean, userId);
        viewVoiceFlag.setImageResource(R.mipmap.ic_msg_voice_me);
        //String playTimeStr=String.valueOf(dbBean.getPlaytime())+"'";
        //viewVoiceTime.setText(playTimeStr);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        PlayVoice.getInstance().play(bean.getUrl(), viewVoiceFlag, position);
    }

}
