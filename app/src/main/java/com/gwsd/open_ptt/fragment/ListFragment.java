package com.gwsd.open_ptt.fragment;

import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.manager.GWSDKManager;


public abstract class ListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected TextView viewNoData;

    @Override
    protected int getViewId() {
        return R.layout.fragment_datalist;
    }

    protected String getUid() {
        String uid = String.valueOf(GWSDKManager.getSdkManager().getUserInfo().getId());
        return uid;
    }

    @Override
    protected void initView() {
        swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewNoData = (TextView) contentView.findViewById(R.id.viewNoData);
        setAdapter();
        loadData();
    }

    protected abstract void setAdapter();
    protected abstract void loadData();

    @Override
    protected void initEvent() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        loadData();
    }


}
