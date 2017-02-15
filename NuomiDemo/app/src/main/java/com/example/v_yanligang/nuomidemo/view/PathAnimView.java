package com.example.v_yanligang.nuomidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by v_yanligang on 2017/2/6.
 */

public class PathAnimView  extends View {
    private Paint mPaint;
    private Path mSourcePath;
    private Path mAnimPath;
    private int mColorBg = Color.GRAY; //背景色
    private int mColorFg = Color.RED; // 前景色
    private PathAnimViewHelper mHelper; // path动画工具类
    private boolean isShowBg; // 是否显示背景

    public PathAnimView(Context context) {
        this(context,null);
    }

    public PathAnimView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PathAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAnimPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        initPathHelper();
    }

    private void initPathHelper() {
        mHelper = new PathAnimViewHelper(this, mSourcePath, mAnimPath);
    }

    public void setSourcePath(Path sourcePath) {
        mSourcePath = sourcePath;
        initPathHelper();
    }

    public void setIsShowBg(boolean isShow) {
        isShowBg = isShow;
    }

    public void setIsInfinite(boolean infinite) {
        mHelper.setInfinite(infinite);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShowBg) {
            mPaint.setColor(mColorBg); //先绘制背景
            canvas.drawPath(mSourcePath, mPaint);
        }

        // 再绘制前景
        mPaint.setColor(mColorFg);
//        LogUtils.e("pathview", "path" + mAnimPath.toString());
        canvas.drawPath(mAnimPath, mPaint);
    }

    public void startAnim() {
        mHelper.startAnim();
    }

    public void stopAnim() {
        mHelper.stopAnim();
    }

}
