package com.gwsd.open_ptt.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gwsd.open_ptt.R;

public class ChatInputView extends RelativeLayout implements View.OnClickListener, View.OnTouchListener, TextWatcher, View.OnFocusChangeListener {
    final int TOUCH_Down = 0;
    final int TOUCH_Up = 1;
    final int TOUCH_Cancel = 2;

    public interface OnInputViewLisenter {
        void onSendTxt(String str);

        void onStartVoice();

        void onStopVoice();

        void onCancelVoice();

        void onBtnPhoto();

        void onBtnVideo();

        void onBtnFile();

        void onBtnLoc();

        void onBtnPttCall();

        void onBtnVoiceCall();

        void onBtnVideoCall();
    }

    public ChatInputView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public ChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public ChatInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }


    ImageView viewChangeEtType;
    Button viewSend;
    ImageView viewShowAttach;
    EditText viewEtInput;
    Button viewVoiceRecord;

    Button viewBtnPhoto;
    Button viewBtnVoiceCall;
    Button viewBtnFile;
    Button viewBtnVideo;
    Button viewBtnLocation;
    Button viewBtnVideoCall;
    Button viewBtnPttCall;

    LinearLayout viewAttach;

    ChatInputView.OnInputViewLisenter onInputVewCLisenter;

    boolean showAttach = false;

    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.view_chat_input_layout, this, true);
        viewChangeEtType = findViewById(R.id.viewChangeEtType);
        viewSend = findViewById(R.id.viewSend);
        viewShowAttach = findViewById(R.id.viewShowAttach);
        viewEtInput = findViewById(R.id.viewEtInput);
        viewVoiceRecord = findViewById(R.id.viewVoiceRecord);

        viewBtnPhoto = findViewById(R.id.viewBtnPhoto);
        viewBtnVoiceCall = findViewById(R.id.viewBtnVoiceCall);
        viewBtnFile = findViewById(R.id.viewBtnFile);
        viewBtnVideo = findViewById(R.id.viewBtnVideo);
        viewBtnLocation = findViewById(R.id.viewBtnLoc);
        viewBtnVideoCall = findViewById(R.id.viewBtnVideoCall);
        viewBtnPttCall = findViewById(R.id.viewBtnPttCall);

        viewAttach = findViewById(R.id.viewAttach);

        viewChangeEtType.setOnClickListener(this::onClick);
        viewSend.setOnClickListener(this::onClick);
        viewShowAttach.setOnClickListener(this::onClick);
        viewVoiceRecord.setOnTouchListener(this);
        viewEtInput.addTextChangedListener(this);
        viewEtInput.setOnFocusChangeListener(this);

        viewBtnPhoto.setOnClickListener(this::onClick);
        viewBtnVoiceCall.setOnClickListener(this::onClick);
        viewBtnFile.setOnClickListener(this::onClick);
        viewBtnVideo.setOnClickListener(this::onClick);
        viewBtnLocation.setOnClickListener(this::onClick);
        viewBtnVideoCall.setOnClickListener(this::onClick);
        viewBtnPttCall.setOnClickListener(this::onClick);

        viewChangeEtType.setSelected(false);
        viewChangeEtType.setImageResource(R.mipmap.ic_voice_send);
        viewVoiceRecord.setVisibility(GONE);
        viewEtInput.setVisibility(VISIBLE);
        showAttach = false;
        viewEtInput.setText("");
        viewVoiceRecord.setText(R.string.btn_press_speak);
        changeBtnSndByInpout();
        viewAttach.setVisibility(GONE);
    }

    public void setOnInputVewCLisenter(ChatInputView.OnInputViewLisenter onInputVewCLisenter) {
        this.onInputVewCLisenter = onInputVewCLisenter;
    }

    public void hideBtnFile(){
        if(viewBtnFile!=null){
            viewBtnFile.setVisibility(GONE);
        }
    }

    public void hideAudioVideoBtn() {
        if (viewBtnVideoCall!=null) {
            viewBtnVideoCall.setVisibility(INVISIBLE);
        }
        if (viewBtnVoiceCall!=null) {
            viewBtnVoiceCall.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.viewChangeEtType) {
            if (view.isSelected()) {
                view.setSelected(false);
                viewChangeEtType.setImageResource(R.drawable.selector_input_key_voice);
                viewVoiceRecord.setVisibility(GONE);
                viewEtInput.setVisibility(VISIBLE);
            } else {
                view.setSelected(true);
                viewChangeEtType.setImageResource(R.drawable.selector_input_keyboard);
                viewVoiceRecord.setVisibility(VISIBLE);
                viewEtInput.setVisibility(GONE);
            }
        } else if (view.getId() == R.id.viewSend) {
            String inputStr = viewEtInput.getText().toString().trim();
            if (TextUtils.isEmpty(inputStr)) {
                return;
            }
            if (onInputVewCLisenter != null) onInputVewCLisenter.onSendTxt(inputStr);
            viewEtInput.setText("");
        } else if (view.getId() == R.id.viewShowAttach) {
            if (showAttach) {
                viewAttach.setVisibility(GONE);
            } else {
                viewAttach.setVisibility(VISIBLE);
            }
            showAttach = !showAttach;
        } else if (view.getId() == R.id.viewBtnPhoto) {
            if (onInputVewCLisenter != null) onInputVewCLisenter.onBtnPhoto();
        } else if (view.getId() == R.id.viewBtnVoiceCall) {
            if (onInputVewCLisenter != null) onInputVewCLisenter.onBtnVoiceCall();
        } else if (view.getId() == R.id.viewBtnFile) {
            if (onInputVewCLisenter != null) onInputVewCLisenter.onBtnFile();
        } else if (view.getId() == R.id.viewBtnVideo) {
            if (onInputVewCLisenter != null) onInputVewCLisenter.onBtnVideo();
        } else if (view.getId() == R.id.viewBtnLoc) {
            if (onInputVewCLisenter != null) onInputVewCLisenter.onBtnLoc();
        } else if (view.getId() == R.id.viewBtnVideoCall) {
            if (onInputVewCLisenter != null) onInputVewCLisenter.onBtnVideoCall();
        } else if (view.getId() == R.id.viewBtnPttCall) {
            if (onInputVewCLisenter != null) onInputVewCLisenter.onBtnPttCall();
        }
    }

    protected float downY = 0;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                changeVoiceRecord(TOUCH_Down);
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() - downY < -100 && viewVoiceRecord.isSelected()) {
                    viewVoiceRecord.setSelected(false);
                    changeVoiceRecord(TOUCH_Cancel);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                changeVoiceRecord(TOUCH_Up);
                break;
        }
        return true;
    }

    protected void changeVoiceRecord(int touchType) {
        if (touchType == TOUCH_Down) {
            viewVoiceRecord.setSelected(true);
            viewVoiceRecord.setText(R.string.btn_loosen_end);
            if (onInputVewCLisenter != null) onInputVewCLisenter.onStartVoice();
        } else if (touchType == TOUCH_Cancel) {
            //cancel voice
            viewVoiceRecord.setSelected(false);
            viewVoiceRecord.setText(R.string.btn_press_speak);
            if (onInputVewCLisenter != null) onInputVewCLisenter.onCancelVoice();
        } else if (touchType == TOUCH_Up && viewVoiceRecord.isSelected()) {
            viewVoiceRecord.setSelected(false);
            viewVoiceRecord.setText(R.string.btn_press_speak);
            if (onInputVewCLisenter != null) onInputVewCLisenter.onStopVoice();
        }
    }

    protected void changeBtnSndByInpout() {
        if (viewEtInput.getText().toString().trim().length() == 0) {
            viewSend.setEnabled(false);
            viewSend.setVisibility(GONE);
            viewShowAttach.setVisibility(VISIBLE);
        } else {
            viewSend.setEnabled(true);
            viewSend.setVisibility(VISIBLE);
            viewAttach.setVisibility(GONE);
            showAttach = false;
            viewShowAttach.setVisibility(GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        changeBtnSndByInpout();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            viewAttach.setVisibility(GONE);
            showAttach = false;
        }
    }
}
