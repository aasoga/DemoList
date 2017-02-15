package com.example.v_yanligang.nuomidemo.behavior;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by v_yanligang on 2016/12/26.
 */

public class ZoomHeaderViewPager extends ViewPager{
    public boolean canScroll = true;
    public ZoomHeaderViewPager(Context context) {
        super(context);
    }

    public ZoomHeaderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canScroll&&super.onTouchEvent(ev);
    }
    // 改变系统绘制顺序
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int position = getCurrentItem();
//        Log.e("viewpager", "position" + position + "=== childcount" + childCount + "===i" +i);
        if (position < 0) {
            return i;
        }
        if(i == childCount-1) { //最后需要 刷新的item
            if(position > i) {
                position = i;
            }
            return position;
        }

        if (i == position) { // 这是原本要在最后一个刷新的item
            return childCount-1;
        }
        return i;
    }
}
