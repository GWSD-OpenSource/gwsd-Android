package com.gwsd.open_ptt.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class CommonHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    public int position;

    protected HashMap<Integer,WeakReference<View>> viewLists=new HashMap<>();

    CommonContracts.HolderCallAdapter holderCallAdapter;

    int itemClickResId=-1;


    public CommonHolder(@NonNull View itemView) {
        super(itemView);
    }
    protected <T extends View> T findViewById(int resId){
        return itemView.findViewById(resId);
    }
    public void setViewAll(int... resIds){
        for (int resId : resIds) {
            View view=findViewById(resId);
            WeakReference<View> weakReference=new WeakReference<>(view);
            viewLists.put(resId,weakReference);
        }
    }
    public View getView(int resId){
        WeakReference<View> weakReference=viewLists.get(Integer.valueOf(resId));
        if(weakReference==null)return null;
        View view=weakReference.get();
        return view;
    }
    public <V extends View> V getViewSpecific(int resId){
        WeakReference<View> weakReference=viewLists.get(Integer.valueOf(resId));
        if(weakReference==null)return null;
        View view=weakReference.get();
        return (V) view;
    }

    public void setOnItemClickView(int resIds){
        this.itemClickResId=resIds;
        View view=getView(resIds);
        if(view!=null) view.setOnClickListener(this);
    }
    public void setOnClickView(int... resIds){
        for (int resId : resIds) {
            View view=getView(resId);
            if(view!=null)view.setOnClickListener(this);
        }
    }
    public void setOnItemLongClickView(int resIds){
        View view=getView(resIds);
        if(view!=null) view.setOnLongClickListener(this);
    }

    public void setHolderCallAdapter(CommonContracts.HolderCallAdapter holderCallAdapter) {
        this.holderCallAdapter = holderCallAdapter;
    }

    @Override
    public void onClick(View view) {
        int resId=view.getId();
        if(resId==itemClickResId){
            if(holderCallAdapter!=null) holderCallAdapter.holderCallAdapterOnItemClick(this,view);
        }else {
            if(holderCallAdapter!=null) holderCallAdapter.holderCallAdapteronClick(this,view);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(holderCallAdapter!=null){
            return holderCallAdapter.holderCallAdapteronLongClick(this,view);
        }else {
            return false;
        }
    }
}
