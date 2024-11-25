package com.gwsd.open_ptt.comm_ui.voice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.manager.AppManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Nicky on 2017/10/24.
 */

public class PlayTaskV2 implements MediaPlayer.OnCompletionListener {
    public interface OnPlayStateCallback{
        void onPlayState(PlayState playState, Music music);
    }
    private static final long TIME_UPDATE = 300L;
    private MediaPlayer mPlayer = new MediaPlayer();
    PlayState playState;
    Music mPlayingMusic;

    OnPlayStateCallback onPlayStateCallback;

    private void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public PlayTaskV2(){
        init();
    }
    private void init(){
        mPlayer.setOnCompletionListener(this);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    public void play(Music music) {
        this.mPlayingMusic=music;
        try {
            mPlayer.reset();
            if(music.getType()== Music.MusicType_RAW){
                Context context= AppManager.getApp();
                AssetFileDescriptor afd = context.getResources().openRawResourceFd(music.getRawId());
                mPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }else {
                mPlayer.setDataSource(music.getPath());
            }
            mPlayer.setLooping(music.isLoop);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.prepareAsync();
            playState=PlayState.STATE_PREPARING;
            mPlayer.setOnPreparedListener(mPreparedListener);
            mPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            if(onPlayStateCallback!=null)onPlayStateCallback.onPlayState(playState,music);
            mPlayer.setVolume(1f,1f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        stopSelf();
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPreparing()) {
                mPlayingMusic.setDuration(mPlayer.getDuration());
                start();
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
    };


    public boolean isPlaying() {
        return playState == PlayState.STATE_PLAYING;
    }

    public boolean isPausing() {
        return playState == PlayState.STATE_PAUSE;
    }

    public boolean isPreparing() {
        return playState == PlayState.STATE_PREPARING;
    }
    public boolean isIdle() {
        return playState == PlayState.STATE_IDLE;
    }

    void start() {
        if (!isPreparing() && !isPausing()) {
            return;
        }
        mPlayer.start();
        playState = PlayState.STATE_PLAYING;
        checkPlayTimeStart();
        if(onPlayStateCallback!=null)onPlayStateCallback.onPlayState(playState,mPlayingMusic);
    }
    private void stopSelf() {
        stop();
    }
    public void stop() {
        if (isIdle()) {
            return;
        }
        if(mPlayer!=null){
            if(isPlaying()){
                mPlayer.pause();
            }
            mPlayer.reset();
        }
        checkPlayTimeStop();
        if(isPlaying()){
            if(onPlayStateCallback!=null)onPlayStateCallback.onPlayState(PlayState.STATE_IDLE,mPlayingMusic);
        }
        playState = PlayState.STATE_IDLE;
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mPlayer.seekTo(msec);
        }
    }
    /**
     * 获取正在播放的歌曲[本地|网络]
     */
    public Music getPlayingMusic() {
        return mPlayingMusic;
    }
    private void checkPlayTimeEnd(){
        if(mPlayer==null || !mPlayer.isPlaying()){
            stopSelf();
        }else{
            int curPo=mPlayer.getCurrentPosition();
            long dur=mPlayingMusic.getDuration();
            if(curPo>=dur){
                stopSelf();
            }
        }
    }
    Disposable mDisposable;
    private void checkPlayTimeStart(){
        checkPlayTimeStop();
        mDisposable=Observable.interval(1,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    log("==onNext==");
                    checkPlayTimeEnd();
                });
    }
    private void checkPlayTimeStop(){
        log("==stopTime==");
        if(mDisposable!=null && !mDisposable.isDisposed()){
            mDisposable.dispose();;
            mDisposable=null;
        }
    }

    public void onDestroy() {
        if(mPlayer!=null){
            try{
                mPlayer.reset();
                mPlayer.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void quit() {
        stopSelf();
    }

    public void setOnPlayStateCallback(OnPlayStateCallback onPlayStateCallback) {
        this.onPlayStateCallback = onPlayStateCallback;
    }
}
