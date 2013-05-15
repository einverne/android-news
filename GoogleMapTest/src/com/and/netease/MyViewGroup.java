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
		/* ��User���ػ���ǰԤ���ƶ�λ�� */
		mScaledTouchSlop = ViewConfiguration.get(getContext())
				.getScaledTouchSlop();
		/* ����ViewGroup�ĵĿ�ΪWRAP_CONTENT����ΪFILL_PARENT */
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
		/* ʵ��onInterceptTouchEvent��������User��ָ������Ļ�ƶ��¼� */
		if ((event.getAction() == MotionEvent.ACTION_MOVE)
				&& (mTouchState != REST_STATE)) {
			return true;
		}
		switch (event.getAction()) {
		/* ��ס������Ļ���ƶ��¼� */
		case MotionEvent.ACTION_MOVE:
			/* �ж�ACTION_MOVE�¼�����ƶ�X������ */
			int intShiftX = (int) Math.abs(event.getX() - mLastMovingX);
			boolean bMovingDiff = intShiftX > mScaledTouchSlop;

			Log.i(TAG, Boolean.toString(bMovingDiff));
			if (bMovingDiff) {
				mTouchState = SCROLLING_STATE;
			}
			break;
		/* ��ס������Ļ�¼���ʼ */
		case MotionEvent.ACTION_DOWN:
			// ���°��µ�X����
			mLastMovingX = event.getX();
			mTouchState = mScroller.isFinished() ? REST_STATE : SCROLLING_STATE;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			/* ��ָ�뿪��Ļ */
			mTouchState = REST_STATE;
			break;
		}
		return mTouchState != REST_STATE;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mVelocityTracker01 == null) {
			/*
			 * ʵ��flinging�¼���ͨ��obtain()ȡ���µ�trackingʵ��
			 */
			mVelocityTracker01 = VelocityTracker.obtain();
		}
		/* ��User���ص�MotionEvent����Tracker */
		mVelocityTracker01.addMovement(event);
		// *�ж�User��onTouchEvent��Ļ�����¼�*/
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			/* �жϻ����¼��Ƿ���ɣ���ֹͣ�������� */
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			/* ������ָ������Ļ�����¼�������X���� */
			mLastMovingX = event.getX();
			break;
		case MotionEvent.ACTION_UP:
			/*
			 * ����ָ�뿪��Ļ�¼����� ��¼��mVelocityTracker01�ļ�¼����ȡ��X�Ử���ٶ�
			 */
			VelocityTracker velocityTracker = mVelocityTracker01;
			velocityTracker.computeCurrentVelocity(1000);
			float velocityX = velocityTracker.getXVelocity();
			/* ��X�Ử���ٶȴ���1000����mCurrentLayoutFlag>0 */
			if (velocityX > FLING_VELOCITY && mCurrentLayoutFlag > 0) {
				/* �����ƶ����� */
				snapToScreen(mCurrentLayoutFlag - 1);
			} else if (velocityX < -FLING_VELOCITY
					&& mCurrentLayoutFlag < getChildCount() - 1) {
				/* �����ƶ����� */
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
		/* ��ֹ�ػ�View�Ļ��� */
		invalidate();
	}

	/* �̳���ViewGroup������д��onLayout()���� */
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

	/* ��дonMeasure���������ж�����Layout Flag */
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

	/* ��дcomputeScroll()������֪View�Ѹ��� */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			/* ȡ��ĿǰScroller��X offset */
			mScrollingX = mScroller.getCurrX();
			/* �ƶ���scroll�ƶ���position */
			scrollTo(mScrollingX, 0);
			/* ����invalidate()������������non-UI thread���ƶ����� */
			postInvalidate();
		}
	}
}
