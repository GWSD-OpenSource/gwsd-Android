package com.gwsd.open_ptt.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.dialog.CustomProgressDialog;
import com.gwsd.open_ptt.manager.AppManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {

    protected void log(String msg) {
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Gw_Phone);
        setContentView(getViewId());

        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    protected Context getContext() {
        return this;
    }

    protected abstract int getViewId();

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initEvent();

    protected void release() {
        stopTimer();
        disposable = null;
    }

    protected void onTimer(int ts) {

    }

    Disposable disposable;
    protected void startTimer(int ms) {
        disposable = Observable.interval(ms, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    onTimer(aLong.intValue());
                });
    }

    protected void stopTimer() {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = null;
    }

    protected void showToast(int id) {
        Toast.makeText(this, AppManager.getApp().getString(id), Toast.LENGTH_SHORT).show();
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("pointer")
                .setMessage(message)
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // 关闭对话框
                    }
                })
                .setNegativeButton("cancel", null)
                .show();
    }

    protected void showLoadingDig(){
        showLoadingDig(getString(R.string.please_Waiting));
    }
    protected void showLoadingDig(boolean hasAutoDis){
        showLoadingDig(getString(R.string.please_Waiting),hasAutoDis);
    }
    protected void showLoadingDig(int resId){
        showLoadingDig(getString(resId));
    }
    protected void showLoadingDig(String msg){
        showLoadingDig(msg,true);
    }

    CustomProgressDialog customProgressDialog;
    protected void showLoadingDig(String msg ,boolean hasAutoDis){
        if(customProgressDialog==null){
            customProgressDialog=CustomProgressDialog.build(getContext());
            customProgressDialog.setMessage(msg);
        }else {
            customProgressDialog.setMessage(msg);
        }
        if(customProgressDialog.isShowing()){
            return;
        }
        customProgressDialog.show(hasAutoDis?3000:0);
    }
    protected void updateLoadingDigMsg(String msg){
        if(customProgressDialog!=null && customProgressDialog.isShowing()){
            customProgressDialog.setMessage(msg);
            return;
        }
    }
    protected void dissmissLoadingDig(){
        if(customProgressDialog!=null && customProgressDialog.isShowing()){
            customProgressDialog.dismiss();
            customProgressDialog=null;
        }
    }
}
