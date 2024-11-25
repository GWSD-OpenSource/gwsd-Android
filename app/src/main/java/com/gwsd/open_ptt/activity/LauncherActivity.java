package com.gwsd.open_ptt.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.manager.AppManager;
import com.gwsd.open_ptt.service.MainService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = "GWAPP";
    //final int REQUEST_CODE_PERMISSION=100;

    ImageView viewLogo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Gw_Phone);
        setContentView(R.layout.activity_launcher);
        viewLogo = findViewById(R.id.viewLogo);
        viewLogo.setImageResource(R.mipmap.ic_logo_gw_day);
        init();
    }

    private Handler mhandle = new Handler();

    private void init() {
        if(checkAndHandleRepeatStartApp()){
            Log.i(TAG, "app have started");
            return;
        }
        Log.i(TAG, "start app request permissions");
        updateResources(this.getApplicationContext());
        requestPermissions();
    }

    public String getSysCountry() {
        return Locale.getDefault().getCountry();
    }

    private void updateResources(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context = context.getApplicationContext();
            Resources resources = context.getResources();
            Locale locale = null;//
            String language = Locale.getDefault().getLanguage();
            if ("de".equals(language) || "ru".equals(language) || "tr".equals(language) || "zh".equals(language)
                    || "es".equals(language) || "fr".equals(language) || "in".equals(language) || "it".equals(language) || "ko".equals(language)
                    || "pt-rBR".equals(language) || "th".equals(language) || "tl".equals(language)) {
                locale = Locale.getDefault();
            }else{
                locale = Locale.ENGLISH;
            }
            if (locale != null) {
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                configuration.setLocales(new LocaleList(locale));
                Context context1 = context.createConfigurationContext(configuration);
                AppManager.getInstance().updateAppContext(context1);
            }
        }else{
            String country = getSysCountry();
            Locale mDefaultLocale = null;
            if (Locale.CHINA.getCountry().equals(country)) {
                mDefaultLocale = Locale.SIMPLIFIED_CHINESE;
            } else {
                mDefaultLocale = Locale.ENGLISH;
            }
            updateResourcesLegacy(context, mDefaultLocale, 1.0F);
        }
    }

    private Context updateResourcesLegacy(Context context, Locale locale, float fontScale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
            configuration.setLayoutDirection(locale);
        } else {
            configuration.locale = locale;
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    boolean requestPermissionOk = true;
                    for (String s : permissionsGroupList) {
                        if (result.get(s) != null && result.get(s).equals(true)) {
                            Log.i(TAG, "request "+s+" permission success");
                        } else {
                            Log.i(TAG, "request "+s+" permission fail");
                            requestPermissionOk = false;
                            break;
                        }
                    }
                    if (requestPermissionOk) {
                        requestAppPermissionsResultOk();
                    } else {
                        MyApp.exitApp();
                    }
                }
            });

    List<String> permissionsGroupList=null;
    private void requestPermissions() {
        if(permissionsGroupList==null)permissionsGroupList=new ArrayList<>();
        permissionsGroupList.clear();
        permissionsGroupList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsGroupList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsGroupList.add(Manifest.permission.RECORD_AUDIO);
        permissionsGroupList.add(Manifest.permission.READ_PHONE_STATE);
        permissionsGroupList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsGroupList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsGroupList.add(Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
        permissionsGroupList.add(Manifest.permission.WAKE_LOCK);
        permissionsGroupList.add(Manifest.permission.CAMERA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int isNeedRequest = 0;
            for (String s : permissionsGroupList) {
                if(checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED){
                    isNeedRequest = 1;
                    break;
                }else{

                }
            }
            if (isNeedRequest != 0) {
                //requestPermissions(permissionsGroupList.toArray(new String[permissionsGroupList.size()]), REQUEST_CODE_PERMISSION);
                requestPermissionsLauncher.launch(permissionsGroupList.toArray(new String[permissionsGroupList.size()]));
            }else{
                requestAppPermissionsResultOk();
            }
        }else {
            requestAppPermissionsResultOk();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_PERMISSION && grantResults.length == permissionsGroupList.size()) {
//            requestAppPermissionsResultOk();
//        } else {
//            MyApp.exitApp();
//        }
//    }

    private void requestAppPermissionsResultOk() {
        startMain();
    }

    private void startMain() {
        MainService.startServer(this);

        mhandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(LauncherActivity.this, LoginActivity.class);
                LauncherActivity.this.startActivity(intent);
                finish();
            }
        }, 500);
    }

    public boolean appBringToFront(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(100);
        ActivityManager.RunningTaskInfo taskInfoCur = null;
        Iterator var4 = list.iterator();

        while(var4.hasNext()) {
            ActivityManager.RunningTaskInfo taskInfo = (ActivityManager.RunningTaskInfo)var4.next();
            if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                taskInfoCur = taskInfo;
                break;
            }
        }

        if (taskInfoCur != null) {
            activityManager.moveTaskToFront(taskInfoCur.id, ActivityManager.MOVE_TASK_WITH_HOME);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkAndHandleRepeatStartApp(){
        if(checkHasRepeatStartApp()){
            AppManager.getInstance().finshAct(android.app.LauncherActivity.class);
            finish();
            appBringToFront(this);
            return true;
        }
        return false;
    }
    private boolean checkHasRepeatStartApp(){
        boolean hasRepeatStartApp=false;
        if(AppManager.getActList().size()>0){
            for (Activity activity : AppManager.getActList()) {
                if( activity != this){
                    hasRepeatStartApp=true;
                }else {
                }
            }
        }
        return hasRepeatStartApp;
    }
}
