package com.gwsd.open_ptt.comm_ui.video_record.contracts;

import android.hardware.Camera;
import android.media.MediaRecorder;

import androidx.fragment.app.Fragment;

import com.gwsd.open_ptt.comm_ui.video_record.help.VideoRecoidingResultBean;
import com.gwsd.open_ptt.view.CameraPreview;


/**
 * Created by Nicky on 2017/12/15.
 */

public interface VideoRecordingContracts {

    interface VideoRedcordingModel{

    }
    interface VideoRecordingUI{
        void uiToast(int resId);
        void uiStartRecording();
        void uiStopRecording();
        boolean uiIsRecording();
        boolean uiCameraSwitchChange();
        void uiSetOnRecordingLisenter(OnRecordingLisenter onRecordingLisenter);

    }
    interface VideoRecordingPresenter  {
        boolean pPrepareVideoRecorder(MediaRecorder mMediaRecorder, Camera mCamera, int cameraFacing, CameraPreview cameraPreview, String outputFilePath, int videoQuality);
        void pSetCameraParam(Fragment fragment, Camera camera, int cameraFacing);
    }

    interface OnRecordingLisenter{
        void onResult(VideoRecoidingResultBean bean);
    }

}
