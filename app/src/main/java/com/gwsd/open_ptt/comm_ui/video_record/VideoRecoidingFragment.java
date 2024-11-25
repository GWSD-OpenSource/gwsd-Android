package com.gwsd.open_ptt.comm_ui.video_record;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.widget.FrameLayout;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.comm_ui.video_record.contracts.VideoRecordingContracts;
import com.gwsd.open_ptt.fragment.BaseFragment;
import com.gwsd.open_ptt.utils.SDCardUtil;
import com.gwsd.open_ptt.utils.Utils;
import com.gwsd.open_ptt.comm_ui.video_record.help.VideoRecoidingResultBean;
import com.gwsd.open_ptt.comm_ui.video_record.presenter.VideoRecordingPresenterImp;
import com.gwsd.open_ptt.view.CameraPreview;

import java.io.File;
import java.util.Calendar;


/**
 * Created by Nicky on 2017/12/15.
 */

public class VideoRecoidingFragment extends BaseFragment implements VideoRecordingContracts.VideoRecordingUI{


    FrameLayout view_fragmentlayout;

    Camera mCamera;
    CameraPreview cameraPreview;
    MediaRecorder mMediaRecorder;

    String outputFilePath;
    int videoQuality=-1;
    long timeStart;
    boolean isRecording=false;
    int currentCameraFacing=Camera.CameraInfo.CAMERA_FACING_BACK;

    VideoRecordingContracts.OnRecordingLisenter onRecordingLisenter;

    VideoRecordingContracts.VideoRecordingPresenter presenter;


    public static VideoRecoidingFragment build(){
        return new VideoRecoidingFragment();
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_video_record;
    }

    @Override
    protected void initData() {

    }

    @Override
    public void release() {
        if(mMediaRecorder!=null){
            mMediaRecorder.stop();
            releaseMediaRecorder();
        }
        releaseCamera();
        setRecording(false);
        onRecordingLisenter=null;
        mCamera=null;
        cameraPreview=null;
        mMediaRecorder=null;
    }

    protected void initView() {
        presenter=new VideoRecordingPresenterImp(this);
        mCamera=cameraGetInstance(currentCameraFacing);

        if(mCamera==null){
            showToast(R.string.hint_open_camera_failure);
            getActivity().finish();
            return;
        }
        cameraPreview = new CameraPreview(getContext(), mCamera);
        view_fragmentlayout = contentView.findViewById(R.id.view_fragmentlayout);
        view_fragmentlayout.addView(cameraPreview);
        outputFilePath=refreshOutputFile().getAbsolutePath();
        setRecording(false);

    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void uiToast(int resId) {
        showToast(resId);
    }

    @Override
    public void uiStartRecording() {
        if (prepareVideoRecorder()) {
            timeStart= Calendar.getInstance().getTimeInMillis();
            mMediaRecorder.start();

            setRecording(true);
            if(onRecordingLisenter!=null){
                VideoRecoidingResultBean videoRecoidingResultBean=new VideoRecoidingResultBean();
                videoRecoidingResultBean.code=201;
                videoRecoidingResultBean.message="start record";
                onRecordingLisenter.onResult(videoRecoidingResultBean);
            }
        } else {
            setRecording(false);
           if(onRecordingLisenter!=null){
               VideoRecoidingResultBean videoRecoidingResultBean=new VideoRecoidingResultBean();
               videoRecoidingResultBean.code=-1;
               videoRecoidingResultBean.message="VideoRecorder init err";
               onRecordingLisenter.onResult(videoRecoidingResultBean);
           }
        }
    }

    @Override
    public void uiStopRecording() {
        if(mMediaRecorder==null || mCamera==null){
            return;
        }

        if (isLongEnough() ){
            mMediaRecorder.stop();
        }
        releaseMediaRecorder();
        mCamera.lock();
        setRecording(false);
        if (isLongEnough()){
            Utils.mediaScannerScanFile(getContext(),new File(outputFilePath));
            if(onRecordingLisenter!=null){
                VideoRecoidingResultBean videoRecoidingResultBean=new VideoRecoidingResultBean();
                videoRecoidingResultBean.code=200;
                videoRecoidingResultBean.message="OK";
                videoRecoidingResultBean.filePath=outputFilePath;
                videoRecoidingResultBean.timerCount=Calendar.getInstance().getTimeInMillis() - timeStart;
                onRecordingLisenter.onResult(videoRecoidingResultBean);
            }
        }else{
            uiToast(R.string.hint_camera_time_small);
            String msg=getResources().getString(R.string.hint_camera_time_small);
            if(onRecordingLisenter!=null){
                VideoRecoidingResultBean videoRecoidingResultBean=new VideoRecoidingResultBean();
                videoRecoidingResultBean.code=-1;
                videoRecoidingResultBean.message=msg;
                onRecordingLisenter.onResult(videoRecoidingResultBean);
            }
            deleteFileByPath(outputFilePath);
        }
    }

    @Override
    public boolean uiIsRecording() {
        return isRecording();
    }

    @Override
    public boolean uiCameraSwitchChange() {
        if(!Utils.hasBackFacingCamera() || !Utils.hasFrontFacingCamera()){
            return false;
        }
        if(currentCameraFacing==Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraFacing=Camera.CameraInfo.CAMERA_FACING_FRONT;
        }else if(currentCameraFacing==Camera.CameraInfo.CAMERA_FACING_FRONT){
            currentCameraFacing=Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        mCamera.stopPreview();//停掉原来摄像头的预览
        mCamera.release();//释放资源
        mCamera = null;//取消原来摄像头
        mCamera = cameraGetInstance(currentCameraFacing);
        cameraPreview.setmCamera(mCamera);
        return true;
    }
    @Override
    public void uiSetOnRecordingLisenter(VideoRecordingContracts.OnRecordingLisenter onRecordingLisenter) {
            this.onRecordingLisenter=onRecordingLisenter;
    }

    /**
     * @return
     */
    private Camera cameraGetInstance(int currentCameraFacing){
        Camera c = null;
        try {
            boolean hasBackFacingCamera=Utils.hasBackFacingCamera();
            boolean hasFrontFacingCamera=Utils.hasFrontFacingCamera();
            if( hasBackFacingCamera && hasFrontFacingCamera){
                try{
                    c = Camera.open(currentCameraFacing);
                    this.currentCameraFacing=currentCameraFacing;
                }catch (Exception ex){
                    c=null;
                }
            }
            if(c==null){
                c = Camera.open();
                this.currentCameraFacing=Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(presenter!=null){
            presenter.pSetCameraParam(this,c,this.currentCameraFacing);
        }
        return c;
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }
    private void deleteFileByPath(String filePath){
        try{
            if(new File(outputFilePath).exists()){
                new File(outputFilePath).delete();
            }
        }catch (Exception ex){

        }
    }
    private boolean prepareVideoRecorder(){
        if (mCamera==null) return false;
        mMediaRecorder = new MediaRecorder();
        return presenter.pPrepareVideoRecorder(mMediaRecorder,mCamera,currentCameraFacing,cameraPreview,outputFilePath,videoQuality);
    }
    private File refreshOutputFile(){
        File videoSavePath = SDCardUtil.getSDCardVideoDir();
        String SAVE_FILE_PATCH_Directory= videoSavePath.getAbsolutePath();
        String fileName = "video_"+System.currentTimeMillis()+".mp4";
        return new File(SAVE_FILE_PATCH_Directory+ "/" + fileName);
    }

    public boolean isLongEnough(){
        return Calendar.getInstance().getTimeInMillis() - timeStart >3000;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }
}
