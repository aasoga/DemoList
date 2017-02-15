package com.example.v_yanligang.nuomidemo.behavior;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by v_yanligang on 2016/12/27.
 */

public class CtrlLinearLayoutManager extends LinearLayoutManager {
    private boolean canScroll = true;

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public CtrlLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically()&&canScroll;
    }
}
