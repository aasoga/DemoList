package com.example.v_yanligang.nuomidemo.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.v_yanligang.nuomidemo.R;
import com.example.v_yanligang.nuomidemo.util.LogUtils;

/**
 * Created by v_yanligang on 2016/12/14.
 */

public class ZoomHeaderBehavior extends CoordinatorLayout.Behavior<View>{
    private static final String TAG = ZoomHeaderBehavior.class.getSimpleName();
    public ZoomHeaderBehavior(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    private boolean isFirst = true;
    private ZoomHeadView mDependency;

    // child为使用behavior的recycle，dependency为依赖的view
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof ZoomHeadView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (isFirst) {
            isFirst=false;
            mDependency = (ZoomHeadView) dependency;
            mDependency.setRecycleView((RecyclerView) child);
        }

        // 让chlid始终在dependency下面
        child.setY(dependency.getY() + dependency.getHeight());

        //对所有viewpager进行缩放
//        ZoomHeaderViewPager viewPager = (ZoomHeaderViewPager) mDependency.getViewPager();
//        LogUtils.e(TAG, "count" + viewPager.getChildCount());
//        for (int i=0; i<viewPager.getChildCount(); i++) {
//            changeView(child, viewPager.getChildAt(i));
//        }
        float progress = -mDependency.getY() / mDependency.getHeight() * 2;
        child.setAlpha(progress*2);
        if (progress > 0) {
            ((ZoomHeadView)dependency).setScaleX(1+progress/2);
//            ((ZoomHeadView)dependency).setScaleY(1+progress/2);
        }

        return super.onDependentViewChanged(parent, child, dependency);
    }

    private void changeView(View child, View view) {
        View target = view.findViewById(R.id.linearLayout);
        View img = view.findViewById(R.id.imageView);
        View bottom = view.findViewById(R.id.ll_bottom);
        View buyButton = view.findViewById(R.id.btn_buy);
        View nameTxt = view.findViewById(R.id.tv_name);
        View costText = view.findViewById(R.id.tv_cost);

        float progress = -mDependency.getY() / mDependency.getHeight() * 2;
//        Log.e("behavior", bottom.getX() + "===" + progress);
        child.setAlpha(progress*2);

        if (progress >0) {
            // offset为正则x轴正向移动 margin为50
            bottom.offsetLeftAndRight((int)(target.getWidth() / 2 - target.getWidth() * (1 + progress) / 2 + 50 -bottom.getX()));
            buyButton.offsetLeftAndRight((int) (target.getWidth() + target.getWidth()*progress/2
                                -buyButton.getWidth() - 50 - buyButton.getX()));
            //按钮与文字居中
            buyButton.offsetTopAndBottom((int) (bottom.getY()
                    + (costText.getY() + costText.getHeight() - nameTxt.getY()) / 2
                    + nameTxt.getY() - buyButton.getHeight() / 2 - buyButton.getY()));

            if (progress < 0.8) {
                target.setScaleX(1+progress);
                img.setScaleY(1+progress);
            }
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        //只关心垂直事件
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed) {
        Log.e(TAG, "y" + velocityY + "===x" +velocityX);
        if (velocityY < 0 && ((RecyclerView) target).getChildAt(0).getY() == 0) {
            mDependency.restore(mDependency.getY());
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        // 如果在顶部
        LogUtils.e(TAG, "Y" +((RecyclerView)target).getChildAt(0).getY() + "===dy" + dy);
        if(((RecyclerView)target).getChildAt(0).getY() == 0) {
            // 如果向下滑动
            if (dy < 0) {
                mDependency.setY(mDependency.getY() - dy);
                if (mDependency.getY() <500) {
                    mDependency.restore(mDependency.getY());
                }
            }
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }
}
