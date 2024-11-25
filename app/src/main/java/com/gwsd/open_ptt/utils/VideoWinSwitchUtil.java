package com.gwsd.open_ptt.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.gwsd.rtc.view.GWRtcSurfaceVideoRender;


public class VideoWinSwitchUtil implements View.OnTouchListener {
    final int VIEW_ID_None=-1;
    final int VIEW_ID_Remote=1;
    final int VIEW_ID_Local=2;

    private  static class VideoViewParam{
        int smallViewWidth;
        int smallViewHeight;
        int srceenW;
        int srceenH;
        int offsetLeft=0;
        int offsetBottom=0;
    }
    private VideoViewParam param;
    private RelativeLayout viewSurfaceGroup;
    private GWRtcSurfaceVideoRender remoteView;
    private GWRtcSurfaceVideoRender localView;

    private int curSmallId=VIEW_ID_None;

    int[] locationScreen=new int[2];

    private void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public VideoWinSwitchUtil(Context context){
        param=new VideoViewParam();
        calculationSmallSize(context);
    }

    public void release(){
        this.viewSurfaceGroup=null;
        this.localView=null;
        this.remoteView=null;
        curSmallId=VIEW_ID_None;
    }
    public void calculationSmallSize(Context context){
        Resources resources=context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        param.srceenW =dm.widthPixels>dm.heightPixels?dm.heightPixels:dm.widthPixels;
        param.srceenH=dm.widthPixels>dm.heightPixels?dm.widthPixels:dm.heightPixels;
        param.smallViewWidth = param.srceenW  /3;
        param.smallViewHeight = param.smallViewWidth +50;
        param.offsetBottom = (int) context.getResources().getDimension(R.dimen.dimen5dp);

//        log("==calculationSmallSize="+curSmallId+",srceen w:"+param.srceenW+",h:"+param.srceenH+","+param.smallViewWidth);
//        if(curSmallId==VIEW_ID_Remote){
//            changeVideoView(viewSurfaceGroup,remoteView,localView);
//        }else if(curSmallId==VIEW_ID_Local){
//            changeVideoView(viewSurfaceGroup,localView,remoteView);
//        }
    }
    public void setVideoView(RelativeLayout viewSurfaceGroup,GWRtcSurfaceVideoRender remoteView, GWRtcSurfaceVideoRender localView){
        this.viewSurfaceGroup=viewSurfaceGroup;
        this.remoteView=remoteView;
        this.localView=localView;
    }
    public void addClickListener(){

    }
    public void changeLocalSmall(){
        changeSmallAuto();
    }
    public void changeRemoteSmall(){
        changeSmallAuto();
    }

