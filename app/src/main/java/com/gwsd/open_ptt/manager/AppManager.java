package com.gwsd.open_ptt.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AppManager implements Application.ActivityLifecycleCallbacks {

    static AppManager appManager;
    public static AppManager getInstance() {
        if (appManager == null) {
            Class var0 = AppManager.class;
            synchronized(AppManager.class) {
                if (appManager == null) {
                    appManager = new AppManager();
                }
            }
        }
        return appManager;
    }

    Context appContext;
    LinkedList<Activity> activityList = new LinkedList();
    final HashMap<Object, AppManager.OnAppStatusChangedListener> mStatusListenerMap = new HashMap();
    private int mForegroundCount = 0;
    private int mConfigCount = 0;
    private int activityCount = 0;
    boolean isForeground = false;

    public static Context getApp() {
        return getInstance().appContext;
    }

    private AppManager() {
    }

    public static List<Activity> getActList() {
        return getInstance().getActivityList();
    }

    public void init(Context context) {
        Application app = (Application)context.getApplicationContext();
        this.init(app, true);
    }

    public void updateAppContext(Context context) {
        this.appContext = context;
    }

    public void init(Application app) {
        this.init(app, true);
    }

    public void init(Application app, boolean isManagerAct) {
        if (app != null) {
            this.appContext = app.getApplicationContext();
            if (isManagerAct) {
                app.registerActivityLifecycleCallbacks(this);
            }
        } else {
            throw new RuntimeException(" context is not null");
        }
    }

    public void addListener(Object object, AppManager.OnAppStatusChangedListener listener) {
        this.mStatusListenerMap.put(object, listener);
    }

    public void removeListener(Object object) {
        this.mStatusListenerMap.remove(object);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++activityCount;
        if (mForegroundCount <= 0) {
            postStatus(true);
        }

        if (mConfigCount < 0) {
            ++mConfigCount;
        } else {
            ++mForegroundCount;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        --activityCount;
        if (activity.isChangingConfigurations()) {
            --mConfigCount;
        } else {
            --mForegroundCount;
            if (mForegroundCount <= 0) {
                postStatus(false);
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        removeActivity(activity);
        if (activityList != null && activityList.size() == 0) {
            sendActivityEmpty();
        }
    }

    protected void addActivity(Activity activity) {
        System.out.println("=addActivity==" + activity);
        if (activity != null && !this.activityList.contains(activity)) {
            this.activityList.add(activity);
        }

    }

    protected void removeActivity(Activity activity) {
        System.out.println("=removeActivity==" + activity);
        if (activity != null && this.activityList.contains(activity)) {
            this.activityList.remove(activity);
        }

    }

    public List<Activity> getActivityList() {
        return this.activityList;
    }

    public void finshAllOther(Activity activity) {
        Iterator it = this.activityList.iterator();

        while(it.hasNext()) {
            Activity acti = (Activity)it.next();
            if (acti != null && acti != activity) {
                acti.finish();
                it.remove();
            }
        }

    }

    public void finshActAll() {
        this.finshAllOther((Activity)null);
    }

    public void finshAct(Activity activity) {
        Iterator it = this.activityList.iterator();

        while(it.hasNext()) {
            Activity acti = (Activity)it.next();
            if (acti != null && acti == activity) {
                acti.finish();
                it.remove();
            }
        }

    }

    public void finshAct(Class clzz) {
        Iterator it = this.activityList.iterator();

        while(it.hasNext()) {
            Activity acti = (Activity)it.next();
            if (acti != null && acti.getClass() == clzz) {
                acti.finish();
                it.remove();
            }
        }

    }

    public void exit() {
        try {
            if (this.activityList != null) {
                Iterator it = this.activityList.iterator();

                while(it.hasNext()) {
                    Activity acti = (Activity)it.next();
                    if (acti != null) {
                        acti.finish();
                        it.remove();
                    }
                }

                this.activityList.clear();
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            Process.killProcess(Process.myPid());
            System.exit(0);
        }

    }

    private void postStatus(boolean isForeground) {
        this.isForeground = isForeground;
        if (!this.mStatusListenerMap.isEmpty()) {
            Iterator var2 = this.mStatusListenerMap.values().iterator();

            while(var2.hasNext()) {
                AppManager.OnAppStatusChangedListener onAppStatusChangedListener = (AppManager.OnAppStatusChangedListener)var2.next();
                if (onAppStatusChangedListener != null) {
                    onAppStatusChangedListener.onAppStatusChanged(isForeground);
                }
            }

        }
    }

    private void sendActivityEmpty() {
        Iterator var1 = this.mStatusListenerMap.values().iterator();

        while(var1.hasNext()) {
            AppManager.OnAppStatusChangedListener onAppStatusChangedListener = (AppManager.OnAppStatusChangedListener)var1.next();
            if (onAppStatusChangedListener != null) {
                onAppStatusChangedListener.onAppActSize(0);
            }
        }

    }

    public boolean isForeground() {
        return this.isForeground;
    }

    private void setTopActivity(Activity activity) {
        if (this.activityList.contains(activity)) {
            if (!((Activity)this.activityList.getLast()).equals(activity)) {
                this.activityList.remove(activity);
                this.activityList.addLast(activity);
            }
        } else {
            this.activityList.addLast(activity);
        }

    }

    public Activity getTopActivity() {
        Activity topActivityByReflect;
        if (!this.activityList.isEmpty()) {
            topActivityByReflect = (Activity)this.activityList.getLast();
            if (topActivityByReflect != null) {
                return topActivityByReflect;
            }
        }

        topActivityByReflect = this.getTopActivityByReflect();
        if (topActivityByReflect != null) {
            this.setTopActivity(topActivityByReflect);
        }

        return topActivityByReflect;
    }

    private Activity getTopActivityByReflect() {
        Context var1 = getApp();

        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke((Object)null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivityList");
            activitiesField.setAccessible(true);
            Map activities = (Map)activitiesField.get(activityThread);
            if (activities == null) {
                return null;
            }

            Iterator var6 = activities.values().iterator();

            while(var6.hasNext()) {
                Object activityRecord = var6.next();
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity)activityField.get(activityRecord);
                }
            }
        } catch (ClassNotFoundException var11) {
            var11.printStackTrace();
        } catch (IllegalAccessException var12) {
            var12.printStackTrace();
        } catch (InvocationTargetException var13) {
            var13.printStackTrace();
        } catch (NoSuchMethodException var14) {
            var14.printStackTrace();
        } catch (NoSuchFieldException var15) {
            var15.printStackTrace();
        }

        return null;
    }

    public interface OnAppStatusChangedListener {
        void onAppActSize(int var1);

        void onAppStatusChanged(boolean var1);
    }
}
