package com.example.v_yanligang.nuomidemo.behavior;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.example.v_yanligang.nuomidemo.util.LogUtils;

/**
 * Created by v_yanligang on 2016/12/15.
 */

public class ZoomHeadView extends LinearLayout{

    private static final String TAG = ZoomHeadView.class.getSimpleName();
    private ZoomHeaderViewPager mViewPager;
    private RecyclerView mRecycleView;
    private float mDownY;
    private float mTouchSlop;
    private float mFirstY;
    private boolean isExpand=false;

    public ZoomHeadView(Context context) {
        super(context);
    }

    public ZoomHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public ZoomHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mFirstY = getY();
        LogUtils.e(TAG, "mFirstY" + mFirstY);
        mViewPager = (ZoomHeaderViewPager) getChildAt(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                // event.getY 触摸点的y值  getY view的y坐标值
//                Log.e("header", event.getY() + "===" +getY()+ "===" + getHeight());

                float moveY = event.getY() - mDownY;
                float currentY = getY();
                //moveY+currentY 现在view的y坐标
//                Log.e("header", "moveY" +event.getY()+"currentY "+currentY +"====" + "getheight" + getHeight());
                if (moveY+currentY <0 && moveY+currentY > -getHeight()/2) {
                    doPageUp(moveY,currentY);
                }

                if (moveY+currentY >0 && moveY+currentY <800) {
                    doPageDown(moveY,currentY);
                }
                break;
            case MotionEvent.ACTION_UP:
                float upY = event.getY() - mDownY;
                float currentUpY = getY();
//                LogUtils.e(TAG, "upY" + upY + "===" + "currentupY" + currentUpY + "=== getHeight" + getHeight());
                if (upY + currentUpY > 190) { // 往下滑达到阈值，结束activity
                    finish();
                }
                // 不在任何阈值，恢复
                if (upY + currentUpY <190 && upY + currentUpY > -getHeight()/5) {
                    restore(upY + currentUpY);
                }

                // 超过展开阈值，则展开
                if (upY + currentUpY < -getHeight()/5) {
                    expand(upY + currentUpY);
                }
            return true;
        }
        return super.onTouchEvent(event);
    }
    // 展开成详情
    private void expand(float y) {
        mRecycleView.scrollToPosition(0);
        ((CtrlLinearLayoutManager)mRecycleView.getLayoutManager()).setCanScroll(true);
        ValueAnimator expandVa = ValueAnimator.ofFloat(y, -getHeight() / 3);
        expandVa.setDuration(300);
        expandVa.setInterpolator(new LinearInterpolator());
        expandVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float y = (float) animation.getAnimatedValue();
                setTranslationY(y);
                isExpand = true;
                mViewPager.canScroll= false;
            }
        });
        expandVa.start();
    }
    // 恢复成viewpager
    public void restore(float y) {
        LogUtils.e(TAG, "y"+y);
        mRecycleView.scrollToPosition(0);
        ValueAnimator restoreVa = ValueAnimator.ofFloat(y, mFirstY);
        restoreVa.setInterpolator(new DecelerateInterpolator());
        restoreVa.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float y = (float) animation.getAnimatedValue();
                setTranslationY(y);
                isExpand = false;
                mViewPager.canScroll= true;
            }
        });
        ((CtrlLinearLayoutManager)mRecycleView.getLayoutManager()).setCanScroll(false);
        restoreVa.setDuration(300);
        restoreVa.start();
    }

    private void finish() {
        ((Activity)getContext()).finish();
    }


    private void doPageUp(float moveY, float currentY) {
        setTranslationY(moveY+currentY);
    }

    private void doPageDown(float moveY, float currentY) {
        setTranslationY(moveY+currentY);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getY() - mDownY) > mTouchSlop) {
                    return true;
                }

        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setRecycleView(RecyclerView recycleView) {
        this.mRecycleView = recycleView;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public boolean isExpand() {
        return isExpand;
    }
}
