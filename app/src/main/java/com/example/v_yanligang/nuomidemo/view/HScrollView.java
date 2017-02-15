package com.example.v_yanligang.nuomidemo.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by v_yanligang on 2016/6/20.
 */
public class HScrollView extends HorizontalScrollView implements View.OnClickListener {
    private Context mContext;
    private LinearLayout mContainer;
    private int mScreenWidth;
    private int mChildWidth;
    private int mChildHeight;
    private BaseAdapter mAdapter;
    private int mCurrentPosition;
    private OnCurrentItemClick mOnCurrentItemClick;
    private OnSelectListener mOnSelectListener;
    private Map<View, Integer> mViewPos = new HashMap<>();
    private int mScrollX;
    private int mLastScrollX;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mLastScrollX == mScrollX) { // 停下
                Log.e("HScrollView", "mCurrentPosition" + mCurrentPosition);
                mHandler.removeMessages(0);
                setmCurrentPosition(mCurrentPosition);
            } else {
                mLastScrollX = mScrollX;
                mHandler.sendEmptyMessageDelayed(0, 50);
            }
        }
    };

    public interface OnCurrentItemClick {
        void click(View v, int currentPosition);
    }

    public interface OnSelectListener {
        void onSelectCurrent(int position, int diff);

        void onSelectNext(int position);
    }

    public void setOnCurrentItemClick(OnCurrentItemClick onCurrentItemClick) {
        this.mOnCurrentItemClick = onCurrentItemClick;
    }

    public void setOnSelectListener(OnSelectListener onSelectLisener) {
        this.mOnSelectListener = onSelectLisener;
    }

    public HScrollView(Context context) {
        super(context);
    }

    public HScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;

        mContainer = new LinearLayout(mContext);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        addView(mContainer);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollX = l;
//        Log.e("sh", "l" + l + "=====" + oldl + "oldl" + "mChildWidth" + mChildWidth);
        int diff = Math.abs(l - mChildWidth * mCurrentPosition);
        if (diff > mChildWidth / 2) {
            mCurrentPosition = (l + mChildWidth / 2) / mChildWidth;
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectNext(mCurrentPosition);
            }
        } else {
            if (mOnSelectListener != null) {
                mOnSelectListener.onSelectCurrent(mCurrentPosition, diff);
            }
        }
    }

    // 设置适配器
    public void setAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        if (adapter.getCount() > 0) {
            View view = adapter.getView(0, null, null);
            int w = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            mChildWidth = view.getMeasuredWidth();
            mChildHeight = view.getMeasuredHeight();
            initChildView();
        }
    }

    private void initChildView() {
        View emptyView = new View(mContext);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams((mScreenWidth - mChildWidth) / 2, mChildHeight);
        emptyView.setLayoutParams(layoutParams);
        // 加载全部view
        mContainer.addView(emptyView);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = mAdapter.getView(i, null, null);
            view.setOnClickListener(this);
            mContainer.addView(view);
            mViewPos.put(view, i);
        }
        View emptyView1 = new View(mContext);
        emptyView1.setLayoutParams(layoutParams);
        mContainer.addView(emptyView1);
        // 子View放入容器后，容器可能会为其添加间隔，需获取容器宽度，重新计算子View的宽度
        Log.e("HorizontalScrollView", "mChildWidth" + mChildWidth);
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mContainer.measure(w, h);
        int scrollWidth = mContainer.getMeasuredWidth() - mScreenWidth;

    }

    @Override
    public void onClick(View v) {
        mCurrentPosition = mViewPos.get(v);
        if (mOnCurrentItemClick != null) {
            mOnCurrentItemClick.click(v, mCurrentPosition);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mLastScrollX = mScrollX;
            mHandler.sendEmptyMessageDelayed(0, 50);
        }
        return super.onTouchEvent(ev);
    }

    public void setmCurrentPosition(int currentPosition) {
        if (currentPosition < 0) {
            currentPosition = 0;
        } else if (mCurrentPosition > mAdapter.getCount() - 1) {
            mCurrentPosition = mAdapter.getCount() - 1;
        } else {
            mCurrentPosition = currentPosition;
        }
        scrollTo(currentPosition * mChildWidth, 0);
    }


}
