package com.gwsd.open_ptt.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.dao.pojo.MsgConversationPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MsgConvAdapter extends RecyclerView.Adapter<MsgConvAdapter.ImConversHodler> {

    public interface OnItemClick<T>{
        void onItemClick(View view, T t, boolean hasLongClick);
    }
    List<MsgConversationPojo> mData;
    LayoutInflater layoutInflater;
    String userId;
    String voiceStr,videoStr,photoStr,locationStr,voiceCallStr,videoCallStr;
    OnItemClick onItemClick;

    private void log(String msg){
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }
    public MsgConvAdapter(Context context, String userId) {
        mData=new ArrayList<>();
        layoutInflater=LayoutInflater.from(context);
        this.userId=userId;
        voiceStr=context.getResources().getString(R.string.chat_imtype_voice);
        videoStr=context.getResources().getString(R.string.chat_imtype_video);
        photoStr=context.getResources().getString(R.string.chat_imtype_img);
        locationStr=context.getResources().getString(R.string.chat_imtype_location);
        voiceCallStr=context.getResources().getString(R.string.chat_type_voice);
        videoCallStr=context.getResources().getString(R.string.chat_type_video);
    }
    public void setData(List<MsgConversationPojo> data){
        this.mData.clear();
        this.mData=data;
        notifyDataSetChanged();
    }
    public void addData(MsgConversationPojo imConversationBean){
        mData.add(imConversationBean);
        notifyDataSetChanged();
    }
    public void removeData(MsgConversationPojo imConversationBean){
        mData.remove(imConversationBean);
        notifyDataSetChanged();
    }
    public void removeData(String conID){
        MsgConversationPojo mDatumFind=null;
        for (MsgConversationPojo mDatum : mData) {
            if(mDatum.getConvId().equals(conID)){
                mDatumFind=mDatum;
                break;
            }
        }
        if(mDatumFind!=null ){
            mData.remove(mDatumFind);
            notifyDataSetChanged();
        }
    }
    public int getMsgCount(){
        return mData.size();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    @NonNull
    @Override
    public ImConversHodler onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view=layoutInflater.inflate(R.layout.item_msg_conversation_layout,parent,false);
        return new ImConversHodler(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ImConversHodler holder, @SuppressLint("RecyclerView") int position) {
        holder.position=position;
        MsgConversationPojo convBean=mData.get(position);
        if(convBean.getMsgUnReadCnt()>0){
            String unReadStr=convBean.getMsgUnReadCnt()>99?"99+":String.valueOf(convBean.getMsgUnReadCnt());
            holder.viewChatMsgRemind.setVisibility(View.VISIBLE);
            holder.viewChatMsgRemind.setText(unReadStr);
        }else {
            holder.viewChatMsgRemind.setVisibility(View.INVISIBLE);
        }
        holder.viewDescName.setText(convBean.getConvNm());
        String timestr = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(convBean.getLastMsgTime()*1000);
        holder.viewLastMsgTimer.setText(timestr);

        if (convBean.getConvType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_GROUP
            ||convBean.getConvType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_SELFGROUP) {
            holder.viewItemLeftGrp.setBackgroundResource(R.color.color_transparent);
            holder.viewIVHead.setImageResource(R.mipmap.ic_group_p_blue);
        } else if (convBean.getConvType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER) {
            holder.viewItemLeftGrp.setBackgroundResource(R.color.color_transparent);
            holder.viewIVHead.setImageResource(R.mipmap.ic_member_online_blue);
        } else if (convBean.getConvType() == GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_DISPATCH) {
            holder.viewItemLeftGrp.setBackgroundResource(R.color.color_transparent);
            holder.viewIVHead.setImageResource(R.mipmap.ic_dispatch_online);
        } else {
            holder.viewItemLeftGrp.setBackgroundResource(R.color.color_transparent);
            holder.viewIVHead.setImageResource(R.mipmap.ic_member_online_blue);
        }

        if(convBean.getLastMsgType()==GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT){
            holder.viewLastMsg.setText(convBean.getLastMsgContent());
        }else if(convBean.getLastMsgType()==GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_PHOTO){
            holder.viewLastMsg.setText(photoStr);
        }else if(convBean.getLastMsgType()==GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VOICE){
            holder.viewLastMsg.setText(voiceStr);
        }else if(convBean.getLastMsgType()==GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO){
            holder.viewLastMsg.setText(videoStr);
        }else if(convBean.getLastMsgType()==0){
            holder.viewLastMsg.setText(voiceCallStr);
        }else if(convBean.getLastMsgType()==1){
            holder.viewLastMsg.setText(videoCallStr);
        }else {
            holder.viewLastMsg.setText("");
        }
    }
    public class ImConversHodler extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        View viewRoot;
        RelativeLayout viewItemLeftGrp;
        ImageView viewIVHead;
        TextView viewChatMsgRemind;
        TextView viewDescName;
        TextView viewLastMsg;
        TextView viewLastMsgTimer;

        int position;
        public ImConversHodler(@NonNull View itemView) {
            super(itemView);
            viewRoot = itemView.findViewById(R.id.viewRoot);
            viewItemLeftGrp = itemView.findViewById(R.id.viewItemLeftGrp);
            viewIVHead = itemView.findViewById(R.id.viewIVHead);
            viewChatMsgRemind = itemView.findViewById(R.id.viewChatMsgRemind);
            viewDescName = itemView.findViewById(R.id.viewDescName);
            viewLastMsg = itemView.findViewById(R.id.viewLastMsg);
            viewLastMsgTimer = itemView.findViewById(R.id.viewLastMsgTimer);
            viewChatMsgRemind.setVisibility(View.INVISIBLE);
            viewRoot.setOnClickListener(this);
            viewRoot.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            viewChatMsgRemind.setVisibility(View.INVISIBLE);
            MsgConversationPojo bean=mData.get(position);
            if(onItemClick!=null)onItemClick.onItemClick(view,bean,false);
        }

        @Override
        public boolean onLongClick(View view) {
            MsgConversationPojo bean=mData.get(position);
            if(onItemClick!=null)onItemClick.onItemClick(view,bean,true);
            return false;
        }
    }

}
