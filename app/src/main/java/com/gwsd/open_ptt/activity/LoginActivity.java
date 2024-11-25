package com.gwsd.open_ptt.activity;

import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.config.DeviceConfig;
import com.gwsd.open_ptt.manager.GWSDKManager;

public class LoginActivity extends BaseActivity {

    EditText viewInputUm;
    EditText viewInputPsw;
    TextView viewLogin;
    TextView viewMore;
    ImageView viewLogo;
    TextView viewUpdateApp;
    TextView viewLoginWelcome;

    @Override
    protected int getViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        viewInputUm = findViewById(R.id.view_input_username);
        viewInputPsw = findViewById(R.id.view_input_psw);
        viewLogin = findViewById(R.id.view_login);
        viewMore = findViewById(R.id.view_more);
        viewLogo = findViewById(R.id.view_login_logflag);
        viewUpdateApp = findViewById(R.id.viewUpdateApp);
        viewLoginWelcome = findViewById(R.id.view_login_welcome);
        viewLoginWelcome.setText(String.format(getString(R.string.login_welcome), getString(R.string.app_name)));
    }

    @Override
    protected void initEvent(){
        viewLogin.setOnClickListener(v->{
            String account = viewInputUm.getText().toString();
            String password = viewInputPsw.getText().toString();
            String imei = DeviceConfig.getDeviceImei();
            String iccid = DeviceConfig.getDeviceIccid();
            GWSDKManager.getSdkManager().login(account,password,imei,iccid);
        });
    }

    @Override
    protected void initData(){
        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGIN) {
                    GWLoginResultBean gwLoginResultBean = JSON.parseObject(data, GWLoginResultBean.class);
                    if (gwLoginResultBean.getResult() == 0) {
                        runOnUiThread(() -> {
                            MainActivity.startAct(getContext());
                            finish();
                        });
                    } else {
                        runOnUiThread(()->{
                            showToast(getContext().getString(R.string.hint_login_failure));
                        });
                    }
                } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_ERROR) {
                    runOnUiThread(()->{
                        showToast(getContext().getString(R.string.hint_network_err));
                    });
                }
            }
            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }

    @Override
    protected void release() {
        super.release();
        GWSDKManager.getSdkManager().registerPttObserver(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
