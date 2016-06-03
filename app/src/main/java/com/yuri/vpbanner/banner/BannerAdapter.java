package com.yuri.vpbanner.banner;

import android.content.Context;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yuri.vpbanner.R;
import com.yuri.xlog.Log;

import java.util.List;

/**
 * banner adapter
 * Created by Yuri on 2016/4/28.
 */
public class BannerAdapter extends PagerAdapter {

    private List<String> mDataList;
    private Context mContext;

    private SimpleArrayMap<String, ImageView> mViews;

    public BannerAdapter(Context context, List<String> dataList) {
        mContext = context;
        mDataList = dataList;
        mViews = new SimpleArrayMap<>();
    }

    public void setDataList(List<String> dataList) {
        mDataList = dataList;
    }

    public int getItemCount() {
        return mDataList == null? 0 : mDataList.size();
    }

    @Override
    public int getCount() {
        if (mDataList == null || mDataList.size() == 0) {
            return 0;
        }

        if (mDataList.size() == 1) {
            return 1;
        }
        //为了支持无线循环滚动，将count设置为最大值
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        String url = mDataList.get(position % mDataList.size());
        ImageView imageView = mViews.get(url);
        if (imageView == null) {
            imageView = new ImageView(mContext);
            Glide.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imageView);
            mViews.put(url, imageView);
        }
        container.addView(imageView);
        //使用position % mDataList.size()得到实际的位置
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, v);
                }
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public SimpleItemClickListener mClickListener;
    public void setOnItemClickListener(SimpleItemClickListener listener){
        mClickListener = listener;
    }
}
