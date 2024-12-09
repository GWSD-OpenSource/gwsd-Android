package com.gwsd.open_ptt.utils;

import android.content.Context;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.gwsd.open_ptt.MyApp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by Nicky on 2018/6/12.
 *屏幕唤醒与解锁操作
 */
public class ScreenWakeUtils {
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    Context context;



    static  ScreenWakeUtils screenWakeUtils;
    static  ScreenWakeUtils screenWakeUtilsCpu;
    public static ScreenWakeUtils getInstace(Context context){

        if(screenWakeUtils==null){
            synchronized (ScreenWakeUtils.class){
                if(screenWakeUtils==null){
                    screenWakeUtils=new ScreenWakeUtils(context.getApplicationContext(),0);
                }
            }
        }
        return screenWakeUtils;
    }
    public static ScreenWakeUtils getInstaceCpu(Context context){

        if(screenWakeUtilsCpu==null){
            synchronized (ScreenWakeUtils.class){
                if(screenWakeUtilsCpu==null){
                    screenWakeUtilsCpu=new ScreenWakeUtils(context.getApplicationContext(),1);
                }
            }
        }
        return screenWakeUtilsCpu;
    }
    int type;
    private ScreenWakeUtils(Context context, int type){
        this.context=context;
        if(context==null)return;
        powerManager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.type=type;
        if(type==0){//
            wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"myapp:THCAppMyLog_all");
        }else if(type==1){//保持CPU唤醒
            wakeLock = powerManager.newWakeLock(PowerManager.ON_AFTER_RELEASE | PowerManager.PARTIAL_WAKE_LOCK,"myapp:THCAppMyLog_CPU");
        }

        wakeLock.setReferenceCounted(false);//不计数，多次open，只需要一次relese

        // PARTIAL_WAKE_LOCK:保持CPU 运转，屏幕和键盘灯有可能是关闭的 -- 最常用,保持CPU运转
        // SCREEN_DIM_WAKE_LOCK：保持CPU 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯
        // SCREEN_BRIGHT_WAKE_LOCK：保持CPU 运转，允许保持屏幕高亮显示，允许关闭键盘灯
        // FULL_WAKE_LOCK：保持CPU 运转，保持屏幕高亮显示，键盘灯也保持亮度
        // ACQUIRE_CAUSES_WAKEUP：强制使屏幕亮起，这种锁主要针对一些必须通知用户的操作.
        // ON_AFTER_RELEASE：当锁被释放时，保持屏幕亮起一段时间
    }
    private void log(String message){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+message);
    }
    /**
     * 获取系统休眠时间
     * @return
     */
    private int getScreenOffTime() {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(context.getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception localException) {
            screenOffTime=10*1000;
        }
        log("===getScreenOffTime:"+screenOffTime);
        return screenOffTime;
    }
    Method dismissKeyguard;
    Object objIWindowManager;

    public void openScreenAndUnLockOnly(){
        openScreenAndUnLockOnly(true);
    }

    /**
     *
     * @param isOpenLock 是否解锁
     */
    public void openScreenAndUnLockOnly(boolean isOpenLock){
        openScreenOnly();
        if(isOpenLock)openUnLock();
    }
    /**
     *
     * @param isOpenLock 是否解锁
     */
    public void openScreenAndUnLock(boolean isOpenLock){
        openScreen();
        if(isOpenLock)openUnLock();
    }

    boolean isLongOpen=false;//是否一直打开

    public ScreenWakeUtils setLongOpen(boolean longOpen) {
        isLongOpen = longOpen;
        return this;
    }

    public boolean isLongOpen() {
        return isLongOpen;
    }

    private boolean checkNeedOpen(){

//        if(powerManager!=null && powerManager.isScreenOn() ){
//            //屏幕亮着
//            log("==checkNeedOpen==isScreenOn=true");
//            return false;
//        }
        if(wakeLock==null){
            return false;
        }
        boolean isHeld=wakeLock.isHeld();
//        log("==checkNeedOpen==isHeld:"+isHeld+",译：是否可以执行唤醒wakeLock:"+(!isHeld));
        if(isHeld){
            //已经打开
            return false;
        }
        return true;
    }
    private boolean checkNeedClose(){
        if(isLongOpen()){
//            log("保持唤醒，不执行释放wakeLuck");
            return false;
        }
        if(wakeLock==null){
            return false;
        }
        boolean isHeld=wakeLock.isHeld();
//        log("==checkNeedClose=="+isHeld+"，译:是否可以执行释放wakeLock："+isHeld);
        if(!isHeld){
            //没有持有
            return false;
        }
        return true;
    }
    /**
     * 唤醒屏幕
     */
    public void openScreen(){
        if(checkNeedOpen()){
            log("openScreen");
            wakeLock.acquire();
        }
    }
    /**
     * 仅仅唤醒屏幕，
     */
    public void openScreenOnly(){
        if(!checkNeedOpen()){
            return;
        }
        try{
//            log("openScreenOnly");
            wakeLock.acquire(5*1000);
            wakeLock.release();
        }catch (RuntimeException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void openScreenOnly(long time){
        if(checkNeedOpen()){
            try{
                wakeLock.acquire(time);
            }catch (RuntimeException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 释放屏幕
     */
    public void releaseScreen(){
       if(checkNeedClose()){
           try{
//               log("releaseScreen");
               wakeLock.release();
           }catch (RuntimeException e){
               e.printStackTrace();
           }catch (Exception e){
               e.printStackTrace();
           }
       }
    }
    /**
     * 解锁
     */
    public void openUnLock(){
        dismissKeyguard();
    }
    private void dismissKeyguard() {
        try {
            log("开始打开锁");
            if(dismissKeyguard==null){
                Class clsServiceManager = Class.forName("android.os.ServiceManager");
                Method getService = clsServiceManager.getMethod("getService",String.class);
                Object winService = getService.invoke(clsServiceManager,new Object[] { new String("window") });
                Class clsIWindowManager = Class.forName("android.view.IWindowManager");
                Class clsStub = Class.forName("android.view.IWindowManager$Stub");
                Method asInterface = clsStub.getMethod("asInterface", IBinder.class);
                objIWindowManager = asInterface.invoke(clsStub, winService);
                Class IWindowManger = Class.forName("android.view.IWindowManager");
                dismissKeyguard= IWindowManger.getMethod("dismissKeyguard");
            }
            if(dismissKeyguard!=null && objIWindowManager!=null){
                dismissKeyguard.invoke(objIWindowManager);
                log("锁打开完毕");
            }
        } catch (NoSuchMethodException blocke) {
            // TODO Auto-generated catch blocke.printStackTrace();
            log("锁打开错误");
        } catch (ClassNotFoundException blocke) {// TODO Auto-generated catch

            log("锁打开错误");
        } catch (IllegalArgumentException blocke) {// TODO Auto-generated catch

            log("锁打开错误");
        } catch (IllegalAccessException blocke) {// TODO Auto-generated catch

            log("锁打开错误");
        } catch (InvocationTargetException blocke) {// TODO Auto-generated catch

            log("锁打开错误");
        }catch (Exception e){
        }

    }
}
