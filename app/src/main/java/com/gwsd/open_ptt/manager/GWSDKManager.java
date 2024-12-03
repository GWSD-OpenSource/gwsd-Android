package com.gwsd.open_ptt.manager;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.gwsd.GWPttApi;
import com.gwsd.GWPttEngine;
import com.gwsd.GWVideoEngine;
import com.gwsd.bean.GWChatGroupDetailBean;
import com.gwsd.bean.GWCurrentGroupNotifyBean;
import com.gwsd.bean.GWDuplexBean;
import com.gwsd.bean.GWGroupListBean;
import com.gwsd.bean.GWGroupOperateBean;
import com.gwsd.bean.GWJoinGroupBean;
import com.gwsd.bean.GWKickoutNotifyBean;
import com.gwsd.bean.GWLocationBean;
import com.gwsd.bean.GWLoginResultBean;
import com.gwsd.bean.GWMemberInfoBean;
import com.gwsd.bean.GWMsgBaseBean;
import com.gwsd.bean.GWMsgBean;
import com.gwsd.bean.GWRequestSpeakBean;
import com.gwsd.bean.GWSpeakNotifyBean;
import com.gwsd.bean.GWTempGroupBean;
import com.gwsd.bean.GWTempGroupNotifyBean;
import com.gwsd.bean.GWType;
import com.gwsd.open_ptt.MyApp;
import com.gwsd.open_ptt.activity.AudioCallActivity;
import com.gwsd.open_ptt.activity.PttCallActivity;
import com.gwsd.open_ptt.activity.VideoCallActivity;
import com.gwsd.open_ptt.activity.VideoMeetingActivity;
import com.gwsd.open_ptt.activity.VideoViewActivity;
import com.gwsd.open_ptt.bean.ExitTmpGroupEventBean;
import com.gwsd.open_ptt.bean.GWPttUserInfo;
import com.gwsd.open_ptt.bean.LoginEventBean;
import com.gwsd.open_ptt.bean.NotifiDataBean;
import com.gwsd.open_ptt.bean.OfflineEventBean;
import com.gwsd.open_ptt.config.DeviceConfig;
import com.gwsd.open_ptt.config.ServerAddressConfig;
import com.gwsd.open_ptt.dao.MsgDaoHelp;
import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;
import com.gwsd.open_ptt.dao.pojo.MsgConversationPojo;
import com.gwsd.open_ptt.service.MainService;
import com.gwsd.rtc.view.GWRtcSurfaceVideoRender;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class GWSDKManager implements GWPttApi.GWPttObserver, GWVideoEngine.GWVideoEventHandler, Handler.Callback {

    public interface GWSDKPttEngineObserver {
        void onPttEvent(int var1, String var2, int var3);
        void onMsgEvent(int var1, String var2);
    }

    public interface GWSDKVideoEngineObserver extends GWVideoEngine.GWVideoEventHandler {

    }

    private static GWSDKManager gwsdkManager;
    public static GWSDKManager INSTANCE(Context context) {
        if (gwsdkManager == null) {
            synchronized (GWSDKManager.class) {
                if (gwsdkManager == null) {
                    gwsdkManager = new GWSDKManager(context);
                }
            }
        }
        return gwsdkManager;
    }
    public static GWSDKManager getSdkManager() {
        return gwsdkManager;
    }

    private void log(String msg) {
        Log.i(MyApp.TAG, this.getClass().getSimpleName()+"="+msg);
    }

    private Context context;
    private GWPttEngine gwPttEngine;
    private GWVideoEngine gwVideoEngine;

    private GWSDKPttEngineObserver pttObserver;
    private GWSDKVideoEngineObserver videoObserver;

    private GWPttUserInfo userInfo;
    private String imei;
    private String iccid;
    private boolean autoLogin = true;
    private Map<Long, GWGroupListBean.GWGroupBean> groupMap;
    private List<GWGroupListBean.GWGroupBean> groupBeanList;
    private List<GWMemberInfoBean.MemberInfo> memBeanList;
    private boolean haveStartMsgService = false;

    private Disposable disposable;
    protected HandlerThread handlerThread;
    protected Handler handler;

    private GWSDKManager(Context context) {
        this.context = context;
        gwPttEngine = GWPttEngine.INSTANCE(this.context);
        gwVideoEngine = GWVideoEngine.INSTANCE();
        gwPttEngine.pttInit(this, this, null);
        gwPttEngine.pttConfigServer(0, ServerAddressConfig.PTT_SERVER_ADDRESS, ServerAddressConfig.PTT_SERVER_PORT);
        gwPttEngine.pttConfigServer(1, ServerAddressConfig.MSG_SERVER_ADDRESS, ServerAddressConfig.MSG_SERVER_PORT);
        gwPttEngine.pttConfigServer(2, ServerAddressConfig.DISPATCH_SERVER_ADDRESS, ServerAddressConfig.DISPATCH_SERVER_PORT);
        gwPttEngine.pttConfigServer(3, ServerAddressConfig.VIDEO_SERVER_ADDRESS, ServerAddressConfig.VIDEO_SERVER_PORT);
        log("current sdk version:"+gwPttEngine.pttGetVersion());
        userInfo = new GWPttUserInfo();
        handlerThread = new HandlerThread("SDKManagerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), this);
    }

    private final int LOGIN_AGAIN_MSG = 0;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what)
        {
            case LOGIN_AGAIN_MSG:
                log("login msg try login");
                login(userInfo.getAccount(), userInfo.getPassword(), imei, iccid);
                break;
            default:
                break;
        }
        return false;
    }

    private void sendMessageToSDKHandler(int msg, int timeout) {
        handler.sendEmptyMessageDelayed(msg, timeout);
    }

    private void removeSDKHandlerMessage(int msg) {
        handler.removeMessages(msg);
    }

    public void registerPttObserver(GWSDKPttEngineObserver observer) {
        pttObserver = observer;
    }

    public void registerVideoObserver(GWSDKVideoEngineObserver observer) {
        videoObserver = observer;
    }

    public GWPttUserInfo getUserInfo() {
        return userInfo;
    }

    public String getVersion(){
        return gwPttEngine.pttGetVersion();
    }
    public void checkNet() {
        NetCheckThread netCheckThread = new NetCheckThread(gwPttEngine, new NetCheckThread.OnNetCheckCallback() {
            @Override
            public void onNetCheck(int result) {
                if (result >= 0) {
                    log("netcheck success");
                } else {
                    log("netcheck fail");
                }
            }
        });
    }

    public void login(String account, String password, String imei, String iccid) {
        gwPttEngine.pttLogin(account, password, imei, iccid);
        this.imei = imei;
        this.iccid = iccid;
        userInfo.setAccount(account);
        userInfo.setPassword(password);
        userInfo.setOnline(false);
    }

    public void joinGroup(long gid, int type) {
        gwPttEngine.pttJoinGroup(gid, type);
    }

    public void queryGroup() {
        gwPttEngine.pttQueryGroup();
    }
    public void queryMember(long gid,int type){
        gwPttEngine.pttQueryMember(gid,type);
    }
    public void tempGroup(int[] var1, int var2){
        gwPttEngine.pttTempGroup(var1,var2);
    }
    public void startSpeak(){
        gwPttEngine.pttSpeak(GWType.GW_SPEAK_TYPE.GW_PTT_SPEAK_START,System.currentTimeMillis());
    }
    public void stopSpeak(){
        gwPttEngine.pttSpeak(GWType.GW_SPEAK_TYPE.GW_PTT_SPEAK_END,System.currentTimeMillis());
    }
    public void fullDuplex(int uid,int action){
        gwPttEngine.pttDuplexCall(uid,action);
    }
    public void mutePttSpk(boolean mute) {
        int open = mute?0:1;
        gwPttEngine.pttVoiceToggle(0, open);
    }
    public void mutePttMic(boolean mute) {
        int open = mute?0:1;
        gwPttEngine.pttVoiceToggle(1, open);
    }
    public void loginOut(){
        gwPttEngine.pttLogout();
    }
    public void getWeather(int cellid, int lac, int mode, String mcc, String mnc) {
        gwPttEngine.pttGetWeather(cellid, lac, mode, mcc, mnc);
    }
    public void reportGps(double lat, double lon) {
        GWLocationBean gwLocationBean = new GWLocationBean();
        gwLocationBean.setType(GWType.GW_LOC_TYPE.GW_PTT_LOC_TYPE_GPS);
        GWLocationBean.Location location = new GWLocationBean.Location();
        location.setLat(lat);
        location.setLon(lon);
        gwLocationBean.setLocation(location);
        gwPttEngine.pttReportLocation(gwLocationBean, System.currentTimeMillis());
    }
    public void reportBaseStation(int cellid, int lac_tac, @GWType.GW_NETWORK_TYPE int mode, String mcc, String mnc) {
        GWLocationBean gwLocationBean = new GWLocationBean();
        gwLocationBean.setType(GWType.GW_LOC_TYPE.GW_PTT_LOC_TYPE_CELL);
        GWLocationBean.Location location = new GWLocationBean.Location();
        location.setCellid(cellid);
        location.setLac_or_tac(lac_tac);
        location.setMode(mode);
        location.setMcc(mcc);
        location.setMnc(mnc);
        gwLocationBean.setLocation(location);
        gwPttEngine.pttReportLocation(gwLocationBean, System.currentTimeMillis());
    }
    public boolean hasMsgPermission() {
        if (userInfo.isMessage() || userInfo.isVideo() || userInfo.isSilent()) {
            return true;
        }
        return false;
    }
    public boolean hasVideoPermission() {
        if (userInfo.isVideo() || userInfo.isSilent()) {
            return true;
        }
        return false;
    }
    public boolean hasSilentVideoPermission() {
        if (userInfo.isSilent()) {
            return true;
        }
        return false;
    }
    public boolean hasDuplexCallPermission() {
        if (userInfo.isCall()) {
            return true;
        }
        return false;
    }
    public boolean isOnline() {
        return userInfo.isOnline();
    }
    public void startMsgService(int groups[], int type[], int num) {
        if (!haveStartMsgService) {
            gwPttEngine.pttRegOfflineMsg(groups, type, num, (char)0);
            haveStartMsgService = true;
        }
    }
    public GWMsgBean createMsgBean1(String sendId, String sendNm, int recvtype, String remoteid, String remoteNm, int msgtype) {
        GWMsgBean gwMsgBean = new GWMsgBean();
        gwMsgBean.setFrom(sendId);
        gwMsgBean.setType(msgtype);
        long tm = System.currentTimeMillis();
        gwMsgBean.setTime((int)(tm/1000));
        GWMsgBean.MsgContent msgContent = new GWMsgBean.MsgContent();
        msgContent.setContent("");
        msgContent.setSendId(sendId);
        msgContent.setSendName(sendNm);
        msgContent.setSendUType(GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER);
        msgContent.setReceiveId(remoteid);
        msgContent.setReceiveName(remoteNm);
        msgContent.setReceiveUType(recvtype);
        msgContent.setTime((int)(tm/1000));
        msgContent.setMsgType(msgtype);
        gwMsgBean.setData(msgContent);
        return gwMsgBean;
    }
    public GWMsgBean createMsgBean(int recvtype, int remoteid, String remoteNm, int msgtype) {
        GWMsgBean gwMsgBean = new GWMsgBean();
        gwMsgBean.setFrom(String.valueOf(userInfo.getId()));
        gwMsgBean.setType(msgtype);
        long tm = System.currentTimeMillis();
        gwMsgBean.setTime((int)(tm/1000));
        GWMsgBean.MsgContent msgContent = new GWMsgBean.MsgContent();
        msgContent.setContent("");
        msgContent.setSendId(String.valueOf(userInfo.getId()));
        msgContent.setSendName(userInfo.getName());
        msgContent.setSendUType(GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER);
        msgContent.setReceiveId(String.valueOf(remoteid));
        msgContent.setReceiveName(remoteNm);
        msgContent.setReceiveUType(recvtype);
        msgContent.setTime((int)(tm/1000));
        msgContent.setMsgType(msgtype);
        gwMsgBean.setData(msgContent);
        return gwMsgBean;
    }

    public void sendMsg(GWMsgBean msg) {
        String content = msg.getData().getContent();
        long ts = (long)msg.getTime() * 1000;
        if (msg.getType() != GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT) {
            content = msg.getData().getUrl();
        }
        log(msg.toString());
        gwPttEngine.pttSendMsg(userInfo.getId(), userInfo.getName(), msg.getData().getReceiveUType(), Integer.valueOf(msg.getData().getReceiveId()),
                msg.getData().getReceiveName(), msg.getType(), content, msg.getData().getThumbUrl(), 0, "", (char)0, ts, (char)1, (char)1);
    }
    public String createThumb(String video) {
        String thumb = gwPttEngine.pttCreateThumbForVideo(video);
        log("thumb="+thumb);
        return thumb;
    }
    public List<GWGroupListBean.GWGroupBean> getGroupList() {
        return groupBeanList;
    }

    public List<GWMemberInfoBean.MemberInfo> getMemberList(){
        return memBeanList;
    }

    @Override
    public void onPttEvent(int event, String data, int data1) {
        log("recv ptt event="+event+" data="+data);
        if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGIN) {
            GWLoginResultBean gwLoginResultBean = JSON.parseObject(data, GWLoginResultBean.class);
            if (gwLoginResultBean.getResult() == 0) {
                log(gwLoginResultBean.getName()+" login success");
                userInfo.setName(gwLoginResultBean.getName());
                userInfo.setId(gwLoginResultBean.getUid());
                userInfo.setOnline(true);
                userInfo.setDefaultGid(gwLoginResultBean.getDefaultGid());
                userInfo.setMessage(gwLoginResultBean.isMessage());
                userInfo.setCall(gwLoginResultBean.isCall());
                userInfo.setVideo(gwLoginResultBean.isVideo());
                userInfo.setSilent(gwLoginResultBean.isSilent());
                gwPttEngine.pttHeart(DeviceConfig.getDeviceBattery(), DeviceConfig.getDeviceNetwork(), System.currentTimeMillis());
                startTimer();
                queryGroup();
                //joinGroup(gwLoginResultBean.getDefaultGid(), 0);
            }
            removeSDKHandlerMessage(LOGIN_AGAIN_MSG);
            EventBus.getDefault().post(new LoginEventBean(gwLoginResultBean.getResult()));
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_GROUP) {
            GWGroupListBean gwGroupListBean = JSON.parseObject(data, GWGroupListBean.class);
            if (gwGroupListBean.getResult() == 0) {
                List<GWGroupListBean.GWGroupBean> groups = gwGroupListBean.getGroups();
                if (groupMap == null) {
                    groupMap = new HashMap<>();
                }
                if (groupBeanList == null) {
                    groupBeanList = new ArrayList<>();
                }
                groupMap.clear();
                groupBeanList.clear();
                groupBeanList.addAll(groups);
                for (GWGroupListBean.GWGroupBean gwGroupBean : groupBeanList) {
                    if (gwGroupBean.getType() != GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_SELFGROUP) {
                        gwGroupBean.setType(GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_GROUP);
                    }
                }
                int[] msg_groups = new int[groups.size()];
                int[] msg_groups_type = new int[groups.size()];
                int i = 0;
                long joingroupid = 0;
                int joingrouptype = 0;
                for (GWGroupListBean.GWGroupBean group : groups) {
                    if (group.getGid() == userInfo.getDefaultGid()) {
                        joingroupid = group.getGid();
                        joingrouptype = group.getType();
                    }
                    groupMap.put(group.getGid(), group);
                    msg_groups[i] = (int)group.getGid();
                    msg_groups_type[i] = group.getType();
                    i++;
                }
                if (userInfo.isMessage() || userInfo.isVideo() || userInfo.isSilent()) {
                    startMsgService(msg_groups, msg_groups_type, groups.size());
                }
                if (joingroupid == 0) {
                    joingroupid = groups.get(0).getGid();
                    joingrouptype = groups.get(0).getType();
                }
                if (userInfo.getCurrentGroupGid() == 0) {
                    joinGroup(joingroupid, joingrouptype);
                }
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_JOIN_GROUP) {
            GWJoinGroupBean gwJoinGroupBean = JSON.parseObject(data, GWJoinGroupBean.class);
            if (gwJoinGroupBean.getResult() == 0) {
                userInfo.setCurrentGroupGid(gwJoinGroupBean.getGid());
                userInfo.setCurrentGroupPriority(gwJoinGroupBean.getPriority());
                userInfo.setLastpriority(userInfo.getCurrentGroupPriority());
                userInfo.setCurrentGroupName(groupMap.get(gwJoinGroupBean.getGid()).getName());
                userInfo.setCurrentGroupType(groupMap.get(gwJoinGroupBean.getGid()).getType());
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_MEMBER) {
            GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
            if (gwMemberInfoBean.getResult() == 0){
                List<GWMemberInfoBean.MemberInfo> members = gwMemberInfoBean.getMembers();
                if (memBeanList == null) {
                    memBeanList = new ArrayList<>();
                }
                memBeanList.clear();
                memBeanList.addAll(members);
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_REQUEST_MIC) {
            GWRequestSpeakBean gwRequestSpeakBean = JSON.parseObject(data, GWRequestSpeakBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_ACTIVE) {
            GWTempGroupBean gwTempGroupBean = JSON.parseObject(data, GWTempGroupBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_TMP_GROUP_PASSIVE) {
            GWTempGroupNotifyBean gwTempGroupNotifyBean = JSON.parseObject(data, GWTempGroupNotifyBean.class);
            if (gwTempGroupNotifyBean.getName() != null && gwTempGroupNotifyBean.getName() != "") {
                CallManager.getManager().enterPttTmpGroupCall((canswitch, oldstate, newstate) -> {
                    if (canswitch) {
                        PttCallActivity.startAct(AppManager.getApp(), gwTempGroupNotifyBean.getUid(), gwTempGroupNotifyBean.getName(), GWType.GW_MSG_RECV_TYPE.GW_PTT_MSG_RECV_TYPE_USER ,false);
                    } else {
                        int[] ids = new int[1];
                        ids[0] = 0;
                        tempGroup(ids, 1);
                    }
                });
            } else {
                log("remote releas tmp group");
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_DUPLEX) {
            GWDuplexBean gwDuplexBean = JSON.parseObject(data, GWDuplexBean.class);
            if (gwDuplexBean.getResult() == 0) {
                if (gwDuplexBean.getStatus() == GWType.GW_DUPLEX_STATUS.GW_PTT_DUPLEX_STATUS_INVIET) {
                    if (gwDuplexBean.getUid() != 0) {
                        CallManager.getManager().enterAudioVideoCall(0, (canswitch, oldstate, newstate) -> {
                            if (canswitch) {
                                if (oldstate == CallManager.CALL_STATE_PTT_TMP_GROUP_CALL) {
                                    EventBus.getDefault().post(new ExitTmpGroupEventBean());
                                    Observable.timer(500,TimeUnit.MILLISECONDS)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(aLong -> {
                                                AudioCallActivity.startAct(AppManager.getApp(), gwDuplexBean.getUid(), gwDuplexBean.getName(), false);
                                            });
                                } else {
                                    AudioCallActivity.startAct(AppManager.getApp(), gwDuplexBean.getUid(), gwDuplexBean.getName(), false);
                                }
                            } else {
                                fullDuplex(gwDuplexBean.getUid(), GWType.GW_DUPLEX_TYPE.GW_PTT_DUPLEX_ACTION_HANGUP);
                            }
                        });
                    }
                }
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOGOUT) {
            log("logout");
            offline(OfflineEventBean.OFFLINE_REASON_LOGOUT_CODE, null);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_KICKOUT) {
            GWKickoutNotifyBean gwKickoutNotifyBean = JSON.parseObject(data, GWKickoutNotifyBean.class);
            log("kickout");
            offline(OfflineEventBean.OFFLINE_REASON_KICKOUT_CODE, gwKickoutNotifyBean.getReason());
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_CURRENT_GROUP) {
            GWCurrentGroupNotifyBean gwCurrentGroupNotifyBean = JSON.parseObject(data, GWCurrentGroupNotifyBean.class);
            if (gwCurrentGroupNotifyBean.getResult() == 0) {
                userInfo.setCurrentGroupGid(gwCurrentGroupNotifyBean.getGid());
                userInfo.setCurrentGroupName(gwCurrentGroupNotifyBean.getName());
                userInfo.setCurrentGroupType(0);
                if (gwCurrentGroupNotifyBean.getReason().equals("return")) {
                    userInfo.setCurrentGroupPriority(userInfo.getLastpriority());
                } else {
                    userInfo.setLastpriority(userInfo.getCurrentGroupPriority());
                    userInfo.setCurrentGroupPriority(-1);
                }
            }
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_SPEAK) {
            GWSpeakNotifyBean gwSpeakNotifyBean = JSON.parseObject(data, GWSpeakNotifyBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_PLAY_DATA) {
            log("recv pcm data="+data1);
            return;
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_LOSTMIC) {
            log("speak too long time stop record");
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_GROUP_OPERATE) {
            GWGroupOperateBean gwGroupOperateBean = JSON.parseObject(data, GWGroupOperateBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_DISPATCH) {
            GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_FRIEND_OPERATE) {
            GWMemberInfoBean gwMemberInfoBean = JSON.parseObject(data, GWMemberInfoBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_QUERY_CHAT_GRP) {
            GWGroupListBean gwGroupListBean = JSON.parseObject(data, GWGroupListBean.class);
        } else if (event == GWType.GW_PTT_EVENT.GW_PTT_EVENT_CHAT_GRP_DETAIL) {
            GWChatGroupDetailBean gwChatGroupDetailBean = JSON.parseObject(data, GWChatGroupDetailBean.class);
        } else {
            log("error happen");
            offline(OfflineEventBean.OFFLINE_REASON_ERROR_CODE, null);
        }
        if (pttObserver != null) {
            pttObserver.onPttEvent(event, data, data1);
        }
    }

    private void offline(int code, String info) {
        userInfo.setOnline(false);
        stopTimer();
        haveStartMsgService = false;
        EventBus.getDefault().post(new OfflineEventBean(code, info));
        if (code != OfflineEventBean.OFFLINE_REASON_LOGOUT_CODE) {
            if (autoLogin) {
                log("other login kickout login again");
                sendMessageToSDKHandler(LOGIN_AGAIN_MSG, 3000);
            }
        }
    }

    @Override
    public void onMsgEvent(int i, String s) {
        log("recv msg event="+i+" data="+s);
        if (i == GWType.GW_MSG_STATUS.GW_MSG_STATUS_ERROR) {
            haveStartMsgService = false;
        }
        if (i == GWType.GW_MSG_STATUS.GW_MSG_STATUS_DATA) {
            GWMsgBaseBean gwMsgBaseBean = JSON.parseObject(s, GWMsgBaseBean.class);
            if (gwMsgBaseBean.getType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_TEXT
                || gwMsgBaseBean.getType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_PHOTO
                || gwMsgBaseBean.getType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VOICE
                || gwMsgBaseBean.getType() == GWType.GW_MSG_TYPE.GW_PTT_MSG_TYPE_VIDEO) {
                log("recv msg data");
                if (gwMsgBaseBean.getFrom().equals(String.valueOf(userInfo.getId()))) {
                    log("recv self msg data drop it");
                } else {
                    GWMsgBean gwMsgBean = JSON.parseObject(s, GWMsgBean.class);
                    MsgContentPojo msgContentPojo = MsgDaoHelp.saveMsgContent(String.valueOf(userInfo.getId()), gwMsgBean);
                    MsgConversationPojo msgConversationPojo = MsgDaoHelp.saveOrUpdateConv(msgContentPojo,false);
                    EventBus.getDefault().post(msgContentPojo);
                    EventBus.getDefault().post(msgConversationPojo);
                    NotifiDataBean notifiDataBean = new NotifiDataBean();
                    notifiDataBean.setSendNm(msgContentPojo.getSenderNm());
                    notifiDataBean.setRecvNm(msgContentPojo.getRecvNm());
                    notifiDataBean.setRecvId(msgContentPojo.getConvId());
                    notifiDataBean.setMsgType(msgContentPojo.getMsgType());
                    notifiDataBean.setContent(msgContentPojo.getContent());
                    notifiDataBean.setRecvType(msgContentPojo.getRecvType());
                    MainService.startServerWithData(AppManager.getApp(), notifiDataBean);
                }
            }
        }
        if (pttObserver != null) {
            pttObserver.onMsgEvent(i, s);
        }
    }

    public void pullVideo(String remoteid, boolean silent, boolean record, GWVideoEngine.GWVideoPriority priority, GWVideoEngine.GWVideoResolution resolution) {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoPull(String.valueOf(userInfo.getId()), userInfo.getName(), remoteid, silent, record, priority, resolution);
    }

    public void acceptPullVideo(int cameraNum, boolean virtualCamera) {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoAcceptPull(userInfo.getAccount(), String.valueOf(userInfo.getId()), userInfo.getName(), cameraNum, virtualCamera);
    }

    public void callVideo(String remoteid, boolean record, GWVideoEngine.GWVideoResolution resolution) {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoCall(userInfo.getAccount(), String.valueOf(userInfo.getId()), userInfo.getName(), remoteid, record, resolution);
    }

    public void acceptCallVideo() {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoAcceptCall(userInfo.getAccount(), String.valueOf(userInfo.getId()), userInfo.getName());
    }

    public void hangupVideo(String remoteid) {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoHangup(String.valueOf(userInfo.getId()), userInfo.getName(), remoteid);
    }

    public void joinVideoMeeting() {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoJoinMeeting(userInfo.getAccount(), String.valueOf(userInfo.getId()), userInfo.getName());
    }

    public void rejectVideoMeeting(String creater, String reason) {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoRejectMeeting(String.valueOf(userInfo.getId()), userInfo.getName(), creater, reason);
    }

    public void leaveVideoMeeting() {
        if (!userInfo.isOnline()) {
            log("user not login!!!");
            return;
        }
        gwVideoEngine.videoLeaveMeeting();
    }

    public void attachLocalVideoView(GWRtcSurfaceVideoRender render) {
        gwVideoEngine.videoAttachLocalView(render);
    }

    public void attachRemoteVideoView(GWRtcSurfaceVideoRender render, long userid) {
        gwVideoEngine.videoAttachRemoteView(render, userid);
    }

    public void clearVideoView(GWRtcSurfaceVideoRender render) {
        gwVideoEngine.videoClearView(render);
    }

    public void muteMic(boolean mute) {
        gwVideoEngine.muteMic(mute);
    }

    public void muteSpk(boolean mute) {
        gwVideoEngine.muteSpeaker(mute);
    }

    public void switchCamera() {
        gwVideoEngine.switchCamera();
    }

    public void updateBitrate(int kbps) {
        gwVideoEngine.updateBitrate(kbps);
    }

    @Override
    public void onVideoEvent(String s) {
        // not process
    }

    @Override
    public void onVideoPull(String remoteid, String remotenm, int remotetype, boolean silent) {
        if (videoObserver != null) {
            videoObserver.onVideoPull(remoteid, remotenm, remotetype, silent);
        }
        VideoViewActivity.startAct(AppManager.getApp(), remoteid, remotenm, false, false);
    }

    @Override
    public void onVideoCall(String s, String s1) {
        if (videoObserver != null) {
            videoObserver.onVideoCall(s, s1);
        }
        CallManager.getManager().enterAudioVideoCall(1, (canswitch, oldstate, newstate) -> {
            if (canswitch) {
                if (oldstate == CallManager.CALL_STATE_PTT_TMP_GROUP_CALL) {
                    EventBus.getDefault().post(new ExitTmpGroupEventBean());
                    Observable.timer(500,TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                VideoCallActivity.startAct(AppManager.getApp(), s, s1, false, false);
                            });
                } else {
                    VideoCallActivity.startAct(AppManager.getApp(), s, s1, false, false);
                }
            } else {
                hangupVideo(s);
            }
        });

    }

    @Override
    public void onVideoMeetingInvite(String s, String s1) {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingInvite(s, s1);
        }
        VideoMeetingActivity.navToAct(AppManager.getApp(), s, s1);
    }

    @Override
    public void onVideoMeetingCancel() {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingCancel();
        }
    }

    @Override
    public void onVideoMeetingSpeak() {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingSpeak();
        }
    }

    @Override
    public void onVideoMeetingMute() {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingMute();
        }
    }

    @Override
    public void onVideoMeetingUserJoin(long l, String s, String s1, boolean b) {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingUserJoin(l, s, s1, b);
        }
    }

    @Override
    public void onVideoMeetingSelfJoin() {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingSelfJoin();
        }
    }

    @Override
    public void onVideoMeetingUserLeave(long l) {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingUserLeave(l);
        }
    }

    @Override
    public void onVideoMeetingKickout() {
        if (videoObserver != null) {
            videoObserver.onVideoMeetingKickout();
        }
    }

    @Override
    public void onLocalStreamReady() {
        if (videoObserver != null) {
            videoObserver.onLocalStreamReady();
        }
    }

    @Override
    public void onRemoteStreamReady(boolean b, long l) {
        if (videoObserver != null) {
            videoObserver.onRemoteStreamReady(b, l);
        }
    }

    @Override
    public void onRemoteStreamRemove() {
        if (videoObserver != null) {
            videoObserver.onRemoteStreamRemove();
        }
    }

    @Override
    public void onLocalStreamRemove() {
        if (videoObserver != null) {
            videoObserver.onLocalStreamRemove();
        }
    }

    @Override
    public void onVideoData(byte[] bytes, int i, int i1, int i2, int i3) {
        if (videoObserver != null) {
            videoObserver.onVideoData(bytes, i, i1, i2, i3);
        }
    }

    @Override
    public void onHangup(String s) {
        if (videoObserver != null) {
            videoObserver.onHangup(s);
        }
    }

    @Override
    public void onError(int i, String s) {
        if (videoObserver != null) {
            videoObserver.onError(i, s);
        }
    }

    private void startTimer() {
        disposable = Observable.interval(50, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //Log.d(TAG, "send heart");
                    gwPttEngine.pttHeart(DeviceConfig.getDeviceBattery(), DeviceConfig.getDeviceNetwork(), System.currentTimeMillis());
                });
    }

    private void stopTimer() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    private static class NetCheckThread extends Thread {

        public interface OnNetCheckCallback{
            public void onNetCheck(int result);
        }

        private GWPttEngine gwPttEngine;
        private OnNetCheckCallback onNetCheckCallback;
        public NetCheckThread(GWPttEngine engine, OnNetCheckCallback callback) {
            gwPttEngine = engine;
            onNetCheckCallback = callback;
        }

        @Override
        public void run() {
            super.run();
            int ret;
            ret = gwPttEngine.pttNetCheck(0,"43.250.33.13", 23003);
            if (ret < 0) {
                onNetCheckCallback.onNetCheck(-1);
                return;
            }
            ret = gwPttEngine.pttNetCheck(1,"43.250.33.13", 51883);
            if (ret < 0) {
                onNetCheckCallback.onNetCheck(-2);
                return;
            }
            ret = gwPttEngine.pttNetCheck(2,"43.250.33.13", 50001);
            if (ret < 0) {
                onNetCheckCallback.onNetCheck(-3);
                return;
            }
            onNetCheckCallback.onNetCheck(0);
        }
    }

}
