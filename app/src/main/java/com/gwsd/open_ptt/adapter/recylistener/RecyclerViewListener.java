package com.gwsd.open_ptt.adapter.recylistener;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Nicky on 2017/6/30.
 */

public class RecyclerViewListener {

    /**
     * item点击监听
     */
    public interface OnRecyclerViewItemClickListener{
        /**
         * item点击回调
         * @param viewHolder  item hodler
         * @param view          点击的view
         * @param position   item 位置
         */
        void onItemClick(RecyclerView.ViewHolder viewHolder, View view, int position);
    }
    /**
     * 按钮点击监听
     */
    public interface OnRecyclerOtherViewClickListener{
        /**
         * 按钮点击回调
         * @param viewHolder  item hodler
         * @param view          点击的view
         * @param position   item 位置
         */
        void onOtherViewClick(RecyclerView.ViewHolder viewHolder, View view, int position);
    }
    /**
     *item长按监听
     */
    public interface OnRecyclerViewItemLongClickListener{
        /**
         * item长按击回调
         * @param viewHolder  item hodler
         * @param view          点击的view
         * @param position   item 位置
         */
        boolean onItemLongClick(RecyclerView.ViewHolder viewHolder, View view, int position);
    }
    public interface OnRecyclerViewQianTaoListViewOnItemClickListener{
        void onQianTaoListViewOnItemClick(View view, int parentPosition, int childPosition, Object childObj);
    }

}
