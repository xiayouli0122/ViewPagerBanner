package com.yuri.vpbanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.yuri.vpbanner.banner.BannerAdapter;
import com.yuri.vpbanner.banner.BannerViewPager;
import com.yuri.vpbanner.banner.CircleFlowIndicator;
import com.yuri.vpbanner.banner.DensityUtil;
import com.yuri.vpbanner.banner.SimpleItemClickListener;
import com.yuri.xlog.Log;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SimpleItemClickListener {

    /**banner默认图片的宽度*/
    public static final float DEFAULT_BANNER_WIDTH = 1080.0f;
    /**banner默认图片的高度*/
    public static final float DEFAULT_BANNER_HEIGTH = 584.0f;

    private BannerAdapter mBannerAdapter;
    private BannerViewPager mBannerViewPager;
    private CircleFlowIndicator mFlowIndicator;

    private String[] mTestImageUrls = {
            "http://a.hiphotos.baidu.com/image/h%3D200/sign=aefdabb8d7160924c325a51be406359b/86d6277f9e2f07083038e6eeee24b899a901f21b.jpg",
            "http://a.hiphotos.baidu.com/image/h%3D200/sign=681ee006bb3eb1355bc7b0bb961fa8cb/9f510fb30f2442a73127314bd643ad4bd1130237.jpg",
            "http://f.hiphotos.baidu.com/image/h%3D200/sign=748c50328d1363270aedc533a18ea056/5243fbf2b2119313d52d335e62380cd790238df6.jpg",
            "http://a.hiphotos.baidu.com/image/h%3D200/sign=c20ef72a1cd5ad6eb5f963eab1ca39a3/377adab44aed2e739bc14ac28001a18b87d6fa30.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View bannerView = findViewById(R.id.banner_content);
        //调整一下banner的高度,使之与不同的分辨率屏幕适配
        bannerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                (int) (DensityUtil.getWidthInPx(this) *  DEFAULT_BANNER_HEIGTH / DEFAULT_BANNER_WIDTH)));

        mBannerViewPager = (BannerViewPager) findViewById(R.id.bannerviewpager);
        mFlowIndicator = (CircleFlowIndicator) findViewById(R.id.indicator);


        List<String> bannerList = Arrays.asList(mTestImageUrls);
        Log.d("bannerList.size:%d", bannerList.size());

        mBannerAdapter = new BannerAdapter(this, bannerList);
        mBannerViewPager.setAdapter(mBannerAdapter);
        mBannerAdapter.setOnItemClickListener(this);

        //得到ViewPager的宽度（Activity中需要这样获取）
        //如果是Fragment中，你可以在onViewCreated中执行这段代码，就不需要这样做了
        mBannerViewPager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mBannerViewPager.removeOnLayoutChangeListener(this);
                Log.d("width:%d", mBannerViewPager.getWidth());
                mFlowIndicator.setFlowWidth(mBannerViewPager.getWidth());
            }
        });

        if (bannerList.size() <= 1) {
            mFlowIndicator.setVisibility(View.GONE);
        } else {
            mBannerViewPager.setFlowIndicator(mFlowIndicator);
            mBannerViewPager.startAuto();
        }
    }

    @Override
    public void onItemClick(int position, View view) {
        Log.d("position:%d", position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复可见的时候，继续滚动
        mBannerViewPager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //在不可见的时候停止自动滚动
        mBannerViewPager.stop();
    }
}
