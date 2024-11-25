package com.gwsd.open_ptt.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.config.DeviceConfig;
import com.gwsd.open_ptt.manager.GWSDKManager;

public class KeyReceiver extends BroadcastReceiver {

    private void log(String msg){
        Log.d(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        log("recv action="+action);
        if (action.equals(DeviceConfig.DEVICE_KEY_BROADCAST.PTT_KEY_DOWN)) {
            if (GWSDKManager.getSdkManager().isOnline()) {
                GWSDKManager.getSdkManager().startSpeak();
            }
        } else if (action.equals(DeviceConfig.DEVICE_KEY_BROADCAST.PTT_KEY_UP)) {
            if (GWSDKManager.getSdkManager().isOnline()) {
                GWSDKManager.getSdkManager().stopSpeak();
            }
        } else {
            // you can process other broadcast
        }
    }
}
