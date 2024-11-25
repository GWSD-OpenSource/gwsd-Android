package com.gwsd.open_ptt.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseChatAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    final  long CHATTING_TIME_SPACE=2*60;

    protected   final int FACTOR = 9999;

    protected final int TYPE_DATE_TIME = -1;
    protected final int TYPE_TO_Txt= GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT;
    protected final int TYPE_TO_Img=GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_PHOTO;
    protected final int TYPE_TO_Video=GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO;
    protected final int TYPE_TO_Voice=GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VOICE;
    protected final int TYPE_TO_VoiceCall=0;
    protected final int TYPE_TO_VideoCall=1;

    protected final int TYPE_From_Txt=TYPE_TO_Txt+FACTOR;
    protected final int TYPE_From_Img=TYPE_TO_Img+FACTOR;
    protected final int TYPE_From_Video=TYPE_TO_Video+FACTOR;
    protected final int TYPE_From_Voice=TYPE_TO_Voice+FACTOR;
    protected final int TYPE_From_VoiceCall=TYPE_TO_VoiceCall+FACTOR;
    protected final int TYPE_From_VideoCall=TYPE_TO_VideoCall+FACTOR;

    protected List<Object> mData=new ArrayList<>();
    String userId;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public int getMessaeCount() {
        int count = 0;
        for (Object object : mData){
            if (object instanceof MsgContentPojo){
                count++;
            }
        }
        return count;
    }

    @Override
    public void onBindViewHolder(@NonNull T viewHolder, int position) {

        Object item = mData.get(position);
        if(item instanceof Long){
            onBindDateTimeViewHolder(position,viewHolder, (Long) item);
            return;
        }
        onBindMessageViewHolder(position,viewHolder, (MsgContentPojo) item);
    }

    protected abstract void onBindDateTimeViewHolder(int position,T viewHolder, long time);

    protected abstract void onBindMessageViewHolder(int position,T viewHolder, MsgContentPojo message);


    public void addMessage(MsgContentPojo message) {
        //如果是第一个消息，在消息上面显示时间
        if (mData.isEmpty()) {
            mData.add(message.getTime());
            mData.add(message);
            notifyItemRangeInserted(0,2);
            return;
        }
        long lastTime =((MsgContentPojo) mData.get(mData.size()-1)).getTime();
        long currTime =message.getTime();
        //如果最新消息时间比上一个消息间隔2分钟以上，则在最新消息上显示时间
        if(currTime - lastTime >= CHATTING_TIME_SPACE){
            mData.add(currTime);
            mData.add(message);
            notifyItemRangeInserted(mData.size()-2,2);
        }else {
            mData.add(message);
            notifyItemInserted(mData.size() - 1);
        }
    }
    public void clearMessage(){
        this.mData.clear();
        notifyDataSetChanged();
    }
    public void addAllMessage(List<MsgContentPojo> list) {
        if(list.size()>2) {
            Collections.sort(list, new MsgContentBeanComp());
        }
        List<Object> subList = new ArrayList<>();
        for (MsgContentPojo message :list){
            //上一页的消息第一条显示时间
            if (subList.isEmpty()){
                subList.add(message.getTime());
                subList.add(message);
                continue;
            }
            long lastTime =((MsgContentPojo) subList.get(subList.size()-1)).getTime();
            long currTime =message.getTime();
            //如果最新消息时间比上一个消息间隔2分钟以上，则在最新消息上显示时间
            if(currTime - lastTime >= CHATTING_TIME_SPACE){
                subList.add(currTime);
            }
            subList.add(message);
        }
        this.mData.clear();
        this.mData.addAll(subList);
        notifyDataSetChanged();
    }
    public  boolean contains(MsgContentPojo message){
        return mData.contains(message);
    }


}
