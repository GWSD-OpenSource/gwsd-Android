package com.gwsd.open_ptt.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.gwsd.open_ptt.MyApp;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback ,Camera.ErrorCallback{
    Context mContext;
    SurfaceHolder mSurfaceHolder;
    Camera mCamera;
    boolean mIsStartPreview = false;//是否开始了录制

    private void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public CameraPreview(Context context, Camera camera) {
        super(context);
        init(context, camera);
    }
    public void  init(Context context, Camera camera){
        initView(context, null, 0);
        this.mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        this.mCamera=camera;
    }
    public void setmCamera(Camera mCamera){
        this.mCamera=mCamera;
        startPreview();
    }
    private void initView(Context context, AttributeSet attrs, int defStyleAttr){
        this.mContext=context;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!mIsStartPreview){
            startPreview();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mSurfaceHolder = holder;
        if(!mIsStartPreview){
            startPreview();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsStartPreview=false;
    }
    private void startPreview(){
        if(mSurfaceHolder==null || mSurfaceHolder.getSurface()==null || mCamera==null){
            return;
        }else {
            mIsStartPreview=true;
        }
        try {
            mCamera.stopPreview();
        }catch (Exception e){
          e.printStackTrace();
        }
        mCamera.setErrorCallback(this);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            setCameraParameters();
            try{
                mCamera.cancelAutoFocus();////此句加上 可自动聚焦 必须加
            }catch (Exception e){
                e.printStackTrace();
            }
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setCameraParameters(){
//        Camera.Parameters parameters = mCamera.getParameters();
//        try{
//            parameters.set("orientation", "portrait");
//            if(parameters.isSmoothZoomSupported()){
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            }
//            mCamera.setParameters(parameters);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
    @Override
    public void onError(int error, Camera camera) {
        log("==onError==camera:"+camera);
        if(error==Camera.CAMERA_ERROR_SERVER_DIED){
        }else if(error==Camera.CAMERA_ERROR_UNKNOWN){

        }
    }

    /**
     * 视频流监听，视频直播
     * @param callback
     */
    public void addPreviewDataCall(Camera.PreviewCallback callback){
        if(callback!=null)mCamera.setPreviewCallback(callback);
    }
    public void removePreviewDataCall(Camera.PreviewCallback callback){
        if(callback!=null)mCamera.setPreviewCallback(null);
    }
}