package com.gwsd.open_ptt.manager;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import com.gwsd.open_ptt.MyApp;

public class CallManager {

    /*
     * priority videocall=audiocall > temp group call > ptt group call
     */
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_PTT_GROUP_CALL = CALL_STATE_IDLE;
    public static final int CALL_STATE_PTT_TMP_GROUP_CALL = 1;
    public static final int CALL_STATE_AUDIO_VIDEO_CALL = 2;

    public interface OnCallStateSwitch {
        public void callStateSwitch(boolean canswitch, int oldstate, int newstate);
    }

    private static CallManager callManager;
    public static CallManager INSTANCE(Context context) {
        if (callManager == null) {
            synchronized (CallManager.class) {
                if (callManager == null) {
                    callManager = new CallManager(context);
                }
            }
        }
        return callManager;
    }
    public static CallManager getManager() {
        return callManager;
    }

    private void log(String msg) {
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }

    private AudioManager audioManager;
    private int callState;
    private int avType; // 0 audio 1 video
    private CallManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        callState = CALL_STATE_IDLE;
    }

    public void enterPttGroupCall() {
        callState = CALL_STATE_PTT_GROUP_CALL;
    }

    public void exitPttGroupCall() {
        callState = CALL_STATE_IDLE;
    }

    public void enterPttTmpGroupCall(OnCallStateSwitch onCallStateSwitch) {
        int pendingState = callState;
        boolean canswitch;
        if (callState == CALL_STATE_PTT_GROUP_CALL) {
            // goto ptt call activity for tmp group
            callState = CALL_STATE_PTT_TMP_GROUP_CALL;
            canswitch = true;
        } else {
            // call state is not idle exit tmp call
            log("current call state "+callState+" is high priority enter tmp group call fail");
            canswitch = false;
        }
        if (onCallStateSwitch != null) {
            onCallStateSwitch.callStateSwitch(canswitch, pendingState, callState);
        }
    }

    public void exitPttTmpGroupCall() {
        callState = CALL_STATE_IDLE;
    }

    public void enterAudioVideoCall(int type, OnCallStateSwitch onCallStateSwitch) {
        int pendingState = callState;
        boolean canswitch;
        if (callState == CALL_STATE_PTT_GROUP_CALL) {
            // goto video/audio call activity
            log("current0 call state "+callState+" recv audio/video("+type+") call request");
            callState = CALL_STATE_AUDIO_VIDEO_CALL;
            avType = type;
            canswitch = true;
        } else if (callState == CALL_STATE_PTT_TMP_GROUP_CALL) {
            log("current1 call state "+callState+" recv audio/video("+type+") call request");
            callState = CALL_STATE_AUDIO_VIDEO_CALL;
            // exit tmp group call
            avType = type;
            canswitch = true;
        } else {
            // call state is not idle exit tmp call
            log("current call state "+callState+" is high priority enter audio/video call fail"+":"+avType);
            canswitch = false;
        }
        if (onCallStateSwitch != null) {
            onCallStateSwitch.callStateSwitch(canswitch, pendingState, callState);
        }
    }

    public void exitAudioVideoCall(int type) {
        log("exit audio or video call reset idle from:"+type+"/"+avType);
        callState = CALL_STATE_IDLE;
    }

    public int getCallState() {
        return callState;
    }

    private void printAudio(){
        int volume=audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        int getMode=audioManager.getMode();
        int getRingerMode=audioManager.getRingerMode();
        boolean isBluetoothScoAvailableOffCall=audioManager.isBluetoothScoAvailableOffCall();
        boolean isBluetoothScoOn=audioManager.isBluetoothScoOn();
        boolean isBluetoothA2dpOn=audioManager.isBluetoothA2dpOn();
        boolean isWiredHeadsetOn=audioManager.isWiredHeadsetOn();
        boolean isSpeakerphoneOn=audioManager.isSpeakerphoneOn();
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("volume:").append(volume).append("\n");
        stringBuffer.append("getMode:").append(getMode).append("\n");
        stringBuffer.append("getRingerMode:").append(getRingerMode).append("\n");
        stringBuffer.append("isBluetoothScoAvailableOffCall:").append(isBluetoothScoAvailableOffCall).append("\n");
        stringBuffer.append("isBluetoothScoOn:").append(isBluetoothScoOn).append("\n");
        stringBuffer.append("isBluetoothA2dpOn:").append(isBluetoothA2dpOn).append("\n");
        stringBuffer.append("isWiredHeadsetOn:").append(isWiredHeadsetOn).append("\n");
        stringBuffer.append("isSpeakerphoneOn:").append(isSpeakerphoneOn).append("\n");
        log(stringBuffer.toString());
        stringBuffer.setLength(0);
    }

    public void changeToSpeaker() {
        log("change to speaker");
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(true);
        printAudio();
    }

    public void changeToHandset() {
        log("change to handset");
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(false);
        printAudio();
    }

    public void changeToHeadset() {
        //有线耳机模式
        log("change to headset");
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        audioManager.stopBluetoothSco();
        audioManager.setBluetoothScoOn(false);
        audioManager.setSpeakerphoneOn(false);
        printAudio();
    }

    public int getMaxVolume(int streamType) {
        int max = audioManager.getStreamMaxVolume(streamType);
        return max;
    }

    public int getVolume(int streamType) {
        int volume;
        volume = audioManager.getStreamVolume(streamType);
        return volume;
    }

    public void setVolume(int streamType, int volume, boolean uiShow){
        int max = getMaxVolume(streamType);
        if (volume > max)  {
            volume = max;
        }
        if (volume < 0) {
            volume = 0;
        }
        if (uiShow) {
            audioManager.setStreamVolume(streamType, volume, AudioManager.FLAG_SHOW_UI);
        } else {
            audioManager.setStreamVolume(streamType, volume, AudioManager.FX_KEY_CLICK);
        }
    }

}
