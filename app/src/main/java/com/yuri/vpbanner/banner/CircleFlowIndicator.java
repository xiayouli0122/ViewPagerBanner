package com.yuri.vpbanner.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.yuri.xlog.Log;

/**
 * banner 切换 游标控件
 */
public class CircleFlowIndicator extends View implements BannerViewPager.FlowIndicator {
	//圆的style
	private static final int STYLE_STROKE = 0;
	private static final int STYLE_FILL = 1;

	/**小圆半径*/
	private float radius = 10;
	/**每个圆心的间距*/
	private float circleSeparation = 2 * radius + radius;
	private float activeRadius = 0.5f;
	/**默认圆的画笔*/
	private final Paint paintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
	/**滑动圆的画笔*/
	private final Paint paintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
	/**banner item的数量*/
	private int itemCount = 0;
	/**当前滚动的距离*/
	private int currentScroll = 0;
	/**滑动控件的宽度，这里指ViewPager*/
	private int flowWidth = 0;

	public CircleFlowIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		int activeType = STYLE_FILL;
		int activeColor = 0xCC8DD9BD;
		int inactiveType = STYLE_FILL;
		int inactiveColor = 0xCCFFFFFF;
		radius = DensityUtil.dip2px(context, 4);
		circleSeparation = 2 * radius + radius;
		activeRadius = 0.5f;
		initColors(activeColor, inactiveColor, activeType, inactiveType);
	}

	private void initColors(int activeColor, int inactiveColor, int activeType,
			int inactiveType) {
		switch (inactiveType) {
		case STYLE_FILL:
			paintInactive.setStyle(Style.FILL);
			break;
		default:
			paintInactive.setStyle(Style.STROKE);
		}
		paintInactive.setColor(inactiveColor);

		switch (activeType) {
		case STYLE_STROKE:
			paintActive.setStyle(Style.STROKE);
			break;
		default:
			paintActive.setStyle(Style.FILL);
		}
		paintActive.setColor(activeColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float centeringOffset = 0;
		int leftPadding = getPaddingLeft();
		//圆心Y坐标 paddingTop + 最外面圆的半径
		float centerY = getPaddingTop() + radius;
		//不动圆的X坐标，每个圆的坐标
		float inActiveCenterX;
		for (int iLoop = 0; iLoop < itemCount; iLoop++) {
			inActiveCenterX = leftPadding + radius + (iLoop * circleSeparation) + centeringOffset;
			//绘制不动的圆
			canvas.drawCircle(inActiveCenterX, centerY, radius, paintInactive);
		}
		float cx = 0;
		if (flowWidth != 0) {
			cx = (currentScroll * circleSeparation) / flowWidth;
		}
		//根据滑动的距离动态计算动圆圆心X坐标值
		float activeCenterX = leftPadding + radius + cx + centeringOffset;
		//画动的中心圆
		canvas.drawCircle(activeCenterX,centerY, radius + activeRadius, paintActive);
	}

	@Override
	public void setViewPager(BannerViewPager bannerViewPager, int itemCount) {
		this.flowWidth = bannerViewPager.getWidth();
		this.itemCount = itemCount;
		invalidate();
		requestLayout();
	}

	@Override
	public void onScrolled(int h, int v, int oldh, int oldv) {
		if (itemCount * flowWidth != 0) {
			currentScroll = h % (itemCount * flowWidth);
		} else {
			currentScroll = h;
		}
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * 重新计算 该控件的宽度
     */
	private int measureWidth(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) (getPaddingLeft() + getPaddingRight()
					+ (itemCount * 2 * radius) + (itemCount - 1) * radius + 1);
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * 重新计算 该控件的高度
	 */
	private int measureHeight(int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) (2 * radius + getPaddingTop() + getPaddingBottom() + 1);
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	public void setFillColor(int color) {
		paintActive.setColor(color);
		invalidate();
	}

	public void setStrokeColor(int color) {
		paintInactive.setColor(color);
		invalidate();
	}

	public void setFlowWidth(int width) {
		flowWidth = width;
	}
}