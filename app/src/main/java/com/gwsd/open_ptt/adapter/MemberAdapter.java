package com.gwsd.open_ptt.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.open_ptt.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberAdapter extends RecyclerView.Adapter <MemberAdapter.ViewHolder>{

    private Map<Integer, String> membersMap = new HashMap<>();
    private int selectedPosition = -1; // 当前选中的位置
    private List<Integer> uidList = new ArrayList<>(); // 用于存储 UID 列表

    public void setMembers(Map<Integer, String> membersMap) {
        this.membersMap = membersMap;
        uidList.clear();
        uidList.addAll(membersMap.keySet());
        Log.d("MemberAdapter", "setMembers size: " + membersMap.size());
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_recyclelist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int uid = uidList.get(position);
        String name = membersMap.get(uid);

        // 设置 CheckBox 状态
        holder.checkBox.setChecked(position == selectedPosition);
        holder.count.setText(String.valueOf(position + 1));
        holder.memName.setText(name);
        holder.memId.setText(String.valueOf(uid));

        // 设置 CheckBox 的点击事件
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPosition = position;
                notifyDataSetChanged();
                Log.d("MemberAdapter", "Selected uid: " + uid);
            } else {
                if (selectedPosition == position) {
                    selectedPosition = -1;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return membersMap.size(); // 返回 map 的大小
    }

    public int getSelectedUid() {
        if (selectedPosition != -1) {
            return uidList.get(selectedPosition);
        }
        return -1;
    }

    public String getSelectedName() {
        if (selectedPosition != -1) {
            int selectedUid = uidList.get(selectedPosition); // 获取选中项的 UID
            return membersMap.get(selectedUid); // 返回对应的名字
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView count;
        TextView memName;
        TextView memId;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            count = itemView.findViewById(R.id.count);
            memName = itemView.findViewById(R.id.memName);
            memId = itemView.findViewById(R.id.memId);
        }
    }
}
