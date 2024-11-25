package com.gwsd.open_ptt.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gwsd.open_ptt.MyApp;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment {

    protected View contentView;
    private boolean hasReleaseSuc=false;

    protected void log(String msg) {
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        hasReleaseSuc=false;
        if(contentView==null){
            contentView=LayoutInflater.from(getContext()).inflate(getViewId(),container,false);
            initData();
            initView();
            initEvent();
        }
        return contentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    protected abstract int getViewId();

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initEvent();

    protected void release() {
        stopTimer();
    }

    protected void finishAct(){
        if(getActivity()!=null){
            getActivity().finish();
        }
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

    protected void runOnUiThread(Runnable runnable){
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> runnable.run());
    }

    protected void showToast(int id) {
        showToast(getString(id));
    }

    protected void showToast(String msg){
        if(Looper.myLooper()==Looper.getMainLooper()){
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        }else {
            runOnUiThread(()->  Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());
        }
    }
}
