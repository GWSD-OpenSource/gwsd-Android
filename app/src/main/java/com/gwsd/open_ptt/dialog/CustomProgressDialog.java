package com.gwsd.open_ptt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwsd.open_ptt.GlideApp;
import com.gwsd.open_ptt.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * Created by Nicky on 2017/6/29.
 */
public class CustomProgressDialog extends Dialog {

    View contentView;

    Context context;
    long duration=-1;
    public CustomProgressDialog(Context context) {
        super(context);
    }

    private void log(String message){

    }
    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        intiView(context);
    }
    public static CustomProgressDialog build(Context context) {
        CustomProgressDialog  customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
        return customProgressDialog;
    }

    private void intiView(Context context){
        this.context=context;
        contentView= LayoutInflater.from(context).inflate(R.layout.dialog_customprogress,null);
        setContentView(contentView);
        getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;
        setCanceledOnTouchOutside(false);
        ImageView imageView= (ImageView) findViewById(R.id.viewImg);

        TextView viewMessage= (TextView) findViewById(R.id.viewMessage);
        if(!TextUtils.isEmpty(message)){
            viewMessage.setText(message);
        }

        GlideApp.with(context)
                .asGif()
                .centerInside()
                .load(R.drawable.loading)
                .into(imageView);
    }
    String message;
    public CustomProgressDialog setMessage(int resId) {
        String message= context.getResources().getString(resId);
        return setMessage(message);
    }
    public CustomProgressDialog setMessage(String strMessage) {
        this.message=strMessage;
        if(contentView!=null){
            TextView tvMsg = (TextView) contentView.findViewById(R.id.viewMessage);
            if (tvMsg != null) {
                tvMsg.setText(strMessage);
            }
        }
        return this;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void show(long duration) {
        setDuration(duration);
        show();
    }
    @Override
    public void show() {
        super.show();
        checkAutoDismiss();
    }
    @Override
    public void dismiss() {
        super.dismiss();
        release();
    }
    private void release(){
        context=null;
        contentView=null;
        if(disposableLoadDig!=null && !disposableLoadDig.isDisposed()){
            disposableLoadDig.dispose();
        }
    }

    private Disposable disposableLoadDig;
    public void checkAutoDismiss(){
        if(duration>1000){
            disposableLoadDig=Observable.timer(duration,TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        dismiss();
                    });
        }
    }
}
