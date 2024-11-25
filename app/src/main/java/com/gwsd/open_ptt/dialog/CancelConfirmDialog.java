package com.gwsd.open_ptt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gwsd.open_ptt.R;

/**
 * Created by TianHongChun on 2016/6/28.
 * 确认取消
 */
public class CancelConfirmDialog extends Dialog {
    protected View contentView;
    Button view_cancel;
    Button view_confirm;
    View view_line;
    TextView view_hint_content;
    protected ImageView view_hintImg;
    String content="";
    String leftButtonStr="";
    String rightButtonStr="";
    int logResId;
    OnClickTypeListener onClickTypeListener;

    public interface OnClickTypeListener{
        int CLICK_TYPE_LEFT_BTN=1;
        int CLICK_TYPE_RIFGHT_BTN=2;
        void onClick(View view, int type);
    }
    public CancelConfirmDialog(Context context) {
        super(context);
        initDialog(context);
    }
    public CancelConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
        initDialog(context);
    }
    public static CancelConfirmDialog build(Context context){
        CancelConfirmDialog dialog=new CancelConfirmDialog(context, R.style.CustomProgressDialog);
        String conten="确认删除？";
        String leftStr="取消";
        String rightStr="确认";
        dialog.setContentText(conten,leftStr,rightStr);
        return dialog;
    }
    protected void initDialog(Context context){
        contentView= LayoutInflater.from(context).inflate(R.layout.dialog_cancel_confirm,null);
        view_cancel= (Button) contentView.findViewById(R.id.view_cancel);
        view_confirm= (Button) contentView.findViewById(R.id.view_confirm);
        view_hint_content= (TextView) contentView.findViewById(R.id.view_hint_content);
        view_hintImg= (ImageView) contentView.findViewById(R.id.view_hintImg);
        view_line=contentView.findViewById(R.id.view_line);


        view_hint_content.setVisibility(View.VISIBLE);
        view_hint_content.setText(content);
        view_cancel.setText(leftButtonStr);
        view_confirm.setText(rightButtonStr);

        if(logResId!=0)
            view_hintImg.setImageResource(logResId);

        view_confirm.setOnClickListener(myListener);
        view_cancel.setOnClickListener(myListener);
        this.setContentView(contentView);
        this.setCanceledOnTouchOutside(true);
        this.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;
    }


    public void setContentText(String contentText,String leftButtonStr,String rightButtonStr){
        this.content=contentText;
        this.leftButtonStr=leftButtonStr;
        this.rightButtonStr=rightButtonStr;
        if(view_hint_content!=null){
            view_hint_content.setText(contentText);
            view_cancel.setText(leftButtonStr);
            view_confirm.setText(rightButtonStr);
        }
    }
    public void setImagLog(int resId){
        this.logResId=resId;
        if(view_hintImg!=null)
            view_hintImg.setImageResource(resId);
    }
    public TextView getContentHintView(){
        return view_hint_content;
    }
    public void setHideLeftBtn(){
        if(view_confirm!=null
                && view_cancel!=null
                && view_line!=null){
            view_confirm.setBackgroundResource(R.drawable.selector_bottom_left_right_radius_whitebg);
            view_cancel.setVisibility(View.GONE);
            view_line.setVisibility(View.GONE);
        }

    }
    View.OnClickListener myListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(onClickTypeListener!=null){
                int clickType=1;
                int i = v.getId();
                if (i == R.id.view_cancel) {
                    clickType = OnClickTypeListener.CLICK_TYPE_LEFT_BTN;

                } else if (i == R.id.view_confirm) {
                    clickType = OnClickTypeListener.CLICK_TYPE_RIFGHT_BTN;

                }
                onClickTypeListener.onClick(v,clickType);
            }
        }
    };
    public void setOnClickTypeListener(OnClickTypeListener onClickTypeListener) {
        this.onClickTypeListener = onClickTypeListener;
    }
}

