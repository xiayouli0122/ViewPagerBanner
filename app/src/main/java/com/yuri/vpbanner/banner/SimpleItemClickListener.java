package com.yuri.vpbanner.banner;

import android.view.View;

/**
 * item 点击回调 只处理item被点击的事件
 * Created by Yuri on 2016/5/6.
 */
public interface SimpleItemClickListener {
    /***
     *  item 被点击
     * @param position 被点击的位置
     * @param view itemView
     */
    void onItemClick(int position, View view);
}
