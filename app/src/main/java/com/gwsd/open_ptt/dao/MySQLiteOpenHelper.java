package com.gwsd.open_ptt.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.dao.greendao.DaoMaster;

import org.greenrobot.greendao.database.Database;


public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
    public MySQLiteOpenHelper(Context context, String name) {
        super(context, name);
    }

    private void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);

    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        if (oldVersion == newVersion) {
            log("onUpgrade==数据库是最新版本" + oldVersion + "，不需要升级");
            return;
        }
        log("onUpgrade==数据库从版本" + oldVersion + "升级到版本" + newVersion);
        if(oldVersion<newVersion ){ //add tab  IM and permission
            //MigrationHelper.getInstance().migrate(db, BroadCastNoticeBeanDao.class);
        }
        switch (oldVersion) {
            case 1:
//                String sql = "";
//                db.execSQL(sql);
            case 2:
            default:
                break;
        }
    }
}

