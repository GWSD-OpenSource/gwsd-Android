package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;
import com.gwsd.open_ptt.utils.Utils;

public class ChatFromAVCallView extends ChatBaseFromMsgView {

    public ChatFromAVCallView(Context context) {
        super(context);
    }

    public ChatFromAVCallView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatFromAVCallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    TextView viewText;
    ImageView viewImage;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewText=findViewById(R.id.viewText);
        viewImage=findViewById(R.id.viewCallType);
    }

    @Override
    public void displayMessage(int position, MsgContentPojo dbBean, String userId) {
        super.displayMessage(position, dbBean, userId);
        String content = dbBean.getContent();
        int ts = 0;
        int resId = dbBean.getMsgType() == 0?R.mipmap.ic_msg_voice_call_day:R.mipmap.ic_msg_video_call_day;
        try {
            ts = Integer.valueOf(content);
        } catch (Exception e) {
            ts = 0;
        }
        if (ts < 0) {
            ts = 0;
        }
        viewImage.setImageResource(resId);
        if (ts == 0) {
            viewText.setText(R.string.not_connected);
        } else {
            String time = Utils.intToTimer(ts);
            viewText.setText(String.format(getResources().getString(R.string.talk_time),time));
        }
    }
}
