package com.gwsd.open_ptt.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.activity.AudioCallActivity;
import com.gwsd.open_ptt.activity.ChatActivity;
import com.gwsd.open_ptt.activity.VideoCallActivity;
import com.gwsd.open_ptt.bean.NotifiDataBean;
import com.gwsd.open_ptt.broadcast.KeyReceiver;
import com.gwsd.open_ptt.config.DeviceConfig;
import com.gwsd.open_ptt.manager.AppManager;

public class MainService extends Service {

    public static final String CHANNEL_ID_STRING = "gwsd_ptt_service";
    public static final String MSG_CHANNEL_ID_STRING = "gwsd_ptt_msg";
    public static void startServer(Context context){
        Intent intent=new Intent(context,MainService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
            //context.startService(intent);
        }else{
            context.startService(intent);
        }
    }
    public static void startServerWithData(Context context, NotifiDataBean data){
        Intent intent=new Intent(context,MainService.class);
        intent.putExtra("notifidata", data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
            //context.startService(intent);
        }else{
            context.startService(intent);
        }
    }
    public static void stopServer(Context context){
        Intent intent=new Intent(context,MainService.class);
        context.stopService(intent);
    }

    private void log(String msg){
        Log.d(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }

    private KeyReceiver keyReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        log("service create");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE);
            mChannel.setDescription("service notify");
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                int serviceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                        | ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                        | ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                        | ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
                startForeground(1,notification, serviceType);
            } else {
                startForeground(1, notification);
            }
        }
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("service command");
        if (intent != null) {
            Bundle data = intent.getExtras();
            if (data != null) {
                NotifiDataBean notifiDataBean = (NotifiDataBean) data.getSerializable("notifidata");
                if (notifiDataBean != null) {
                    if (!AppManager.getInstance().isForeground()) {
                        log("app is background");
                        showNotification(notifiDataBean);
                    } else {
                        log("app is not background");
                    }
                }
            }
        }
        return START_STICKY;
    }

    private void showNotification(NotifiDataBean notifiDataBean) {
        String title = "";
        String content = "";
        String convNm = "";
        Intent intent = null;
        int type = notifiDataBean.getType();
        if (type == NotifiDataBean.NOTIFI_TYPE_MSG) {
            if (notifiDataBean.getRecvType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER) {
                title = notifiDataBean.getSendNm();
                if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT) {
                    content = notifiDataBean.getContent();
                } else if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_PHOTO) {
                    content = getString(R.string.chat_imtype_img);
                } else if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VOICE) {
                    content = getString(R.string.chat_imtype_voice);
                } else if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO) {
                    content = getString(R.string.chat_imtype_video);
                } else {
                    content = notifiDataBean.getContent();
                }
                convNm = notifiDataBean.getSendNm();
            } else {
                title = notifiDataBean.getRecvNm();
                if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT) {
                    content = notifiDataBean.getSendNm() + ":" + notifiDataBean.getContent();
                } else if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_PHOTO) {
                    content = notifiDataBean.getSendNm() + ":" + getString(R.string.chat_imtype_img);
                } else if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VOICE) {
                    content = notifiDataBean.getSendNm() + ":" + getString(R.string.chat_imtype_voice);
                } else if (notifiDataBean.getMsgType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO) {
                    content = notifiDataBean.getSendNm() + ":" + getString(R.string.chat_imtype_video);
                } else {
                    content = notifiDataBean.getSendNm() + ":" + notifiDataBean.getContent();
                }
                convNm = notifiDataBean.getRecvNm();
            }
            intent = ChatActivity.getStartIntent(this, notifiDataBean.getRecvId(), convNm, notifiDataBean.getRecvType());
        } else {
            int remoteid = notifiDataBean.getRecvId();
            String remoteidStr = notifiDataBean.getRecvIdStr();;
            String remotenm = notifiDataBean.getRecvNm();
            boolean record = notifiDataBean.isRecord();
            title = remotenm;
            if (type == NotifiDataBean.NOTIFI_TYPE_AUDIO_CALL) {
                content = getString(R.string.invite_to_voice_call);
                intent = AudioCallActivity.getStartIntent(this, remoteid, remotenm);
            } else {
                content = getString(R.string.invite_to_video_call);
                intent = VideoCallActivity.getStartIntent(this, remoteidStr, remotenm, record);
            }
        }
        int flag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        log("send notice");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(MSG_CHANNEL_ID_STRING, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("msg notify");
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MSG_CHANNEL_ID_STRING)
                .setSmallIcon(R.mipmap.ic_logo_gw_desktop)
                .setLargeIcon(((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_logo_gw_desktop)).getBitmap())
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(2, builder.build());
    }

    private void release(){
        log("call release");
        unregisterReceiver(keyReceiver);
        keyReceiver = null;
    }
    private void init(){
        log("call init");
        keyReceiver = new KeyReceiver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(keyReceiver, getIntentFilter(), Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(keyReceiver, getIntentFilter());
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        for (String broadcast : DeviceConfig.DEVICE_KEY_BROADCAST.getBroadcastArray()) {
            intentFilter.addAction(broadcast);
        }
        return intentFilter;
    }


}
