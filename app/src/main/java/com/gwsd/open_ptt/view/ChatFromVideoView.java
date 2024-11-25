package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;
import com.gwsd.open_ptt.comm_ui.video_record.PlayVideoActivity;

public class ChatFromVideoView extends ChatBaseFromMsgView {
    public ChatFromVideoView(Context context) {
        super(context);
    }

    public ChatFromVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatFromVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    ImageView viewVideoThumb;
    ImageView viewVideoFlag;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewVideoThumb=findViewById(R.id.viewVideoThumb);
        viewVideoFlag=findViewById(R.id.viewVideoFlag);
    }

    @Override
    public void displayMessage(int position, MsgContentPojo dbBean, String userId) {
        super.displayMessage(position,dbBean, userId);
        String imgUrl=dbBean.getThumburl();
        loadImg(getContext(),imgUrl,viewVideoThumb);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        String videoUrl=bean.getUrl();
        String thumbUrl=bean.getThumburl();
        PlayVideoActivity.navToAct(view.getContext(),videoUrl,thumbUrl);
    }
}
