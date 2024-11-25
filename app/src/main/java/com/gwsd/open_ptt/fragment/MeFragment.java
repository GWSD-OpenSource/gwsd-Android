package com.gwsd.open_ptt.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.manager.AppManager;
import com.gwsd.open_ptt.manager.GWSDKManager;

public class MeFragment extends BaseFragment {

    private TextView viewAppVersion;
    private TextView viewSdkVersion;

    String appversion;
    int appversioncode;
    String sdkversion;

    public static MeFragment build() {
        MeFragment meFragment = new MeFragment();
        return meFragment;
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_me;
    }

    @Override
    protected void initData() {
        sdkversion = GWSDKManager.getSdkManager().getVersion();
        PackageManager manager = AppManager.getApp().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(AppManager.getApp().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info == null) {
            info = new PackageInfo();
            info.versionName = "";
            info.versionCode = 0;
        }
        appversion = info.versionName;
        appversioncode = info.versionCode;
    }

    @Override
    protected void initView() {
        viewAppVersion = contentView.findViewById(R.id.viewCurrentAppVersion);
        viewSdkVersion = contentView.findViewById(R.id.viewCurrentSdkVersion);

        viewSdkVersion.setText(sdkversion);
        viewAppVersion.setText(appversion+":"+appversioncode);
    }

    @Override
    protected void initEvent() {

    }
}
