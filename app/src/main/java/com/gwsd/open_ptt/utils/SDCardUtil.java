package com.gwsd.open_ptt.utils;

import android.os.Environment;

import java.io.File;

public class SDCardUtil {

    public static File getSDCardVoiceDir() {
        File sdcardVoiceDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        return sdcardVoiceDir;
    }

    public static File getSDCardVideoDir() {
        File sdcardVoiceDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        return sdcardVoiceDir;
    }

    public static File getSDCardPhotoDir() {
        File sdcardVoiceDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return sdcardVoiceDir;
    }

    public static File getSDCardPhotoDir2() {
        File sdcardVoiceDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return sdcardVoiceDir;
    }

    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static File getSDCardRootDir() {
        File sdcardRootDir = Environment.getExternalStorageDirectory();
        return sdcardRootDir;
    }

}
