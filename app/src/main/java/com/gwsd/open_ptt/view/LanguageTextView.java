package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import java.util.regex.Pattern;

/**
 * Created by Nicky on 2018/12/6.
 */

public class LanguageTextView extends androidx.appcompat.widget.AppCompatTextView implements LanguageView {
    private int textId ;//文字id
    private int hintId ;//hint的id
    private int arrResId,arrResIndex;

    public LanguageTextView(Context context) {
        super(context);
        init(context, null);
    }

    public LanguageTextView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext, paramAttributeSet);
    }

    public LanguageTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }

    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 初始化获取xml的资源id
     * @param context
     * @param attributeSet
     */
    private void init (Context context,AttributeSet attributeSet) {
        if (attributeSet!=null) {
            String textValue = attributeSet.getAttributeValue(ANDROIDXML, "text");
            if (!(textValue==null || textValue.length()<2)) {
                //如果是 android:text="@string/testText"
                //textValue会长这样 @156878785,去掉@号就是资源id
                String str=textValue.substring(1,textValue.length()).trim();
                if(isInteger(str)){
                    textId = Integer.parseInt(str);
                }
            }

            String hintValue = attributeSet.getAttributeValue(ANDROIDXML, "hint");
            if (!(hintValue==null || hintValue.length()<2)) {
                String str=hintValue.substring(1,hintValue.length()).trim();
                if(isInteger(str)){
                    textId = Integer.parseInt(str);
                }
            }
        }
    }

    @Override
    public void setTextById (@StringRes int strId) {
        this.textId = strId;
        setText(strId);
    }

    @Override
    public void setTextWithString(String text) {
        this.textId = 0;
        setText(text);
    }
    @Override
    public void setTextByArrayAndIndex (@ArrayRes int arrId, @StringRes int arrIndex) {
        arrResId = arrId;
        arrResIndex = arrIndex;
        String[] strs = getContext().getResources().getStringArray(arrId);
        setText(strs[arrIndex]);
    }

    @Override
    public void reLoadLanguage () {
        try {
            if (textId>0) {
                setText(textId);
            } else if (arrResId>0) {
                String[] strs = getContext().getResources().getStringArray(arrResId);
                setText(strs[arrResIndex]);
            }

            if (hintId>0) {
                setHint(hintId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
