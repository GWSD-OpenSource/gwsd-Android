package com.gwsd.open_ptt.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.bean.ChatParam;
import com.gwsd.open_ptt.dao.MsgDaoHelp;
import com.gwsd.open_ptt.manager.GWSDKManager;
import com.gwsd.open_ptt.view.AppTopView;

public class GroupDetailActivity extends BaseActivity{

    AppTopView topView;
    TextView tVGrpName;
    TextView tVGid;
    LinearLayout lLmsg;
    LinearLayout lLHalfDuplex;
    LinearLayout viewGrpNotice;
    LinearLayout viewGrpDelMsg;

    private long gid;
    private String gname;
    private int gtype;

    public static void startAct(Context context,long gid,String name, int type) {
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtra("selectGid", gid);
        intent.putExtra("selectName",name);
        intent.putExtra("selectType", type);
        context.startActivity(intent);
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_group_detail;
    }

    @Override
    protected void initData() {
        gid = getIntent().getLongExtra("selectGid", -1);
        gname = getIntent().getStringExtra("selectName");
        gtype = getIntent().getIntExtra("selectType", 0);
    }

    @Override
    protected void initView() {
        topView = findViewById(R.id.viewTopView);
        tVGrpName = findViewById(R.id.groupName);
        tVGid = findViewById(R.id.groupId);
        lLmsg = findViewById(R.id.sendMsgLL);
        lLHalfDuplex = findViewById(R.id.halfDuplexLL);
        viewGrpNotice = findViewById(R.id.grpAnnouncement);
        viewGrpDelMsg = findViewById(R.id.viewDelGroupMsg);

        topView.setTopTitle(R.string.group_detail);
        topView.setLeftImg(R.drawable.selector_top_back_day);
        topView.setTopRightImg(R.drawable.selector_more_info_day);
        tVGrpName.setText(gname);
        tVGid.setText(String.valueOf(gid));

    }

    private String getUid() {
        String uid = String.valueOf(GWSDKManager.getSdkManager().getUserInfo().getId());
        return uid;
    }

    @Override
    protected void initEvent() {
        topView.setLeftClick(v->{
            finish();
        });
        topView.setRightClick(v->{
            MemberListActivity.startAct(getContext(),gid);
        });
        lLmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatParam chatParam=new ChatParam();
                chatParam.setConvId((int)gid);
                chatParam.setConvName(gname);
                chatParam.setConvType(gtype);
                ChatActivity.startAct(getContext(), chatParam);
            }
        });
        lLHalfDuplex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PttCallActivity.startAct(getContext(),gid, gname, gtype, false);
            }
        });
        viewGrpNotice.setOnClickListener(v->{
            showToast(R.string.hint_exploit_ing);
        });
        viewGrpDelMsg.setOnClickListener(v->{
            MsgDaoHelp.deleteConv(getUid(), (int)gid, gtype);
            showToast(R.string.succeed);
        });

    }

}
