package com.gwsd.open_ptt.comm_ui.image;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.gwsd.open_ptt.GlideApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.fragment.BaseFragment;

public class ImageFragment extends BaseFragment {

    private String mImageUrl;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;

    public static ImageFragment build(String imageUrl) {
        final ImageFragment f = new ImageFragment();
        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    protected int getViewId() {
        return R.layout.fragment_image;
    }

    @Override
    protected void initData() {
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    protected void initView() {
        mImageView = (ImageView) contentView.findViewById(R.id.image);
        progressBar = (ProgressBar) contentView.findViewById(R.id.loading);
        mAttacher = new PhotoViewAttacher(mImageView);
    }

    @Override
    protected void initEvent() {
        mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GlideApp.with(this).load(mImageUrl).into(mImageView);
    }

}
