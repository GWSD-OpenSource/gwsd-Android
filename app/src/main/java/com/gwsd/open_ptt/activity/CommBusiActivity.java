package com.gwsd.open_ptt.activity;

import com.gwsd.open_ptt.bean.LoginEventBean;
import com.gwsd.open_ptt.bean.OfflineEventBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class CommBusiActivity extends BaseActivity {

    @Override
    protected void initData() {
        log("register event");
        EventBus.getDefault().register(this);
    }

    @Override
    protected void release() {
        super.release();
        log("unregister event");
        EventBus.getDefault().unregister(this);
    }

    protected void processOffline(OfflineEventBean bean) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recvOfflineEvent(OfflineEventBean bean) {
        processOffline(bean);
    }

    protected void processLogin(LoginEventBean bean) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void recvLoginEvent(LoginEventBean bean) {
        processLogin(bean);
    }



}
