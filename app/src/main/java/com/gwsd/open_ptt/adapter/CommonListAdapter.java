package com.gwsd.open_ptt.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.open_ptt.adapter.recylistener.RecyclerViewListener;

import java.util.LinkedList;
import java.util.List;

public abstract class CommonListAdapter<T,H extends CommonHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements CommonContracts.HolderCallAdapter {
    protected LayoutInflater layoutInflater;
    protected RecyclerViewListener.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    protected RecyclerViewListener.OnRecyclerOtherViewClickListener onRecyclerOtherViewClickListener;
    protected RecyclerViewListener.OnRecyclerViewItemLongClickListener onItemLongClick;

    protected List<T> mData;
    protected List<T> selectList;

    public CommonListAdapter(Context context) {
        layoutInflater=LayoutInflater.from(context);
        selectList=new LinkedList<>();
    }

    public CommonListAdapter setData(List<T> data){
        this.mData=data;
        return this;
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public void changeData(){
        int size=mData.size();
        notifyDataSetChanged();
    }
    public List<T> getData(){
        return mData;
    }
    public void changeData(List<T> data){
        this.mData=data;
        notifyDataSetChanged();
    }
    public List< T> getSelectList() {
        return selectList;
    }
    public void clearSelectMap(){
        if(selectList!=null)
            selectList.clear();
        notifyDataSetChanged();
    }
    public void release(){
        this.layoutInflater=null;
        this.onRecyclerOtherViewClickListener=null;
        this.onRecyclerViewItemClickListener=null;
        this.selectList.clear();;
        this.mData.clear();;
    }
    public void setOnRecyclerOtherViewClickListener(RecyclerViewListener.OnRecyclerOtherViewClickListener onRecyclerOtherViewClickListener) {
        this.onRecyclerOtherViewClickListener = onRecyclerOtherViewClickListener;
    }

    public void setOnRecyclerViewItemClickListener(RecyclerViewListener.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void setOnItemLongClick(RecyclerViewListener.OnRecyclerViewItemLongClickListener onItemLongClick) {
        this.onItemLongClick = onItemLongClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        H hodler=getCommonHolder(viewGroup,type);
        hodler.setHolderCallAdapter(this);
        return hodler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof CommonHolder){
            H holder= (H) viewHolder;
            holder.position=position;
            onBindViewHolderClid(holder,mData.get(position),position);
        }
    }

    protected abstract H getCommonHolder(@NonNull ViewGroup viewGroup, int type);
    protected abstract void onBindViewHolderClid(H holder,T pojo,int position);


    @Override
    public void holderCallAdapterOnItemClick(CommonHolder holder, View view) {
        if(onRecyclerViewItemClickListener!=null)onRecyclerViewItemClickListener.onItemClick(holder,view,holder.position);
    }
    @Override
    public void holderCallAdapteronClick(CommonHolder holder, View view) {
        if(onRecyclerOtherViewClickListener!=null)onRecyclerOtherViewClickListener.onOtherViewClick(holder,view,holder.position);
    }

    @Override
    public boolean holderCallAdapteronLongClick(CommonHolder holder, View view) {
        if(onItemLongClick!=null){
            return onItemLongClick.onItemLongClick(holder,view,holder.position);
        }
        return false;
    }
}
