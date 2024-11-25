package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.bean.LoginEventBean;
import com.gwsd.open_ptt.bean.OfflineEventBean;
import com.gwsd.open_ptt.dao.pojo.MsgConversationPojo;
import com.gwsd.open_ptt.fragment.BaseFragment;
import com.gwsd.open_ptt.fragment.ChatListFragment;
import com.gwsd.open_ptt.fragment.GroupListFragment;
import com.gwsd.open_ptt.fragment.MeFragment;
import com.gwsd.open_ptt.view.AppTopView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends CommBusiActivity {

    AppTopView viewAppTop;
    FrameLayout viewFrameLayout;
    TabLayout viewTablayout;

    RotateAnimation rotateAnimation;

    List<BaseFragment> fragmentList;

    public static void startAct(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void processLogin(LoginEventBean bean) {
        super.processLogin(bean);
        log("login:"+bean.getLoginResult());
        if (bean.getLoginResult() == 0) {
            //showToast(R.string.hint_login_succeed);
            viewAppTop.setTopRightImgGone();
        } else if (bean.getLoginResult() < 0) {
            showToast(R.string.hint_login_failure);
        }
    }

    @Override
    protected void processOffline(OfflineEventBean bean) {
        super.processOffline(bean);
        log("offline:"+bean.getCode());
        if (bean.getCode() == OfflineEventBean.OFFLINE_REASON_KICKOUT_CODE) {
            showToast(R.string.hint_loging_otherterminals);
        } else if (bean.getCode() == OfflineEventBean.OFFLINE_REASON_ERROR_CODE) {
            showToast(R.string.hint_network_err);
        }
        viewAppTop.setTopRightImgVisible();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        viewAppTop = findViewById(R.id.viewTop);
        viewAppTop.setTopRightImg(R.mipmap.ic_top_logining_day);
        viewFrameLayout = findViewById(R.id.viewFrameLayout);
        viewTablayout = findViewById(R.id.viewTablayout);

        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000); // 设置动画持续时间，单位毫秒
        rotateAnimation.setRepeatCount(Animation.INFINITE); // 设置动画重复次数，INFINITE 表示无限重复
        rotateAnimation.setInterpolator(getApplicationContext(), android.R.interpolator.linear); // 设置动画插值器
        viewAppTop.getViewTopRightImg().startAnimation(rotateAnimation);
        viewAppTop.setTopRightImgGone();
    }

    @Override
    protected void initEvent() {
        onTabLayouSelectedListener();
        fragmentList=new ArrayList<>();
        initTab(getSupportFragmentManager(), fragmentList,this,viewTablayout);
        viewTablayout.getTabAt(0).select();
    }

    private void initTab(FragmentManager fragmentManager, List<BaseFragment> fragmentList, Context context, TabLayout viewTablayout) {

        TabLayout tabLayout = viewTablayout;
        List<String> fragmentTitleList = new ArrayList<>();
        List<String> fragmentFlagList = new ArrayList<>();
        List<Integer> titleImgsList = new ArrayList<>();

        fragmentList.add(ChatListFragment.build());
        fragmentTitleList.add(getString(R.string.main_tab_chat));
        fragmentFlagList.add("ChatListFragment");
        titleImgsList.add(R.drawable.main_tab_chat_day);

        fragmentList.add(GroupListFragment.build());
        fragmentTitleList.add(getString(R.string.main_tab_grp));
        fragmentFlagList.add("GroupFragment");
        titleImgsList.add(R.drawable.main_tab_grp_day);

        fragmentList.add(MeFragment.build());
        fragmentTitleList.add(getString(R.string.main_tab_my));
        fragmentFlagList.add("MeFragment");
        titleImgsList.add(R.drawable.main_tab_me_day);

        for (int i = 0; i < fragmentTitleList.size(); i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(context, i, fragmentTitleList, titleImgsList));
            }
        }
    }

    private View getTabView(Context context, int position, List<String> fragmentTitleList, List<Integer> titleImgsList) {
        View view;

        view = LayoutInflater.from(context).inflate(R.layout.view_main_tab, null);
        TextView textview = (TextView) view.findViewById(R.id.tab_tv);
        textview.setTextAppearance(context, R.style.mainRadioBtn);
        ImageView imageview = (ImageView) view.findViewById(R.id.tab_imgview);
        View view_msg_red = view.findViewById(R.id.view_msg_red);
        view_msg_red.setVisibility(View.GONE);
        textview.setText(fragmentTitleList.get(position));
        imageview.setImageResource(titleImgsList.get(position).intValue());
        return view;
    }

    private void onTabLayouSelectedListener(){
        viewTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    viewAppTop.setVisibility(View.VISIBLE);
                    viewAppTop.setTopTitle(R.string.title_ChatActivity);
                } else if (position == 1) {
                    viewAppTop.setVisibility(View.VISIBLE);
                    viewAppTop.setTopTitle(R.string.title_GroupFragment);
                } else {
                    viewAppTop.setVisibility(View.GONE);
                }
                BaseFragment baseFragment = fragmentList.get(position);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                if (!baseFragment.isAdded()) {
                    fragmentTransaction.add(R.id.viewFrameLayout, baseFragment);
                }
                fragmentTransaction.show(baseFragment);
                for (BaseFragment fragment : fragmentList) {
                    if (fragment != baseFragment) {
                        fragmentTransaction.hide(fragment);
                    }
                }
                fragmentTransaction.commitAllowingStateLoss();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateNewMsgFlag(int newMsgnum) {
        TabLayout.Tab tab = viewTablayout.getTabAt(0);
        View view_msg_red = tab.getCustomView().findViewById(R.id.view_msg_red);
        if (newMsgnum > 0) {
            view_msg_red.setVisibility(View.VISIBLE);
        } else {
            view_msg_red.setVisibility(View.GONE);
        }
    }

}
