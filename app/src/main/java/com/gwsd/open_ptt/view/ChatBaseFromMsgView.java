package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gwsd.open_ptt.GlideApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;

public class ChatBaseFromMsgView extends RelativeLayout implements View.OnClickListener {

    protected MsgContentPojo bean;
    protected int position;
    public ChatBaseFromMsgView(Context context) {
        super(context);
    }

    public ChatBaseFromMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBaseFromMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void displayMessage(int position,MsgContentPojo dbBean,String userId){
        this.bean=dbBean;
        this.position=position;
    }
    public void displayMessageBeforeParam(Object object){

    }
    protected void loadImg(Context context,String imgUrl,ImageView viewImg){
        float dimen=context.getResources().getDimension(R.dimen.dimen80dp);
        int size= (int) dimen;
        GlideApp.with(context).load(imgUrl)
                .placeholder(R.mipmap.photo_ic_photo_loading)
                .error(R.mipmap.photo_ic_photo_loading)
                .override(size,size)
                .into(viewImg);
    }

    @Override
    public void onClick(View view) {

    }
}
