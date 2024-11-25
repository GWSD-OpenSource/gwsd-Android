package com.gwsd.open_ptt.comm_ui.image;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.activity.BaseActivity;
import com.gwsd.open_ptt.view.HackyViewPager;

import java.util.ArrayList;

public class ImageActivity extends BaseActivity {

    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;

    ImagePagerAdapter mAdapter;
    ArrayList<String> mData;

    public static void startAct(Context context, ArrayList<String> imgPaths, int index){
        Intent intent=new Intent(context, ImageActivity.class);
        intent.putStringArrayListExtra(ImageActivity.EXTRA_IMAGE_URLS,imgPaths);
        intent.putExtra(ImageActivity.EXTRA_IMAGE_INDEX,index);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_image;
    }

    @Override
    protected void initData() {
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        mData= getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), mData);
    }

    @Override
    protected void initView() {
        mPager = (HackyViewPager) findViewById(R.id.pager);
        indicator = (TextView) findViewById(R.id.indicator);
        mPager.setAdapter(mAdapter);
        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
        indicator.setText(text);
        mPager.setCurrentItem(pagerPosition);
    }

    @Override
    protected void initEvent() {
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }
        });
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ArrayList<String> fileList;
        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }
        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }
        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position);
            return ImageFragment.build(url);
        }
    }
}
