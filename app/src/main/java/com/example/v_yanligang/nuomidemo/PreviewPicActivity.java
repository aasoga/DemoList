package com.example.v_yanligang.nuomidemo;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.example.v_yanligang.nuomidemo.util.FastBlurUtil;
import com.example.v_yanligang.nuomidemo.util.LogUtils;
import com.example.v_yanligang.nuomidemo.view.ImageLoader;
import com.kogitune.activity_transition.ActivityTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by v_yanligang on 2016/11/10.
 */


public class PreviewPicActivity extends Activity {
    private ViewPager mPager;
    private List<String> mPathList = new ArrayList<>();
    private int mPosition; // 传过来的初始位置
    private boolean isBlur = false;
    private Myadapter Madapter;
    private final int VIEW_SIZE = 4;
    private int mCurrentPosition;
    private boolean isReturn;
    private Bundle msavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previewpic);
        initData();
        initView();
        msavedInstanceState = savedInstanceState;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            setEnterSharedElementCallback(new SharedElementCallback() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    if(isReturn) {
                        ImageView imageView = Madapter.getmViews().get(mCurrentPosition % VIEW_SIZE);
                        if (imageView == null) {
                            names.clear();
                            sharedElements.clear();
                        } else if (mCurrentPosition != mPosition){ // 说明翻页了
                            names.clear();
                            sharedElements.clear();
                            LogUtils.e("preview onmap", "name" + imageView.getTransitionName());
                            names.add(imageView.getTransitionName());
                            sharedElements.put(imageView.getTransitionName(), imageView);
                        }
                    }
                }
            });
        } else {
//            ActivityTransition.with(getIntent()).to(mPager.getChildAt(mPosition)).start(msavedInstanceState);
        }

    }

    private void initView() {
        Madapter = new Myadapter(getApplicationContext());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(Madapter);
        final Button bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBlur = true;
                Madapter.notifyDataSetChanged();
            }
        });
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                List<ImageView> imageViews = Madapter.getmViews();
                final ImageView imageView = imageViews.get(position % VIEW_SIZE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // 需要设置imagev的setTransitionName,否则会和返回的tranname对不上
                    ViewCompat.setTransitionName(imageView, String.valueOf(position));
                    if(mPosition == position) {
                        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public boolean onPreDraw() {
                                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                                startPostponedEnterTransition();
                                return true;
                            }
                        });
                    }
                } else {
                    ActivityTransition.with(getIntent()).to(imageView).start(msavedInstanceState);
                }

                setImage(imageView, position);

                mCurrentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPager.setCurrentItem(mPosition);
//        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
//            @Override
//            public void onTransitionStart(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionEnd(Transition transition) {
//                bt.animate().setDuration(500).alpha(1f);
//            }
//
//            @Override
//            public void onTransitionCancel(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionPause(Transition transition) {
//
//            }
//
//            @Override
//            public void onTransitionResume(Transition transition) {
//
//            }
//        });
    }

    private void initData() {
        Intent intent = getIntent();
        mPathList = intent.getStringArrayListExtra("list");
        mPosition = intent.getIntExtra("position", 0);
        mCurrentPosition = mPosition;
    }

    @Override
    public void finishAfterTransition() {
        isReturn = true;
        Intent intent = new Intent();
        intent.putExtra(SelectPicActivity.EXTRA_CURRENT_POSITION, mCurrentPosition);
        LogUtils.e("preview", "position" + mCurrentPosition);
        setResult(RESULT_OK, intent);
        super.finishAfterTransition();
    }

    public class Myadapter extends PagerAdapter {
        private List<ImageView> mViews = new ArrayList<>();


        public Myadapter(Context context) {
            for (int i = 0; i < VIEW_SIZE; i++) {
                ImageView imageView = new ImageView(context);
                mViews.add(imageView);
            }
        }

        @Override
        public int getCount() {
            return mPathList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public List<ImageView> getmViews() {
            return mViews;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mViews.get(position % VIEW_SIZE);
            LogUtils.e("preview", String.valueOf(mPosition) + "position");
//            if (position == mPosition) { // 只设置当前位置的setTransitionName
//                ViewCompat.setTransitionName(imageView, String.valueOf(mPosition));
//            }
            container.addView(imageView);
//            setImage(imageView, position);
            return imageView;
        }

    }

    private ImageSize getPathSize(String path, ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        ImageSize imageSize = new ImageSize();
        imageSize.height = options.outHeight;
        imageSize.width = options.outWidth;
        ImageSize viewSize = getImageViewSize(imageView);
        if (imageSize.height > viewSize.height || imageSize.width > viewSize.width) {
            imageSize.height = viewSize.height;
            imageSize.width = viewSize.width;
        }
        return imageSize;
    }

    private ImageSize getImageViewSize(ImageView view) {
        ImageSize imageSize = new ImageSize();
        imageSize.height = view.getHeight();
        imageSize.width = view.getWidth();
        if (imageSize.height <= 0 || imageSize.width <= 0) {
            DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
            imageSize.height = metrics.heightPixels;
            imageSize.width = metrics.widthPixels;
        }
        return imageSize;
    }

    public class ImageSize {
        int width;
        int height;
    }

    public void setImage(ImageView imageView, int position) {
        Log.e("onPageSelected", imageView.getHeight() + "width" + "position + {position}" + position);
        if (isBlur) {
            Log.e("Pre", "isBlur" + isBlur);
            isBlur = false;
            Bitmap bitmap = BitmapFactory.decodeFile(mPathList.get(position));
            int scaleRatio = 10;
            int blurRadius = 8;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / scaleRatio,
                    bitmap.getHeight() / scaleRatio,
                    false);
            Bitmap bitmap1 = FastBlurUtil.doBlur(scaledBitmap, 8, true);
            imageView.setImageBitmap(bitmap1);
            return;
        }
        ImageSize size = getPathSize(mPathList.get(position), imageView);
        ImageLoader.load(imageView, mPathList.get(position), size.width, size.height);
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            ActivityTransition.with(getIntent()).to(mPager.getChildAt(mPosition)).start(msavedInstanceState).exit(this);
        }

    }
}
