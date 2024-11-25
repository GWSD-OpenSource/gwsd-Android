package com.gwsd.open_ptt.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.activity.ChatActivity;
import com.gwsd.open_ptt.bean.NotifiDataBean;
import com.gwsd.open_ptt.broadcast.KeyReceiver;
import com.gwsd.open_ptt.config.DeviceConfig;
import com.gwsd.open_ptt.manager.AppManager;

public class MainService extends Service {

    public static final String CHANNEL_ID_STRING = "gwsd_ptt_service";

    public static void startServer(Context context){
        Intent intent=new Intent(context,MainService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //context.startForegroundService(intent);
            context.startService(intent);
        }else{
            context.startService(intent);
        }
    }
    public static void startServerWithData(Context context, NotifiDataBean data){
        Intent intent=new Intent(context,MainService.class);
        intent.putExtra("notifidata", data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //context.startForegroundService(intent);
            context.startService(intent);
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

    @Override
    public void onCreate() {
        super.onCreate();
        log("service create");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription("msg notify");
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
        }
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("service command");
        Bundle data = intent.getExtras();
        if (data != null) {
            NotifiDataBean notifiDataBean = (NotifiDataBean)data.getSerializable("notifidata");
            if (notifiDataBean != null) {
                if (!AppManager.getInstance().isForeground()) {
                    log("app is background");
                    showNotification(notifiDataBean);
                } else {
                    log("app is not background");
                }
            }
        }
        return START_STICKY;
    }

    private void showNotification(NotifiDataBean notifiDataBean) {
        String title = "";
        String content = "";
        String convNm = "";
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
        Intent intent = ChatActivity.getStartIntent(this, notifiDataBean.getRecvId(), convNm, notifiDataBean.getRecvType());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID_STRING)
                .setSmallIcon(R.mipmap.ic_logo_gw_desktop)
                .setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.mipmap.ic_logo_gw_desktop)).getBitmap())
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }

    private void release(){
        log("call release");
        unregisterReceiver(keyReceiver);
        keyReceiver = null;
    }
    private void init(){
        log("call init");
        keyReceiver = new KeyReceiver();
        registerReceiver(keyReceiver, getIntentFilter());
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        for (String broadcast : DeviceConfig.DEVICE_KEY_BROADCAST.getBroadcastArray()) {
            intentFilter.addAction(broadcast);
        }
        return intentFilter;
    }


}
