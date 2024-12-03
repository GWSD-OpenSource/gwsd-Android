package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gwsd.open_ptt.R;

public class MeetingTopView extends RelativeLayout {

    View viewTopRoot;
    //FrameLayout viewTopLeft;
    ImageView viewTopLeftImg1;
    ImageView viewTopLeftImg2;
    TextView viewTopTitle;

    FrameLayout viewTopRight;
    ImageView viewTopRightImg;
    TextView viewTopRightTx;

    public MeetingTopView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public MeetingTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public MeetingTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_meeting_top,this,true);
//        addView(view);
        viewTopRoot=findViewById(R.id.viewTopRoot);
        //viewTopLeft = findViewById(R.id.viewTopLeft);
        viewTopLeftImg1 = findViewById(R.id.viewTopLeftImg1);
        viewTopLeftImg2 = findViewById(R.id.viewTopLeftImg2);
        viewTopTitle = findViewById(R.id.viewTopTitle);
        viewTopRight = findViewById(R.id.viewTopRight);
        viewTopRightImg= findViewById(R.id.viewTopRightImg);
        viewTopRightTx= findViewById(R.id.viewTopRightTx);

        //viewTopLeft.setVisibility(GONE);
        viewTopLeftImg1.setVisibility(GONE);
        viewTopLeftImg2.setVisibility(GONE);
        viewTopRight.setVisibility(GONE);
    }

    public TextView getTitleView(){
        return viewTopTitle;
    }

    public void setViewBg(int resId){
        if(viewTopRoot!=null){
            viewTopRoot.setBackgroundResource(resId);
        }
    }
    public void setTopTitle(int res){
        viewTopTitle.setText(res);
    }
    public void setTopTitle(String title){
        viewTopTitle.setText(title);
    }
    public void setLeftImg1(int resId){
        viewTopLeftImg1.setVisibility(VISIBLE);
        viewTopLeftImg1.setImageResource(resId);
    }
    public void setLeftImg2(int resId){
        viewTopLeftImg2.setVisibility(VISIBLE);
        viewTopLeftImg2.setImageResource(resId);
    }
    public void setLeft1Select(boolean select) {
        viewTopLeftImg1.setSelected(select);
    }
    public void setLeft2Select(boolean select) {
        viewTopLeftImg2.setSelected(select);
    }
    public void setLeftImg1Click(OnClickListener onClickListener){
        viewTopLeftImg1.setVisibility(VISIBLE);
        viewTopLeftImg1.setOnClickListener(onClickListener);
    }
    public void setLeftImg2Click(OnClickListener onClickListener){
        viewTopLeftImg2.setVisibility(VISIBLE);
        viewTopLeftImg2.setOnClickListener(onClickListener);
    }
    public void left1Click(){
        if (viewTopLeftImg1.getVisibility() == VISIBLE) {
            viewTopLeftImg1.performClick();
        }
    }
    public void setRightClick(OnClickListener onClickListener){
        if (onClickListener == null){
            viewTopRight.setVisibility(GONE);
            viewTopRight.setOnClickListener(onClickListener);
        }else {
            viewTopRight.setVisibility(VISIBLE);
            viewTopRight.setOnClickListener(onClickListener);
        }
    }
    public void setTopRightTx(int res){
        viewTopRight.setVisibility(VISIBLE);
        viewTopRightTx.setVisibility(VISIBLE);
        viewTopRightImg.setVisibility(GONE);
        viewTopRightTx.setText(res);
    }
    public void setTopRightTx(String title){
        viewTopRight.setVisibility(VISIBLE);
        viewTopRightTx.setVisibility(VISIBLE);
        viewTopRightImg.setVisibility(GONE);
        viewTopRightTx.setText(title);
    }
    public void setTopRightTxColor(int color){
        viewTopRightTx.setTextColor(color);
    }
    public void setTopRightEnabled(boolean enabled){
        viewTopRight.setEnabled(enabled);
    }
    public void setTopRightImg(int resId){
        viewTopRight.setVisibility(VISIBLE);
        viewTopRightTx.setVisibility(GONE);
        viewTopRightImg.setVisibility(VISIBLE);
        viewTopRightImg.setImageResource(resId);
    }

}
