package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;

public class ChatFromTxtView extends ChatBaseFromMsgView {

    public ChatFromTxtView(Context context) {
        super(context);
    }

    public ChatFromTxtView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatFromTxtView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    TextView viewText;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        viewText=findViewById(R.id.viewText);
    }

    @Override
    public void displayMessage(int position, MsgContentPojo dbBean, String userId) {
        super.displayMessage(position,dbBean, userId);
        viewText.setText(dbBean.getContent());
    }
}
