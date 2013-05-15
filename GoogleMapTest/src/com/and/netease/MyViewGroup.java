package com.and.netease;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyViewGroup extends ViewGroup {
	private static final String TAG = "ViewGroupDebug";
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker01;
	private int mScrollingX = 0;
	private int mCurrentLayoutFlag = 0;
	private float mLastMovingX;
	private static final int FLING_VELOCITY = 1000;
	private final static int REST_STATE = 0x001;
	private final static int SCROLLING_STATE = 0x002;
	private int mTouchState = REST_STATE;
	private int mScaledTouchSlop = 0;

	/* Constructor */
	public MyViewGroup(Context context) {
		super(context);
		mScroller = new Scroller(context);
		/* 在User触控滑动前预测移动位移 */
		mScaledTouchSlop = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
		/* 配置ViewGroup的的宽为WRAP_CONTENT，高为FILL_PARENT */
		MyViewGroup.this.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.FILL_PARENT));
	}

	/* Constructor for layout */
	public MyViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(context);
		mScaledTouchSlop = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
		MyViewGroup.this.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.FILL_PARENT));

		TypedArray mTypedArray01 = getContext().obtainStyledAttributes(attrs,
				R.styleable.SlideStyledAttributes);
		mCurrentLayoutFlag = mTypedArray01.getInteger(
				R.styleable.SlideStyledAttributes_view_screen, 0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		/* 实现onInterceptTouchEvent方法拦截User手指触控屏幕移动事件 */
		if ((event.getAction() == MotionEvent.ACTION_MOVE)
				&& (mTouchState != REST_STATE)) {
			return true;
		}
		switch (event.getAction()) {
		/* 按住触控屏幕且移动事件 */
		case MotionEvent.ACTION_MOVE:
			/* 判断ACTION_MOVE事件间的移动X坐标间距 */
			int intShiftX = (int) Math.abs(event.getX() - mLastMovingX);
			boolean bMovingDiff = intShiftX > mScaledTouchSlop;

			Log.i(TAG, Boolean.toString(bMovingDiff));
			if (bMovingDiff) {
				mTouchState = SCROLLING_STATE;
			}
			break;
		/* 按住触控屏幕事件开始 */
		case MotionEvent.ACTION_DOWN:
			// 记下按下的X坐标
			mLastMovingX = event.getX();
			mTouchState = mScroller.isFinished() ? REST_STATE : SCROLLING_STATE;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			/* 手指离开屏幕 */
			mTouchState = REST_STATE;
			break;
		}
		return mTouchState != REST_STATE;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker01 == null) {
			/*
			 * 实现flinging事件，通过obtain()取得新的tracking实例
			 */
			mVelocityTracker01 = VelocityTracker.obtain();
		}
		/* 将User触控的MotionEvent加入Tracker */
		mVelocityTracker01.addMovement(event);
		// *判断User的onTouchEvent屏幕触控事件*/
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/* 判断滑动事件是否完成，并停止滑动动画 */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			/* 将当手指按下屏幕触发事件，记下X坐标 */
			mLastMovingX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			/*
			 * 当手指离开屏幕事件发生 记录下mVelocityTracker01的纪录，并取得X轴滑动速度
			 */
			VelocityTracker velocityTracker = mVelocityTracker01;
			velocityTracker.computeCurrentVelocity(1000);
			float velocityX = velocityTracker.getXVelocity();
			/* 当X轴滑动速度大于1000，且mCurrentLayoutFlag>0 */
			if (velocityX > FLING_VELOCITY && mCurrentLayoutFlag > 0) {
				/* 向左移动画面 */
				snapToScreen(mCurrentLayoutFlag - 1);
			} else if (velocityX < -FLING_VELOCITY
					&& mCurrentLayoutFlag < getChildCount() - 1) {
				/* 向右移动画面 */
				snapToScreen(mCurrentLayoutFlag + 1);
			} else {
				snapToDestination();
			}
			if (mVelocityTracker01 != null) {
				mVelocityTracker01.recycle();
				mVelocityTracker01 = null;
			}
			mTouchState = REST_STATE;
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.i(TAG, "event : cancel");
			mTouchState = REST_STATE;
		}
		mScrollingX = MyViewGroup.this.getScrollX();
		return true;
	}

	private void snapToDestination() {
		int screenWidth = getWidth();
		int whichScreen = (mScrollingX + (screenWidth / 2)) / screenWidth;
		snapToScreen(whichScreen);
	}

	public void snapToScreen(int whichScreen) {
		mCurrentLayoutFlag = whichScreen;
		final int newX = whichScreen * getWidth();
		final int delta = newX - mScrollingX;
		mScroller.startScroll(mScrollingX, 0, delta, 0, Math.abs(delta) * 2);
		/* 静止重绘View的画面 */
		invalidate();
	}

	/* 继承自ViewGroup必须重写的onLayout()方法 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth,
						child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	/* 覆写onMeasure方法，并判断所在Layout Flag */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("error mode.");
		}
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurrentLayoutFlag * width, 0);
	}

	/* 覆写computeScroll()方法告知View已更新 */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			/* 取得目前Scroller的X offset */
			mScrollingX = mScroller.getCurrX();
			/* 移动至scroll移动的position */
			scrollTo(mScrollingX, 0);
			/* 调用invalidate()方法处理来自non-UI thread的移动请求 */
			postInvalidate();
		}
	}
}
