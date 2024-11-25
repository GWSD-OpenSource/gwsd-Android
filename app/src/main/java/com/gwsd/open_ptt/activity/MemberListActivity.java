package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.gwsd.bean.GWMemberInfoBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.adapter.CommonHolder;
import com.gwsd.open_ptt.adapter.CommonListAdapter;
import com.gwsd.open_ptt.adapter.recylistener.RecyclerViewListener;
import com.gwsd.open_ptt.bean.ChatParam;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.view.AppTopView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MemberListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static void startAct(Context context, long gid) {
        Intent intent = new Intent(context, MemberListActivity.class);
        intent.putExtra("queryMemGid", gid);
        context.startActivity(intent);
    }

    AppTopView aTVMemQuery;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    TextView viewNoData;

    private List<GWMemberInfoBean.MemberInfo> mData;
    private CommonListAdapter mAdapter;
    private long gid;

    @Override
    protected int getViewId() {
        return R.layout.activity_members_query;
    }

    @Override
    protected void release() {
        super.release();
        GWSDKManager.getSdkManager().registerPttObserver(null);
    }

    @Override
    protected void initData() {
        gid = getIntent().getLongExtra("queryMemGid", -1);
        mData = new ArrayList<>();
        mAdapter = new CommonListAdapter<GWMemberInfoBean.MemberInfo, CommonHolder>(getContext()) {
            @Override
            protected CommonHolder getCommonHolder(@NonNull ViewGroup viewGroup, int type) {
                View contentView = LayoutInflater.from(getContext()).inflate(R.layout.item_listdata, viewGroup, false);
                CommonHolder commonHolder = new CommonHolder(contentView);
                commonHolder.setViewAll(R.id.view_item_root, R.id.viewMemberName, R.id.viewMemberState, R.id.viewSelected, R.id.viewHead);
                commonHolder.setOnItemClickView(R.id.view_item_root);
                commonHolder.setOnItemLongClickView(R.id.view_item_root);
                commonHolder.setOnClickView(R.id.viewSelected);
                commonHolder.getView(R.id.viewSelected).setVisibility(View.GONE);
                commonHolder.getView(R.id.viewMemberState).setVisibility(View.GONE);
                ImageView imageView = (ImageView) commonHolder.getView(R.id.viewHead);
                imageView.setImageResource(R.mipmap.ic_member_online_blue);
                TextView textView = (TextView) commonHolder.getView(R.id.viewMemberName);
                ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.textColorBlack);
                if (csl != null) {
                    textView.setTextColor(csl);
                }
                return commonHolder;
            }

            @Override
            protected void onBindViewHolderClid(CommonHolder holder, GWMemberInfoBean.MemberInfo pojo, int position) {
                TextView textView = (TextView) holder.getView(R.id.viewMemberName);
                ImageView viewHead = (ImageView) holder.getView(R.id.viewHead);
                textView.setText(pojo.getName());
            }
        };
        mAdapter.setData(mData);

        GWSDKManager.getSdkManager().registerPttObserver(new GWSDKManager.GWSDKPttEngineObserver() {
            @Override
            public void onPttEvent(int event, String data, int data1) {
                if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_MEMBER) {
                    GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
                    if (gwMemberInfoBean.getResult() == 0) {
                        runOnUiThread(()->{
                            updateMembersList();
                        });
                    }
                }
            }

            @Override
            public void onMsgEvent(int var1, String var2) {

            }
        });
    }

    @Override
    protected void initView() {
        aTVMemQuery = findViewById(R.id.viewQueryMemTop);
        aTVMemQuery.setTopTitle(R.string.group_members);
        aTVMemQuery.setLeftClick(v -> {
            finish();
        });
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewNoData = findViewById(R.id.viewNoData);
        viewNoData.setVisibility(View.GONE);
        setAdapter();
        loadData();
    }

    @Override
    protected void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(this);
        GWSDKManager.getSdkManager().queryMember(gid,1);
        aTVMemQuery.setLeftClick(v->{
            finish();
        });

    }

    protected void setAdapter() {
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnRecyclerViewItemClickListener(new RecyclerViewListener.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position) {
                GWMemberInfoBean.MemberInfo selectedMembers = mData.get(position);
                ChatParam chatParam = new ChatParam();
                chatParam.setConvId(selectedMembers.getUid());
                chatParam.setConvName(selectedMembers.getName());
                chatParam.setConvType(GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER);
                ChatActivity.startAct(getContext(), chatParam);
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
    public void onRefresh() {
        loadData();
    }

    @Override
    protected void onTimer(int ts) {
        log("query timeout="+ts);
        swipeRefreshLayout.setRefreshing(false);
        stopTimer();
    }

    protected void loadData() {
        GWSDKManager.getSdkManager().queryMember(gid,1);
        startTimer(5000);
    }

    private void updateMembersList(){
        stopTimer();
        swipeRefreshLayout.setRefreshing(false);
        mData.clear();
        mData.addAll(GWSDKManager.getSdkManager().getMemberList());
        if(mData.size()>0){
            viewNoData.setVisibility(View.GONE);
        }else {
            viewNoData.setVisibility(View.VISIBLE);
        }
        mAdapter.changeData(mData);
    }

}
