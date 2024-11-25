package com.gwsd.open_ptt.dao;

import android.content.Context;

import com.gwsd.open_ptt.dao.greendao.DaoMaster;
import com.gwsd.open_ptt.dao.greendao.DaoSession;

import org.greenrobot.greendao.database.Database;


public class DBManager {

    public static DaoSession initDB(Context context){
        // regular SQLite database
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context, "gwsd_ptt_db");
        Database db = helper.getWritableDb();
        // encrypted SQLCipher database
        // note: you need to add SQLCipher to your dependencies, check the build.gradle file
        // DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db-encrypted");
        // Database db = helper.getEncryptedWritableDb("encryption-key");
//        DaoMaster.newDevSession(context,"appfragm_db")
        DaoSession daoSession = new DaoMaster(db).newSession();
        return daoSession;
    }

}
