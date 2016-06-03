package com.yuri.vpbanner.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.yuri.xlog.Log;

import java.lang.reflect.Field;

/**
 * 利用viewpager banner循环自动滚动实现
 * Created by Yuri on 2016/6/3.
 */
public class BannerViewPager extends ViewPager {

    //banner显示时间，默认3s
    private int mShowTime = 3000;

    //自动播放的方向，默认是left
    private Direction mDirection = Direction.LEFT;

    /**
     * 滚动的时候的指示器
     */
    private FlowIndicator mFlowIndicator;

    private BannerAdapter mAdapter;

    /**
     * 是否自动滚动
     */
    private boolean mIsAuto = false;

    enum Direction {
        LEFT, RIGHT
    }

    public BannerViewPager(Context context) {
        super(context);
        init();
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        try {
            //利用反射修改ViewPager的滚动速率
            Field mField;
            mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(),
                    new AccelerateDecelerateInterpolator());
            scroller.setDuration(1000); // 1000ms
            mField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFlowIndicator(CircleFlowIndicator flowIndicator) {
        mFlowIndicator = flowIndicator;
        mFlowIndicator.setViewPager(this, mAdapter.getItemCount());
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (BannerAdapter) adapter;
    }

    public int getItemCount() {
        return mAdapter == null ? 0 : mAdapter.getItemCount();
    }


    /**
     * 设置每个item显示的时间，单位mills，默认3000ms
     * @param showTime time
     */
    public void setShowTime(int showTime) {
        mShowTime = showTime;
    }

    /**
     * 设置自动播放的方向
     * @param direction {@link Direction#LEFT}, {@link Direction#RIGHT}
     */
    public void setDirection(Direction direction) {
        mDirection = direction;
    }

    public void startAuto() {
        mIsAuto = true;
        start();
    }

    /**开始自动滚动*/
    public void start() {
        if (!mIsAuto) {
            return;
        }

        //防止无限滚动，每次开始滚动之前先停止
        stop();
        //利用postDelayed方法进行自动滚动
        postDelayed(playRunnable, mShowTime);
    }

    private Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            //开始播放
            play();
        }
    };

    /**停止自动滚动*/
    public void stop() {
        removeCallbacks(playRunnable);
    }

    /**
     * 执行播放
     */
    private synchronized void play() {
        if (mAdapter != null) {
            int count = mAdapter.getCount();
            int currentItem = getCurrentItem();
            switch (mDirection) {
                case LEFT:
                    //自动向后滚动
                    currentItem ++;
                    if (currentItem > count) {
                        //滚动到最后一个的时候,从头开始
                        currentItem = 0;
                    }
                    break;
                case RIGHT:
                    currentItem --;
                    if (currentItem <= 0) {
                        currentItem = count;
                    }
                    break;
            }
            setCurrentItem(currentItem);
            //自动播放的重点，每次播放完成后，自动开启一个定时任务
            start();
        }
    }

    /**
     * 在view被加载完成后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //在view加载完成的时候，开始监听viewpager滑动事件
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == SCROLL_STATE_IDLE) {
                    start();
                } else if (state == SCROLL_STATE_DRAGGING) {
                    stop();
                }
            }
        });

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mFlowIndicator != null) {
//            Log.d("l:%d,t:%d,oldl:%d,oldt:%d",l,t,oldl,oldt);
            mFlowIndicator.onScrolled(l,t,oldl,oldt);
        }
    }

    public interface FlowIndicator {
        void setViewPager(BannerViewPager bannerViewPager, int itemCount);

        /**
         * @param h Current horizontal scroll origin.
         * @param v Current vertical scroll origin.
         * @param oldh Previous horizontal scroll origin.
         * @param oldv Previous vertical scroll origin.
         */
        void onScrolled(int h, int v, int oldh, int oldv);
    }
}
