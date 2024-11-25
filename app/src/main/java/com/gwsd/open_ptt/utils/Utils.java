package com.gwsd.open_ptt.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;

import java.io.File;

public class Utils {

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    private static boolean checkCameraFacing(int facing) {
        if (getSdkVersion() < 9) {
            return false;
        } else {
            int cameraCount = Camera.getNumberOfCameras();
            Camera.CameraInfo info = new Camera.CameraInfo();

            for(int i = 0; i < cameraCount; ++i) {
                Camera.getCameraInfo(i, info);
                if (facing == info.facing) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean hasCamera() {
        return hasBackFacingCamera() || hasFrontFacingCamera();
    }

    public static boolean hasBackFacingCamera() {
        return checkCameraFacing(0);
    }

    public static boolean hasFrontFacingCamera() {
        return checkCameraFacing(1);
    }

    public static String intToTimer(int value) {
        int min = value / 60;
        int sec = value % 60;
        String minStr;
        if (min < 10) {
            minStr = "0" + min;
        } else {
            minStr = "" + min;
        }

        String secStr;
        if (sec < 10) {
            secStr = "0" + sec;
        } else {
            secStr = "" + sec;
        }

        return minStr + ":" + secStr;
    }

    public static void mediaScannerScanFile(Context context, File showFilePhotoOrVideo) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        Uri uri = Uri.fromFile(showFilePhotoOrVideo);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

}
