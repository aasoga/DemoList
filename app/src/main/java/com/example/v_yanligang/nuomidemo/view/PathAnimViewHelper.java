package com.example.v_yanligang.nuomidemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.animation.LinearInterpolator;


/**
 * Created by v_yanligang on 2017/2/8.
 */

public class PathAnimViewHelper {

    private static final long mDefaultAnimTime = 1500; //默认动画总时间
    private PathAnimView mView;
    private Path mSourcePath;
    private Path mAnimPath;
    private long mAnimTime = mDefaultAnimTime;
    private boolean isInfinite = true; // 是否循环
    private ValueAnimator mAnimator;
    public PathAnimViewHelper(PathAnimView pathAnimView, Path mSourcePath, Path mAnimPath) {
        mView = pathAnimView;
        this.mSourcePath = mSourcePath;
        this.mAnimPath = mAnimPath;
    }


    public void setInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
    }

    public void setAnimTime(long animTime) {
        this.mAnimTime = animTime;
    }

    public void startAnim() {
        startAnim(mView, mSourcePath, mAnimPath, mAnimTime, isInfinite);
    }

    // 一个sourcePath内含多段path，循环取出每段path，并做一个动画
    private void startAnim(PathAnimView view, Path sourcePath, Path animPath, long animTime, boolean isInfinite) {
        if (view == null || sourcePath == null || animPath == null) {
            return;
        }
        animPath.reset();
        animPath.lineTo(0,0);
        PathMeasure pathMeasure = new PathMeasure();
        pathMeasure.setPath(sourcePath, false);
        //计算每段动画需要的时间
        int count = 0;
        while (pathMeasure.getLength() != 0) {
            pathMeasure.nextContour();
            count++;
        }

        pathMeasure.setPath(sourcePath, false);
        loopAnim(view, pathMeasure, sourcePath, animPath, animTime/count, isInfinite);
    }

    private void loopAnim(final PathAnimView view, final PathMeasure pathMeasure, final Path sourcePath, final Path animPath, long duration, final boolean isInfinite) {
        mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(duration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                animPath.reset();
//                animPath.lineTo(0,0);
                float value = (float) animation.getAnimatedValue();
//                LogUtils.e("path", "value" + value);
                float stop = pathMeasure.getLength()*value;
                float start = (float) (stop - ((0.5 - Math.abs(value - 0.5)) * pathMeasure.getLength()));
//                pathMeasure.getSegment(start, stop, animPath, true);
//                LogUtils.e("path", "stop" + stop);
//                LogUtils.e("path", "start" + start);
                pathMeasure.getSegment(0, stop, animPath, true);
                view.invalidate();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                // 每段path走完以后要补一下，某些情况会出现animpath不满的情况
                pathMeasure.getSegment(0, pathMeasure.getLength(), animPath, true);
                // 绘制完一条后再绘制下一条
                pathMeasure.nextContour();

                if (pathMeasure.getLength() == 0) {
                    if (isInfinite) {
                        animPath.reset();
                        animPath.lineTo(0,0);
                        pathMeasure.setPath(sourcePath, false);
                    } else {
                        animation.end();
                    }
                }
            }
        });

        mAnimator.start();
    }

    public void stopAnim() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.end();
        }
    }
}
