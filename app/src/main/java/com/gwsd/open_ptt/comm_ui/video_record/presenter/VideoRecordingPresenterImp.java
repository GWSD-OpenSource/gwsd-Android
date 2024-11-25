package com.gwsd.open_ptt.comm_ui.video_record.presenter;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;

import androidx.fragment.app.Fragment;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.comm_ui.video_record.contracts.VideoRecordingContracts;
import com.gwsd.open_ptt.config.DeviceConfig;
import com.gwsd.open_ptt.view.CameraPreview;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nicky on 2017/12/15.
 */

public class VideoRecordingPresenterImp implements VideoRecordingContracts.VideoRecordingPresenter{

    VideoRecordingContracts.VideoRecordingUI ui;
    private void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public VideoRecordingPresenterImp(VideoRecordingContracts.VideoRecordingUI ui){
        this.ui=ui;

    }
    private CamcorderProfile getCamcorderProfile(){
        return getCamcorderProfile(CamcorderProfile.QUALITY_1080P);
    }
    private CamcorderProfile getCamcorderProfile(int videoQuality){
        CamcorderProfile mProfile =null;
        if(videoQuality!=-1){
            mProfile=CamcorderProfile.get(videoQuality);
        }
        return mProfile;
    }
    /**
     *
     *
     * @param mMediaRecorder
     * @param mCamera
     * @param cameraFacing
     *  摄像头方向
     *  Camera.CameraInfo.CAMERA_FACING_BACK
     *  Camera.CameraInfo.CAMERA_FACING_FRONT
     * @param cameraPreview
     * @param outputFilePath
     * @param videoQuality
     * @return
     */
    @Override
    public boolean pPrepareVideoRecorder(MediaRecorder mMediaRecorder, Camera mCamera, int cameraFacing,
                                         CameraPreview cameraPreview, String outputFilePath, int videoQuality) {
        if (mCamera==null) return false;
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());
        mMediaRecorder.setOutputFile(outputFilePath);       //输出路径
        //设置视频输出的格式和编码
        CamcorderProfile mProfile =getCamcorderProfile();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// 音频源率，然后就清晰了
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        //mMediaRecorder.setVideoFrameRate(25);
        try{
            if(mProfile!=null){
                mMediaRecorder.setVideoSize(mProfile.videoFrameWidth, mProfile.videoFrameHeight);
                mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);//码流,清晰度
            }else {
                mMediaRecorder.setVideoEncodingBitRate(5* 1024 * 1024);//码流,清晰度
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// 视频录制格式
        mMediaRecorder.setAudioEncodingBitRate(44100);  //44100
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式

        try {
            if(cameraFacing==Camera.CameraInfo.CAMERA_FACING_BACK){
                mMediaRecorder.setOrientationHint(DeviceConfig.DEVICE_CAMERA_ORIENTATION.back_camera_orientation);
            }else if(cameraFacing==Camera.CameraInfo.CAMERA_FACING_FRONT){
                mMediaRecorder.setOrientationHint(DeviceConfig.DEVICE_CAMERA_ORIENTATION.front_camera_orientation);
            }
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
            return false;
        }
        return true;
    }
    private Camera.Size mSize = null;//相机的尺寸
    @Override
    public void pSetCameraParam(Fragment fragment, Camera mCamera, int cameraFacing) {
        if(mCamera==null) return;
        CameraSizeComparator sizeComparator = new CameraSizeComparator();
        Camera.Parameters parameters = mCamera.getParameters();
        if (mSize == null) {
            List<Camera.Size> vSizeList = parameters.getSupportedPreviewSizes();
            Collections.sort(vSizeList, sizeComparator);

            for (int num = 0; num < vSizeList.size(); num++) {
                Camera.Size size = vSizeList.get(num);

                if (size.width >= 800 && size.height >= 480) {
                    this.mSize = size;
                    break;
                }
            }
            mSize = vSizeList.get(0);
            List<String> focusModesList = parameters.getSupportedFocusModes();
            //增加对聚焦模式的判断
            if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (focusModesList.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }
//        if(parameters.isSmoothZoomSupported()){
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//        }
        if(cameraFacing==Camera.CameraInfo.CAMERA_FACING_BACK){
//            CamcorderProfile mProfile =getCamcorderProfile();
//            if(mProfile!=null){
//                parameters.setPreviewSize(mProfile.videoFrameHeight,mProfile.videoFrameWidth);
//            }
        }
        mCamera.setParameters(parameters);
//        setCameraDisplayOrientation(fragment.getActivity(),cameraFacing,mCamera);
        if(cameraFacing==Camera.CameraInfo.CAMERA_FACING_BACK){
            mCamera.setDisplayOrientation(90);
        }else if(cameraFacing==Camera.CameraInfo.CAMERA_FACING_FRONT){
            mCamera.setDisplayOrientation(90);
        }
//        mCamera.unlock();
    }
    private  void setCameraDisplayOrientation(Activity activity,
                                              int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    private class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }

}