    public void changeSmallAuto(){
        if(viewSurfaceGroup==null)return;
        if(curSmallId!=VIEW_ID_Local){
            changeVideoView(viewSurfaceGroup, localView, remoteView);
            curSmallId=VIEW_ID_Local;
            localView.setOnTouchListener(this);
            remoteView.setOnTouchListener(null);
        }else if(curSmallId!=VIEW_ID_Remote) {
            changeVideoView(viewSurfaceGroup, remoteView, localView);
            curSmallId=VIEW_ID_Remote;
            remoteView.setOnTouchListener(this);
            localView.setOnTouchListener(null);
        }
    }
    private void changeVideoView(RelativeLayout viewSurfaceGroup,GWRtcSurfaceVideoRender smallVideoV, GWRtcSurfaceVideoRender bigVideoV) {
        if(smallVideoV!=null) viewSurfaceGroup.removeView(smallVideoV);
        if(bigVideoV!=null)viewSurfaceGroup.removeView(bigVideoV);

        if(bigVideoV!=null){
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) bigVideoV.getLayoutParams();
            linearParams.width =RelativeLayout.LayoutParams.MATCH_PARENT;
            linearParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            linearParams.leftMargin=0;
            linearParams.topMargin=0;
            linearParams.bottomMargin=0;
            linearParams.rightMargin=0;
            bigVideoV.setLayoutParams(linearParams);
        }
        if(smallVideoV!=null){
            smallVideoV.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) smallVideoV.getLayoutParams();
            linearParams.width = param.smallViewWidth;
            linearParams.height = param.smallViewHeight;
            linearParams.leftMargin=0;
            linearParams.bottomMargin=param.offsetBottom;
            linearParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            linearParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            smallVideoV.setZOrderOnTop(true);
            smallVideoV.setZOrderMediaOverlay(true);
            smallVideoV.setLayoutParams(linearParams);
            log("linearParams.leftMargin=:"+linearParams.leftMargin);
        }
        if(bigVideoV!=null)viewSurfaceGroup.addView(bigVideoV);
        if(smallVideoV!=null)viewSurfaceGroup.addView(smallVideoV);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return dispatchTouchEvent(event);
    }

    int oldX;
    int oldY;
    long oldTime;
    boolean isDownTouchSmall=false;
    public boolean dispatchTouchEvent(MotionEvent event){
        if(viewSurfaceGroup==null)return false;

        if(event.getAction()==MotionEvent.ACTION_DOWN){
            oldX= (int) event.getX();
            oldY= (int) event.getY();
            oldTime=System.currentTimeMillis();
            isDownTouchSmall=isTouchSmalVideo(event);
        }else if(event.getAction()==MotionEvent.ACTION_MOVE){
            if(Math.abs(event.getX()-oldX)>10 || Math.abs(event.getY()-oldY)>10){
                if(isDownTouchSmall){
                    touchMoveSmallView(event);
                }
            }
        }else if(event.getAction()==MotionEvent.ACTION_UP
                || event.getAction()==MotionEvent.ACTION_CANCEL){
            long newTime=System.currentTimeMillis();
            boolean tempIsTouchSmall=isDownTouchSmall;
            isDownTouchSmall=false;
            if(Math.abs(event.getX()-oldX)<10 && Math.abs(event.getY()-oldY)<10 && newTime-oldTime<500){
                if(tempIsTouchSmall){
                    changeSmallAuto();
                }
            }
        }
        return true;
    }
    /**
     * 判断当前触摸的位置是不是小窗口上,
     */
    private boolean isTouchSmalVideo(MotionEvent event){
        if(localView==null || remoteView==null){
            return false;
        }
        if(curSmallId==VIEW_ID_Local){
            localView.getLocationOnScreen(locationScreen);
        }else if(curSmallId==VIEW_ID_Remote){
            remoteView.getLocationOnScreen(locationScreen);
        }else {
            return false;
        }
        int x=  (int) event.getRawX();
        int y= (int) event.getRawY();
        int vx=locationScreen[0];
        int vy=locationScreen[1];
        boolean is1=x>vx;
        boolean is2=x<=vx+param.smallViewWidth;
        boolean is3=y> vy;
        boolean is4=y<=vy+param.smallViewHeight;
        if(is1 && is2 && is3 && is4){
            return true;
        }
        return false;
    }

    private boolean touchMoveSmallView(MotionEvent ev) {
        if(viewSurfaceGroup==null){
            return false;
        }
        if(ev.getAction()==MotionEvent.ACTION_MOVE){
            int x= (int) ev.getRawX();
            int y= (int) ev.getRawY();
            int left=x-param.smallViewWidth/2;
            int bottom=param.srceenH-(y+param.smallViewHeight/2);

            if(left<0){
                left=0;
            }else if(left>param.srceenW-param.smallViewWidth){
                left=param.srceenW-param.smallViewWidth;
            }
            //上下边距
            if(bottom<0){
                bottom=0;
            }else if(bottom>param.srceenH-param.smallViewHeight){
                bottom=param.srceenH-param.smallViewHeight;
            }
            //log("finalX:"+left+",finalY:"+bottom);
            if(curSmallId== VIEW_ID_Local){
                RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) localView.getLayoutParams();
                layoutParams.leftMargin=left;
                layoutParams.bottomMargin=bottom;
                localView.setZOrderOnTop(true);
                localView.setZOrderMediaOverlay(true);
                localView.setLayoutParams(layoutParams);
            }else if(curSmallId== VIEW_ID_Remote){
                RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) remoteView.getLayoutParams();
                layoutParams.leftMargin=left;
                layoutParams.bottomMargin=bottom;
                remoteView.setZOrderOnTop(true);
                remoteView.setZOrderMediaOverlay(true);
                remoteView.setLayoutParams(layoutParams);
            }
        }
        return true;
    }
}
