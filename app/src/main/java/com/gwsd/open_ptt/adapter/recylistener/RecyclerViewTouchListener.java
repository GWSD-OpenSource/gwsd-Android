package com.gwsd.open_ptt.adapter.recylistener;

import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Nicky on 2017/9/14.
 */

public class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener {
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
