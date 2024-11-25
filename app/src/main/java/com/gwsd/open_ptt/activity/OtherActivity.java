//package com.gwsd.ptt.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.gwsd.bean.GWMsgResponseBean;
//import com.gwsd.bean.GWType;
//import com.gwsd.ptt.R;
//import com.gwsd.ptt.manager.GWSDKManager;
//
//public class OtherActivity extends BaseActivity {
//
//    Button btnWeather;
//    Button btnGps;
//    Button btnAGps;
//
//    TextView viewWeather;
//    TextView viewLocation;
//
//    public static void startAct(Context context) {
//        Intent intent = new Intent(context, OtherActivity.class);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_other);
//
//        initData();
//        initView();
//        initEvent();
//    }
//
//    @Override
//    protected int getViewId() {
//        return R.layout.activity_other;
//    }
//
//    @Override
//    protected void initData() {
//        gwsdkManager = GWSDKManager.INSTANCE(getApplicationContext());
//        gwsdkManager.registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
//            @Override
//            public void onPttEvent(int var1, String var2, int var3) {
//
//            }
//
//            @Override
//            public void onMsgEvent(int status, String data) {
//                runOnUiThread(()->{
//                    if (status == GWType.GW_MSG_STATUS.GW_MSG_STATUS_WEATHER) {
//                        if (!TextUtils.isEmpty(data)) {
//                            GWMsgResponseBean gwMsgResponseBean = JSON.parseObject(data, GWMsgResponseBean.class);
//                            GWMsgResponseBean.Weather weather = JSON.parseObject(JSONObject.toJSONString(gwMsgResponseBean.getData()), GWMsgResponseBean.Weather.class);
//                            viewWeather.setText(weather.getWeath());
//                        }
//                    } else if (status == GWType.GW_MSG_STATUS.GW_MSG_STATUS_ADDRESS) {
//                        if (!TextUtils.isEmpty(data)) {
//                            GWMsgResponseBean gwMsgResponseBean = JSON.parseObject(data, GWMsgResponseBean.class);
//                            GWMsgResponseBean.Address address = JSON.parseObject(JSONObject.toJSONString(gwMsgResponseBean.getData()), GWMsgResponseBean.Address.class);
//                            viewWeather.setText(address.getAds());
//                        }
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    protected void initView() {
//        btnWeather = findViewById(R.id.btnWeather);
//        btnGps = findViewById(R.id.btnGps);
//        btnAGps = findViewById(R.id.btnAGps);
//
//        viewWeather = findViewById(R.id.viewWeather);
//        viewLocation = findViewById(R.id.viewLocation);
//    }
//
//    @Override
//    protected void initEvent() {
//        btnWeather.setOnClickListener(v -> {
//            int cellid = 0;
//            int lac = 0;
//            int netmode = GWType.GW_NETWORK_TYPE.GW_PTT_NETWORK_4G;
//            String mcc = "460";
//            String mnc = "03";
//            gwsdkManager.getWeather(cellid, lac, netmode, mcc, mnc);
//        });
//
//        btnGps.setOnClickListener(v->{
//            double lat = 40.059355;
//            double lon = 116.608368;
//            gwsdkManager.reportGps(lat, lon);
//        });
//
//        btnAGps.setOnClickListener(v -> {
//            int cellid = 0;
//            int lac = 0;
//            int netmode = GWType.GW_NETWORK_TYPE.GW_PTT_NETWORK_4G;
//            String mcc = "460";
//            String mnc = "03";
//            gwsdkManager.reportBaseStation(cellid, lac, netmode, mcc, mnc);
//        });
//    }
//}
