package com.gwsd.open_ptt.comm_ui.video_record;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.gwsd.open_ptt.R;
import com.gwsd.open_ptt.activity.BaseActivity;
import com.gwsd.open_ptt.dialog.CancelConfirmDialog;
import com.gwsd.open_ptt.utils.Utils;
import com.gwsd.open_ptt.comm_ui.video_record.contracts.VideoPlayContracts;
import com.gwsd.open_ptt.comm_ui.video_record.contracts.VideoRecordingContracts;
import com.gwsd.open_ptt.comm_ui.video_record.help.VideoRecoidingResultBean;
import com.gwsd.open_ptt.comm_ui.video_record.help.VideoRecordParam1;
import com.gwsd.open_ptt.view.MImageTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 2017/12/15.
 */

public class VideoRecordActivity extends BaseActivity {
    final int FRAG_TYPE_Record=0;
    final int FRAG_TYPE_Play=1;
    final int FRAG_TYPE_None=3;
    final int STATE_None=10;
    final int STATE_Recording=11;
    final int STATE_Playing=12;

    final int SUC_TYPE_Click=20;
    final int SUC_TYPE_ShortcutKeys=21;

    final int STOP_Record_TYPE_None=30;
    final int STOP_Record_TYPE_Norm=31;
    final int STOP_Record_TYPE_ShortcutKeys=32;

    final  int INTENT_CODE_selectVideo_content=201;

    FrameLayout view_video_recording_fragment;

    MImageTextView view_camera_change;
    TextView view_timer;
    View view_control_btn_item;
    ImageView view_album;
    ImageView view_retraction;
    ImageView view_startVideo;
    ImageView view_record_ok;


    VideoRecoidingFragment videoRecoidingFragment;
    VideoPlayFragment videoPalyFragment;
    VideoRecordingContracts.VideoRecordingUI videoRecordingUI;
    VideoPlayContracts.VideoPlayUI videoPalyUI;

    VideoRecordParam1 videoRecordParam1;
    int timeCount=0;
    String curVideoPath;
    int curFragType=FRAG_TYPE_None;
    int curStopRecordType=STOP_Record_TYPE_Norm;
    int curState=STATE_None;


    @Override
    protected int getViewId() {
        return R.layout.activity_record_video;
    }

    @Override
    protected void initData() {
        Bundle bundle=getIntent().getExtras();
        videoRecordParam1 = (VideoRecordParam1) bundle.getSerializable("VideoRecordParam1");
        if(videoRecordParam1.getRecordParam().getMaxTime()==null){
            videoRecordParam1.getRecordParam().setMaxTime(30);
        }
        onCreateInitSuc();
    }

    private void onActDissmis(int resultCode,Intent intent){
        setResult(resultCode,intent);
        release();
        finish();
    }
    @Override
    protected void release(){
        super.release();
        if(videoRecordingUI!=null){
            videoRecordingUI.uiSetOnRecordingLisenter(null);
            videoRecordingUI.uiStopRecording();
            videoRecordingUI=null;
        }
        if(videoPalyUI!=null){
            videoPalyUI.uiAddCallbackPlayTimer(null);
            videoPalyUI.uiStopPlay();
            videoPalyUI=null;
        }
        videoRecoidingFragment=null;
        videoPalyFragment=null;
        videoRecordParam1 =null;
    }

    @Override
    protected void initView() {
        view_video_recording_fragment = findViewById(R.id.view_video_recording_fragment);
        view_camera_change = findViewById(R.id.view_camera_change);
        view_timer = findViewById(R.id.view_timer);
        view_control_btn_item = findViewById(R.id.view_control_btn_item);
        view_album = findViewById(R.id.view_album);
        view_retraction = findViewById(R.id.view_retraction);
        view_startVideo = findViewById(R.id.view_startVideo);
        view_record_ok = findViewById(R.id.view_record_ok);

        prequestPermission();
        view_startVideo.setImageResource(R.drawable.checkbox_select_gtalk_record_video_btn);
        view_record_ok.setImageResource(R.mipmap.ic_gtalk_record_ok);
        videoRecoidingFragment=VideoRecoidingFragment.build();
        videoPalyFragment= VideoPlayFragment.build();
        videoRecordingUI=videoRecoidingFragment;
        videoPalyUI=videoPalyFragment;
        if(videoRecordParam1.getPlayParam()!=null && !TextUtils.isEmpty(videoRecordParam1.getPlayParam().getFilePath())){
            uiUpdateViewByPlay(videoRecordParam1.getPlayParam().getFilePath());
        }else {
            uiUpdateViewByNone();
        }
    }

    @Override
    protected void initEvent() {
        view_album.setOnClickListener(this::onEvenOnClick);
        view_retraction.setOnClickListener(this::onEvenOnClick);
        view_startVideo.setOnClickListener(this::onEvenOnClick);
        view_record_ok.setOnClickListener(this::onEvenOnClick);
        view_record_ok.setOnClickListener(this::onEvenOnClick);
    }

