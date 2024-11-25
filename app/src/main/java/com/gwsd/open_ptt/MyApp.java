package com.gwsd.open_ptt;

import android.app.Application;

import com.gwsd.open_ptt.dao.DBManager;
import com.gwsd.open_ptt.dao.greendao.DaoSession;
import com.gwsd.open_ptt.manager.AppManager;
import com.gwsd.open_ptt.manager.CallManager;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.service.MainService;

public class MyApp extends Application {

    public static final String TAG = "GWAPP";

    static MyApp myApp;
    public static MyApp getInstance(){
        return myApp;
    }

    DaoSession daoSession;
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        init();
    }

    private void init() {
        AppManager.getInstance().init(this, true);
        this.daoSession = DBManager.initDB(this);
        GWSDKManager.INSTANCE(AppManager.getApp());
        CallManager.INSTANCE(AppManager.getApp());
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static void exitApp(){
        MainService.stopServer(myApp);
        int myPid = android.os.Process.myPid();
        AppManager.getInstance().exit();
        android.os.Process.killProcess(myPid);
        System.exit(0);
        System.gc();
    }

}
