package com.gwsd.open_ptt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;
import com.gwsd.open_ptt.view.ChatBaseFromMsgView;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2020/11/26 0026.
 */

public class ChatAdapter extends BaseChatAdapter<ChatAdapter.GWChatHodler> {

    public ChatAdapter(String userId) {
        super();
        setUserId(userId);

    }
    @Override
    public int getItemViewType(int position) {
        Object item = mData.get(position);
        if(item instanceof Long){
            return TYPE_DATE_TIME;
        }
        MsgContentPojo message= (MsgContentPojo) item;
        boolean isSelf=message.getSenderId().equals(getUserId());
        int msgType=message.getMsgType().intValue();
        return isSelf?msgType:msgType+FACTOR;
    }

    @NonNull
    @Override
    public GWChatHodler onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        if(type==TYPE_DATE_TIME){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_datatime,viewGroup,false));
        } else if(type==TYPE_TO_Txt){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_to_txt,viewGroup,false));
        }else if(type==TYPE_TO_Img){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_to_photo,viewGroup,false));
        }else if(type==TYPE_TO_Video){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_to_video,viewGroup,false));
        }else if(type==TYPE_TO_Voice){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_to_voice,viewGroup,false));
        }else if(type==TYPE_TO_VoiceCall || type==TYPE_TO_VideoCall){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_to_avcall,viewGroup,false));
        }else if(type==TYPE_From_Txt){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_from_txt,viewGroup,false));
        }else if(type==TYPE_From_Img){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_from_photo,viewGroup,false));
        }else if(type==TYPE_From_Video){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_from_video,viewGroup,false));
        }else if(type==TYPE_From_Voice){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_from_voice,viewGroup,false));
        }else if(type==TYPE_From_VoiceCall || type==TYPE_From_VideoCall){
            return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_from_avcall,viewGroup,false));
        }
        return new GWChatHodler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_record_to_txt,viewGroup,false));
    }
    @Override
    protected void onBindDateTimeViewHolder(int position,GWChatHodler viewHolder, long time) {
        String timeStr = new SimpleDateFormat("yyyy-MM-dd HH-mm").format(time*1000);
        viewHolder.viewDateTime.setText(timeStr);
    }

    @Override
    protected void onBindMessageViewHolder(int position,GWChatHodler viewHolder, MsgContentPojo message) {
        viewHolder.viewName.setText(message.getSenderNm());
        //viewHolder.viewFromMsgView.displayMessageBeforeParam(upFileProMap);

        viewHolder.viewFromMsgView.displayMessage(position,message,getUserId());
    }

    public class GWChatHodler extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView viewName;
        ChatBaseFromMsgView viewFromMsgView;
        TextView viewDateTime;
        public GWChatHodler(@NonNull View itemView) {
            super(itemView);
            viewDateTime=itemView.findViewById(R.id.viewDateTime);
            viewName=itemView.findViewById(R.id.viewName);
            viewFromMsgView=itemView.findViewById(R.id.viewFromMsgView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (viewFromMsgView != null) {
                viewFromMsgView.onClick(v);
            }
        }
    }

}
