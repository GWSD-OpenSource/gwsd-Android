package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.gwsd.open_ptt.MyApp;

public abstract class ChatVideoBaseView extends RelativeLayout implements View.OnClickListener, View.OnTouchListener,ChatVideoViewContracts.PVideoVUpdate {


    ChatVideoViewContracts.OnVideoBtnCallback videoBtnClick;
    long lastDownTime=0;// Quick click timer;

    boolean hasTouchEnabled = true;
    long lastTouchTimer = -1;


    public ChatVideoBaseView(Context context) {
        super(context);
        initView(context,null,0);
    }

    public ChatVideoBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs,0);
    }
    protected void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public ChatVideoBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    protected abstract int getContentView(Context context, AttributeSet attrs, int defStyleAttr);
    protected abstract void onViewInitSuc(Context context, AttributeSet attrs, int defStyleAttr);
    private void initView(Context context, AttributeSet attrs, int defStyleAttr){
        LayoutInflater.from(context).inflate(getContentView(context, attrs, defStyleAttr),this,true);;
        onViewInitSuc(context, attrs, defStyleAttr);
    }

    public ChatVideoViewContracts.OnVideoBtnCallback getVideoBtnCallback() {
        return videoBtnClick;
    }
    @Override
    public void setOnVideoBtnClick(ChatVideoViewContracts.OnVideoBtnCallback videoVBtnClick) {
        this.videoBtnClick=videoVBtnClick;
    }
    protected boolean hasQuickClick(){
        if(System.currentTimeMillis()-lastDownTime<1000){
            log("Quick click");
            return true;
        }
        lastDownTime=System.currentTimeMillis();
        return false;
    }
    int touchCount=0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isHasTouchEnabled()) return true;
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_DOWN:
                touchCount=0;
                resetLastOptTimer();
                break;
            case MotionEvent.ACTION_MOVE:
                touchCount++;
                if(touchCount>80){
                    touchCount=0;
                    resetLastOptTimer();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public boolean hasLongTimerNoOpt(){
        return System.currentTimeMillis()-getLastOptTimer()>3000;
    }
    protected long getLastOptTimer() {
        return lastTouchTimer;
    }
    public void resetLastOptTimer() {
        this.lastTouchTimer = System.currentTimeMillis();
    }
    protected void setBtnAllEnabled(boolean enabled) {
        this.hasTouchEnabled = enabled;
    }
    protected boolean isHasTouchEnabled() {
        return hasTouchEnabled;
    }

    protected String getString(int resID){
        return getResources().getString(resID);
    }
    protected String stringFormat(int resId,Object... args ){
        return stringFormat(getString(resId),args);
    }
    protected String stringFormat(String format,Object... args ){
        return  String.format(format, args);
    }
}
