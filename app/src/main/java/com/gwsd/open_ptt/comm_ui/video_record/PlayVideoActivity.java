package com.gwsd.open_ptt.comm_ui.video_record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.activity.BaseActivity;

public class PlayVideoActivity extends BaseActivity {

    FrameLayout viewFrameLayout;

    VideoPlayNormFragment videoPlayNormFragment;

    String videoUrl;
    String thumebUrl;
    public static void navToAct(Context context,String videoUrl,String thumebUrl){
        Intent intent=new Intent(context,PlayVideoActivity.class);
        intent.putExtra("videoUrl",videoUrl);
        intent.putExtra("thumebUrl",thumebUrl);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_playvideo;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                videoUrl = bundle.getString("videoUrl","");
                thumebUrl = bundle.getString("thumebUrl","");
            }
            else
            {
                finish();
            }
        } else {
            finish();
        }
    }

    protected void initView() {
        viewFrameLayout = findViewById(R.id.viewFrameLayout);
        videoPlayNormFragment=VideoPlayNormFragment.build(videoUrl,thumebUrl);

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewFrameLayout,videoPlayNormFragment,"VideoPlayFragment");
        fragmentTransaction.commit();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void release() {
        if(videoPlayNormFragment!=null){
            videoPlayNormFragment.releasePlay();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(videoPlayNormFragment!=null)videoPlayNormFragment.releasePlay();
        }
        return super.onKeyDown(keyCode, event);
    }
}
