package com.gwsd.open_ptt.view;

import android.content.Context;
import android.util.AttributeSet;

public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
    }
    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    boolean isStartMarquee=false;
    public void setStartMarquee(boolean isStartMarquee){
        this.isStartMarquee=isStartMarquee;
    }
    // 焦点
    @Override
    public boolean isFocused() {
        if(isStartMarquee){
            return isStartMarquee;
        }
        return super.isFocused();
    }

}