    protected void onCreateInitSuc() {
        if(videoRecordParam1 ==null){
            return;
        }
        if (videoRecordParam1.getPlayParam() == null || TextUtils.isEmpty(videoRecordParam1.getPlayParam().getFilePath())) {
            if (hasActShortcutKeys()) {
                stopOrStartVideoRecord(STOP_Record_TYPE_Norm);
            }
        }
    }
    private boolean hasActShortcutKeys(){
        Integer navToType= videoRecordParam1.getNavToType();
        if(navToType==null){
            return false;
        }
        if (navToType.intValue()== videoRecordParam1.NAVTO_TYPE_ShortcutKeys) {
            return true;
        }
        return false;
    }

    public void onEvenOnClick(View view){

        switch (view.getId()){
            case R.id.view_album:{
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, INTENT_CODE_selectVideo_content);
            }
            break;
            case R.id.view_retraction:
                uiUpdateViewByNone();
                break;
            case R.id.view_startVideo:
                stopOrStartVideoRecord(STOP_Record_TYPE_Norm);
                break;
            case R.id.view_record_ok:
                sendEvenRecordSuc(SUC_TYPE_Click);
                break;
            case R.id.view_camera_change:{
                view_camera_change.startImgViewFlipAnim();
                videoRecordingUI.uiCameraSwitchChange();
            }
            break;
        }
    }

    private void stopOrStartVideoRecord(int stopType){
        setCurStopRecordType(stopType);
        if(videoRecordingUI.uiIsRecording()){
            videoRecordingUI.uiStopRecording();
        }else {
            view_camera_change.setVisibility(View.GONE);
            view_camera_change.setEnabled(false);
            videoRecordingUI.uiStartRecording();
            uiUpdateState(STATE_Recording);
        }
    }
    private void sendEvenRecordSuc(int sucType){
        if(videoPalyUI!=null){
            videoPalyUI.uiStopPlay();
        }
        if(getCurVideoPath()==null){
            log("sendEvenRecordSuc err ,curVideoPath is null");
            onActDissmis(RESULT_CANCELED,null);
        }else {
            Intent intent = new Intent();
            intent.putExtra("filePath", getCurVideoPath());
            intent.putExtra("hasExecuteReport",sucType==SUC_TYPE_ShortcutKeys?1:0);
            onActDissmis(RESULT_OK, intent);
        }
    }
    private void uiUpdateViewByPlay(String filePath){
        setCurVideoPath(filePath);
        stopTimer();
        uiUpdateShowFrag(FRAG_TYPE_Play);
        uiUpdateState(STATE_Playing);
        videoPalyUI.uiPlay(filePath);
    }
    private void uiUpdateViewByNone(){
        setCurVideoPath(null);
        if(videoPalyUI!=null)videoPalyUI.uiStopPlay();
        view_timer.setText("");
        stopTimer();
        uiUpdateShowFrag(FRAG_TYPE_Record);
        uiUpdateState(STATE_None);
    }
    private void uiUpdateShowFrag(int fragType){
        if(getCurFragType()==fragType){
            return;
        }
        setCurFragType(fragType);

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        if(fragType==FRAG_TYPE_Record){
            fragmentTransaction.replace(R.id.view_video_recording_fragment,videoRecoidingFragment,"videoRecoidingFragment");
            if(Utils.hasFrontFacingCamera() && Utils.hasBackFacingCamera()){
                view_camera_change.setVisibility(View.VISIBLE);
                view_camera_change.setEnabled(true);
            }else {
                view_camera_change.setVisibility(View.GONE);
                view_camera_change.setEnabled(false);
            }
            videoRecoidingFragment.uiSetOnRecordingLisenter(new VideoRecordingContracts.OnRecordingLisenter() {
                @Override
                public void onResult(VideoRecoidingResultBean bean) {
                    log("OnRecording==onResult=="+bean.toString());
                    view_timer.setText("");
                    if(bean.code==200){
                        if(hasActShortcutKeys()){
                            setCurVideoPath(bean.filePath);
                            sendEvenRecordSuc(SUC_TYPE_ShortcutKeys);
                        }else {
                            uiUpdateViewByPlay(bean.filePath);
                        }
                    }else if(bean.code==201){
                        startTimer(1000);
                    }else if(bean.code==-1){
                        uiUpdateViewByNone();
                    }
                }
            });
        }else if(fragType==FRAG_TYPE_Play){
            fragmentTransaction.replace(R.id.view_video_recording_fragment,videoPalyFragment,"videoPalyFragment");
            view_camera_change.setVisibility(View.GONE);
            view_camera_change.setEnabled(false);
            videoPalyFragment.uiAddCallbackPlayTimer((progress, currentTime, totalTime) -> runOnUiThread(()->{
                view_timer.setText( Utils.intToTimer(currentTime/1000));
            }));
        }
        fragmentTransaction.commit();
    }
    private void uiUpdateState(int state){
        setCurState(state);
        switch (state){
            case STATE_None:
                view_startVideo.setSelected(false);
                view_album.setVisibility(View.VISIBLE);
                view_startVideo.setVisibility(View.VISIBLE);
                view_retraction.setVisibility(View.GONE);
                view_record_ok.setVisibility(View.GONE);
                view_timer.setText("");
                openNightVision();
                break;
            case STATE_Recording:
                view_startVideo.setSelected(true);
                view_album.setVisibility(View.GONE);
                view_startVideo.setVisibility(View.VISIBLE);
                view_retraction.setVisibility(View.GONE);
                view_record_ok.setVisibility(View.GONE);
                openNightVision();
                break;
            case STATE_Playing:
                view_startVideo.setSelected(true);
                view_album.setVisibility(View.GONE);
                view_startVideo.setVisibility(View.GONE);
                view_retraction.setVisibility(View.VISIBLE);
                view_record_ok.setVisibility(View.VISIBLE);
                closeNightVision();
                break;
        }

    }


    private void closeNightVision(){

    }
    private void openNightVision(){

    }

    @Override
    protected void onTimer(int ts) {
        if(view_timer!=null){
            timeCount++;
            view_timer.setText(Utils.intToTimer((videoRecordParam1.getRecordParam().getMaxTime()-timeCount)));
        }
        if(timeCount>= videoRecordParam1.getRecordParam().getMaxTime()){
            stopOrStartVideoRecord(STOP_Record_TYPE_Norm);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN){
            showExitConfirmDig();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==INTENT_CODE_selectVideo_content){
            Uri uri= data.getData();
            String path = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                //path= PhotoGetPathByUrlUtil.getFileAbsolutePath(this,uri);
            }else {
                // 这里开始的第二部分，获取图片的路径：
                //path= PhotoGetPathByUrlUtil.getFileAbsolutePathDi(this,uri);
            }
            File file=new File(path);
            if(!file.exists()){
                showToast(R.string.hint_nofile);
                return;
            }
            String pathurl=file.getAbsolutePath();
            uiUpdateViewByPlay(pathurl);
        }
    }

    private void prequestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] mPermissions=new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO};
            List<String> requstPer=new ArrayList<>();
            for (String string:mPermissions){
                if (ContextCompat.checkSelfPermission(this,string)!= PackageManager.PERMISSION_GRANTED){
                    showToast(R.string.hint_please_permission);
                    requstPer.add(string);
                }
            }
            if(requstPer.size()>0){
                requestPermissions(requstPer.toArray(new String[requstPer.size()]),100);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            for (int grantR:grantResults){
                if (grantR!= PackageManager.PERMISSION_GRANTED){
                    onActDissmis(RESULT_CANCELED,null);
                    break;
                }
            }
        }
    }



    CancelConfirmDialog cancelConfirmDialog=null;
    /**
     * 提示跳转到系统设备GPS
     */
    private void showExitConfirmDig(){
        if(getCurState()==STATE_None){
            onActDissmis(RESULT_CANCELED,null);
            return;
        }
        if(cancelConfirmDialog==null){
            cancelConfirmDialog=CancelConfirmDialog.build(this);
            String contentStr=null;
            if(getCurState()==STATE_Recording){
                contentStr=getString(R.string.stop);
            }else if(getCurState()==STATE_Playing){
                contentStr=getString(R.string.exit);
            }
            cancelConfirmDialog.setContentText(contentStr,getString(R.string.no),getString(R.string.yes));
            cancelConfirmDialog.setImagLog(R.mipmap.ic_logo_gw_night);
            cancelConfirmDialog.setOnClickTypeListener(new CancelConfirmDialog.OnClickTypeListener() {
                @Override
                public void onClick(View view, int type) {
                    cancelConfirmDialog.dismiss();
                    cancelConfirmDialog=null;
                    if(type==CancelConfirmDialog.OnClickTypeListener.CLICK_TYPE_RIFGHT_BTN){
                        if(getCurState()==STATE_Recording){
                            stopOrStartVideoRecord(STOP_Record_TYPE_Norm);
                        }else if(getCurState()==STATE_Playing){
                            sendEvenRecordSuc(SUC_TYPE_Click);
                        }
                    }
                }
            });
        }
        if(!cancelConfirmDialog.isShowing()){
            cancelConfirmDialog.show();
        }
    }

    public String getCurVideoPath() {
        return curVideoPath;
    }

    public void setCurVideoPath(String curVideoPath) {
        this.curVideoPath = curVideoPath;
    }

    public int getCurFragType() {
        return curFragType;
    }

    public void setCurFragType(int curFragType) {
        this.curFragType = curFragType;
    }

    public int getCurStopRecordType() {
        return curStopRecordType;
    }

    public void setCurStopRecordType(int curStopRecordType) {
        this.curStopRecordType = curStopRecordType;
    }

    public int getCurState() {
        return curState;
    }

    public void setCurState(int curState) {
        this.curState = curState;
    }
}
