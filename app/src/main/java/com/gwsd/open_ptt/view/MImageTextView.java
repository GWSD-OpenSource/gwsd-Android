package com.gwsd.open_ptt.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import com.gwsd.open_ptt.R;

/**
 * Created by Nicky on 2017/9/25.
 */

public class MImageTextView extends LinearLayout implements View.OnClickListener,View.OnTouchListener{

    Context context;
    View view_item_root;
    ImageView view_imgview;
    TextView view_textview;

    Animation rotate_50_0_180,rotate_50_180_0;

    public MImageTextView(Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        initView(context, attrs,0);
    }
    private void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        this.context=context;
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.MImageTextView);
       int mWidth = typedArray.getDimensionPixelSize(R.styleable.MImageTextView_mImg_width, 0);
        int mHeight = typedArray.getDimensionPixelSize(R.styleable.MImageTextView_mImg_height,0);
        Drawable mDrawable = typedArray.getDrawable(R.styleable.MImageTextView_mImg_src);
        Drawable mDrawableBg= typedArray.getDrawable(R.styleable.MImageTextView_mImg_bg);
        int mTextSize = typedArray.getDimensionPixelSize(R.styleable.MImageTextView_mTextSize,16);
        int mTextColor = typedArray.getColor(R.styleable.MImageTextView_mTextColor,0x000000);
        ColorStateList colorStateList=typedArray.getColorStateList(R.styleable.MImageTextView_mTextColorDrawable);
        String mText = typedArray.getString(R.styleable.MImageTextView_mText);
        typedArray.recycle();

        LayoutInflater.from(context).inflate(R.layout.view_m_image_textview,this);
        view_imgview= (ImageView) findViewById(R.id.view_imgview);
        view_textview= (TextView) findViewById(R.id.view_textview);
        view_item_root=findViewById(R.id.view_item_root);

        view_imgview.setImageDrawable(mDrawable);
        view_imgview.setBackground(mDrawableBg);
        view_imgview.setLayoutParams(new LayoutParams(mWidth,mHeight));
        view_textview.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
        if(colorStateList!=null){
            view_textview.setTextColor(colorStateList);
        }else {
            view_textview.setTextColor(mTextColor);
        }
        view_textview.setText(mText);

        initEven();
        initAnim();
    }

    private void initEven(){
        setClickable(true);
    }

    private void initAnim(){
        rotate_50_0_180= AnimationUtils.loadAnimation(context, R.anim.rotate_50_0_180);
        rotate_50_180_0= AnimationUtils.loadAnimation(context, R.anim.rotate_50_180_0);
        rotate_50_0_180.setFillAfter(true);
        rotate_50_180_0.setFillAfter(true);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    boolean isNorm=false;
    public void startImgViewFlipAnim(){
        if(isNorm){
            isNorm=false;
//            view_imgview.clearAnimation();
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view_imgview, "rotationY", 0,180);
//            animator1.setInterpolator(new FastOutSlowInInterpolator());
//            animator1.setDuration(600);
//            animator1.start();
            if(Build.VERSION.SDK_INT<21){
                view_imgview.animate()
                        .rotationY(0)
                        .setDuration(600)
                        .setInterpolator(new FastOutLinearInInterpolator())
                        .start();
            }

        }else {
            isNorm=true;
//            view_imgview.clearAnimation();
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view_imgview, "rotationY",180,0);
//            animator1.setInterpolator(new FastOutSlowInInterpolator());
//            animator1.setDuration(600);
//            animator1.start();
            if(Build.VERSION.SDK_INT<21){
                view_imgview.animate()
                        .rotationY(180)
                        .setDuration(600)
                        .setInterpolator(new FastOutLinearInInterpolator())
                        .start();
            }
        }

    }
    public void startImgViewRotateAnim(){
        if(isNorm){
            isNorm=false;
            view_imgview.clearAnimation();
            view_imgview.startAnimation(rotate_50_180_0);
        }else {
            isNorm=true;
            view_imgview.clearAnimation();
            view_imgview.startAnimation(rotate_50_0_180);
        }
    }

    public TextView getView_textview() {
        return view_textview;
    }

    public ImageView getView_imgview() {
        return view_imgview;
    }
}
