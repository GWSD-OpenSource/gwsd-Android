package com.gwsd.open_ptt.utils;

import android.media.MediaRecorder;
import android.util.Log;

import com.gwsd.open_ptt.MyApp;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecorderUtil {
    public interface OnRecordCallback{
        void onTimerCall(String flag);
    }
    private String mFileName = null;
    private MediaRecorder mRecorder = null;
    private long startTime;
    private long timeInterval;
    private boolean isRecording;
    OnRecordCallback onRecordCallback;

    private void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public RecorderUtil(){

    }
    public void generalFileName(){
        File voiceSavePath = SDCardUtil.getSDCardVoiceDir();
        String fileName = "voice_"+System.currentTimeMillis()+".mp3";
        mFileName = voiceSavePath.getAbsolutePath()+"/"+ fileName;
    }
    public void setmFileName(String filename){
        this.mFileName = filename;
    }

    Integer audioEncodingBitRate=null;//16000
    Integer audioSamplingRate=null;//8000
    Integer audioSource=null;//MediaRecorder.AudioSource.MIC

    public void setAudioEncodingBitRate(Integer audioEncodingBitRate) {
        this.audioEncodingBitRate = audioEncodingBitRate;
    }
    public void setAudioSamplingRate(Integer audioSamplingRate) {
        this.audioSamplingRate = audioSamplingRate;
    }
    public void setAudioSource(Integer audioSource) {
        this.audioSource = audioSource;
    }
    /**
     * 开始录音
     */
    public boolean startRecording() {
        if (mFileName == null){
            return false;
        }
        if (isRecording){
            mRecorder.release();
            mRecorder = null;
        }

        mRecorder = new MediaRecorder();
        if(audioSource!=null){
            mRecorder.setAudioSource(audioSource.intValue());
        }else {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        if(audioEncodingBitRate!=null){
            mRecorder.setAudioEncodingBitRate(audioEncodingBitRate.intValue());
        }
        if(audioSamplingRate!=null){
            mRecorder.setAudioSamplingRate(audioSamplingRate.intValue());
        }

        startTime = System.currentTimeMillis();
        startTimerTask();
        try {
            mRecorder.prepare();
            mRecorder.start();
            isRecording = true;
            return true;
        } catch (Exception e){
            e.printStackTrace();
            log("prepare() failed");
            return false;
        }
    }
    public void release(){
        stopRecording();
    }
    /**
     * 停止录音
     */
    public void stopRecording() {
        if (mFileName == null) return;
        timeInterval = System.currentTimeMillis() - startTime;
        try{
            stopTimerTask();
            if (timeInterval>1000){
                mRecorder.stop();
            }
            mRecorder.release();
            mRecorder = null;
            isRecording =false;
        }catch (Exception e){
            e.printStackTrace();
            log("release() failed");
        }
    }
    Disposable mDisposable;
    public void startTimerTask(){
        mDisposable=Observable.interval(1,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if(onRecordCallback!=null)onRecordCallback.onTimerCall("");
                });
    }
    public void stopTimerTask(){
        log("==stopTimerTask==");
        if(mDisposable!=null && !mDisposable.isDisposed()){
            mDisposable.dispose();
            mDisposable=null;
        }
    }
    public void setOnRecordCallback(OnRecordCallback onRecordCallback) {
        this.onRecordCallback = onRecordCallback;
    }

    /**
     * 获取录音文件
     */
    public byte[] getDate() {
        if (mFileName == null) return null;
        try{
            return readFile(new File(mFileName));
        }catch (IOException e){
            e.printStackTrace();
            log("read file error" + e);
            return null;
        }
    }
    /**
     * 获取录音文件地址
     */
    public String getFilePath(){
        return mFileName;
    }
    /**
     * 获取录音时长,单位秒
     */
    public long getTimeInterval() {
        return timeInterval/1000;
    }

    public boolean isRecording() {
        return isRecording;
    }

    /**
     * 将文件转化为byte[]
     *
     * @param file 输入文件
     */
    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }
}
