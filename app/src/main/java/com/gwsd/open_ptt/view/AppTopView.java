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

public class AppTopView extends RelativeLayout {
    View viewTopRoot;
    FrameLayout viewTopLeft;
    ImageView viewTopLeftImg;
    TextView viewTopTitle;

    FrameLayout viewTopRight;
    ImageView viewTopRightImg;
    TextView viewTopRightTx;

    public AppTopView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public AppTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public AppTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_top_layout,this,true);
//        addView(view);
        viewTopRoot=findViewById(R.id.viewTopRoot);
        viewTopLeft = findViewById(R.id.viewTopLeft);
        viewTopLeftImg = findViewById(R.id.viewTopLeftImg);
        viewTopTitle = findViewById(R.id.viewTopTitle);
        viewTopRight = findViewById(R.id.viewTopRight);
        viewTopRightImg= findViewById(R.id.viewTopRightImg);
        viewTopRightTx= findViewById(R.id.viewTopRightTx);

        viewTopLeft.setVisibility(GONE);
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
    public void setLeftImg(int resId){
        viewTopLeft.setVisibility(VISIBLE);
        viewTopLeftImg.setImageResource(resId);
    }
    public void setLeftSelect(boolean select) {
        viewTopLeft.setSelected(select);
    }
    public void setLeftClick(OnClickListener onClickListener){
        viewTopLeft.setVisibility(VISIBLE);
        viewTopLeft.setOnClickListener(onClickListener);
    }
    public void leftClick() {
        if (viewTopLeft.getVisibility() == VISIBLE) {
            viewTopLeft.performClick();
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
    public ImageView getViewTopRightImg(){
        return viewTopRightImg;
    }
    public void setTopRightImgVisible() {
        viewTopRight.setVisibility(VISIBLE);
        viewTopRightTx.setVisibility(GONE);
        viewTopRightImg.setVisibility(VISIBLE);
    }
    public void setTopRightImgGone() {
        viewTopRight.setVisibility(GONE);
        viewTopRightTx.setVisibility(GONE);
        viewTopRightImg.setVisibility(GONE);
    }
}
