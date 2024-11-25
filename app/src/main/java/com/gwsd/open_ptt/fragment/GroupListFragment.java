package com.gwsd.open_ptt.fragment;


import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gwsd.bean.GWGroupListBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.activity.GroupDetailActivity;
import com.gwsd.open_ptt.adapter.CommonHolder;
import com.gwsd.open_ptt.adapter.CommonListAdapter;
import com.gwsd.open_ptt.adapter.recylistener.RecyclerViewListener;
import com.gwsd.open_ptt.manager.GWSDKManager;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends ListFragment {

    private List<GWGroupListBean.GWGroupBean> mData;
    private CommonListAdapter mAdapter;

    public static GroupListFragment build() {
        GroupListFragment groupListFragment = new GroupListFragment();
        return groupListFragment;
    }

    @Override
    protected void initData() {
        mData = new ArrayList<>();
        mAdapter = new CommonListAdapter<GWGroupListBean.GWGroupBean, CommonHolder>(getContext()) {
            @Override
            protected CommonHolder getCommonHolder(@NonNull ViewGroup viewGroup, int type) {
                View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_listdata, viewGroup, false);
                CommonHolder commonHolder = new CommonHolder(contentView);
                commonHolder.setViewAll(R.id.view_item_root, R.id.viewMemberName, R.id.viewMemberState, R.id.viewSelected,R.id.viewHead);
                commonHolder.setOnItemClickView(R.id.view_item_root);
                commonHolder.setOnItemLongClickView(R.id.view_item_root);
                commonHolder.setOnClickView(R.id.viewSelected);
                commonHolder.getView(R.id.viewSelected).setVisibility(View.GONE);
                ImageView imageView= (ImageView) commonHolder.getView(R.id.viewHead);
                imageView.setImageResource(R.mipmap.ic_group_p_blue);
                TextView textView= (TextView) commonHolder.getView(R.id.viewMemberName);
                ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.textColorBlack);
                if (csl != null) {
                    textView.setTextColor(csl);
                }
                return commonHolder;
            }

            @Override
            protected void onBindViewHolderClid(CommonHolder holder, GWGroupListBean.GWGroupBean pojo, int position) {
                TextView textView = (TextView) holder.getView(R.id.viewMemberName);
                ImageView viewHead= (ImageView) holder.getView(R.id.viewHead);
                textView.setText(pojo.getName());
//                if (pojo.getGid() == GWGroupOpt.getInstance().getCurrentGroupGid()){
//                    textView.setSelected(true);
//                    viewHead.setSelected(true);
//                }else{
//                    textView.setSelected(false);
//                    viewHead.setSelected(false);
//                }
            }
        };
        mAdapter.setData(mData);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_GROUP) {
                    runOnUiThread(()->{
                        updateGroupList();
                    });
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }

    @Override
    protected void release() {
        super.release();
        GWSDKManager.getSdkManager().registerPttObserver(null);
    }

    @Override
    protected void setAdapter() {
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnRecyclerViewItemClickListener(new RecyclerViewListener.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
                GWGroupListBean.GWGroupBean selectedGroup = mData.get(position);
                long gid = selectedGroup.getGid();
                String name = selectedGroup.getName();
                int type = selectedGroup.getType();
                GroupDetailActivity.startAct(getContext(),gid,name,type);
            }
        });
        mAdapter.setOnItemLongClick(new RecyclerViewListener.OnRecyclerViewItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
                return false;
            }
        });
    }

    @Override
    protected void onTimer(int ts) {
        log("query timeout="+ts);
        swipeRefreshLayout.setRefreshing(false);
        stopTimer();
    }

    @Override
    protected void loadData() {
        GWSDKManager.getSdkManager().queryGroup();
        startTimer(5000);
    }

    private void updateGroupList(){
        stopTimer();
        swipeRefreshLayout.setRefreshing(false);
        mData.clear();
        mData.addAll(GWSDKManager.getSdkManager().getGroupList());
        if(mData.size()>0){
            viewNoData.setVisibility(View.GONE);
        }else {
            viewNoData.setVisibility(View.GONE);
        }
        mAdapter.changeData(mData);
    }
}
