package com.example.v_yanligang.nuomidemo;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.v_yanligang.nuomidemo.util.LogUtils;
import com.example.v_yanligang.nuomidemo.view.ImageLoader;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by v_yanligang on 2016/9/12.
 */

public class SelectPicActivity extends Activity {
    private static final String TAG = "SelectPicActivity";
    public static final String EXTRA_CURRENT_POSITION = "currentposition";

    private ArrayList<String> mPathLists = new ArrayList<>();
    private RecyclerView mRecycleView;
    private Myadapter mAdapter;
    private Bundle mTmpReenterState;
    private int mstartPosition = 0;
    private GridLayoutManager gm;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, mPathLists.size() + "size");
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectpic);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            setExitSharedElementCallback(new SharedElementCallback() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    if (mTmpReenterState != null) {

                        int currentPosition = mTmpReenterState.getInt(EXTRA_CURRENT_POSITION);
                        if (mstartPosition != currentPosition) {
                            // If startingPosition != currentPosition the user must have swiped to a
                            // different page in the DetailsActivity. We must update the shared element
                            // so that the correct one falls into place.
//                    String newTransitionName = String.valueOf(currentPosition);
                            int newTransitionName = currentPosition;
                            LogUtils.e(TAG, "name" + newTransitionName);
//                    View newSharedElement = mRecycleView.findViewWithTag(newTransitionName);
                            View newSharedElement = gm.findViewByPosition(currentPosition);

                            if (newSharedElement != null) {
                                names.clear();
                                names.add(String.valueOf(newTransitionName));
                                sharedElements.clear();
                                sharedElements.put(String.valueOf(newTransitionName), newSharedElement);
                            }
                        }

                        mTmpReenterState = null;
                    } else {
                        // If mTmpReenterState is null, then the activity is exiting.
                        View navigationBar = findViewById(android.R.id.navigationBarBackground);
                        View statusBar = findViewById(android.R.id.statusBarBackground);
                        if (navigationBar != null) {
                            names.add(navigationBar.getTransitionName());
                            sharedElements.put(navigationBar.getTransitionName(), navigationBar);
                        }
                        if (statusBar != null) {
                            names.add(statusBar.getTransitionName());
                            sharedElements.put(statusBar.getTransitionName(), statusBar);
                        }
                    }
                }
            });
        } else {

        }
//        setUpWindowTrisience();
        initView();
        initData();
    }

    private void initView() {
        mRecycleView = (RecyclerView) findViewById(R.id.rv);
        gm = new GridLayoutManager(getApplicationContext(), 4);
        gm.setOrientation(GridLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(gm);
        mAdapter = new Myadapter();
        mRecycleView.setAdapter(mAdapter);
    }

    private void initData() {
        getPicturePaths();
    }

    private void getPicturePaths() {
        new Thread() {
            @Override
            public void run() {
                Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.Media.DATA}, null,
                        MediaStore.Images.Media.DATE_TAKEN + " DESC");
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(columnIndex);
                        File file = new File(path);
                        if (file.exists() && file.length() > 0) {
                            mPathLists.add(path);
                        }
                    }
                    cursor.close();
                    mHandler.sendEmptyMessage(0);
                }
            }
        }.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        int currentPosition = data.getIntExtra(EXTRA_CURRENT_POSITION, 0);
        mTmpReenterState = new Bundle(data.getExtras());
        // 跳到返回来的位置
        mRecycleView.scrollToPosition(currentPosition);

        LogUtils.e(TAG, "reenter position" + currentPosition);
        postponeEnterTransition();
        mRecycleView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // 延迟共享元素返回transition
                mRecycleView.getViewTreeObserver().removeOnPreDrawListener(this);
                mRecycleView.requestLayout();
                startPostponedEnterTransition();
                return true;
            }
        });
    }

    public class Myadapter extends RecyclerView.Adapter<Myadapter.ViewHolder> {



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_activity_select, null));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mPictureView.setTag(R.id.tag_position, position);
            LogUtils.e(TAG, "bind position" + position);
            ViewCompat.setTransitionName(holder.mPictureView, String.valueOf(position));
//            holder.mPictureView.setTransitionName(String.valueOf(position));
            holder.mPictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TransitionManager.beginDelayedTransition(mRecycleView, new Fade());// 设置view渐渐消失
//                    boolean isvisible = mRecycleView.getVisibility() == View.VISIBLE;
//                    mRecycleView.setVisibility(isvisible? View.INVISIBLE:View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mstartPosition = position;
                        LogUtils.e("select",  String.valueOf(holder.mPictureView.getTag(R.id.tag_position)));
                        ActivityOptionsCompat aoc = setTransitions(holder.mPictureView);
                        Intent intent = new Intent(SelectPicActivity.this, PreviewPicActivity.class);
                        intent.putStringArrayListExtra("list", mPathLists);
                        intent.putExtra("position", position);
                        ActivityCompat.startActivity(SelectPicActivity.this, intent, aoc.toBundle());
                    } else {
                        Intent intent = new Intent(SelectPicActivity.this, PreviewPicActivity.class);
                        intent.putStringArrayListExtra("list", mPathLists);
                        intent.putExtra("position", position);
                        ActivityTransitionLauncher.with(SelectPicActivity.this).from(holder.mPictureView).launch(intent);
                        overridePendingTransition(0,0);
                    }

                }
            });
            int side = dip2px(getApplicationContext(), 83f);
            ImageLoader.load(holder.mPictureView, mPathLists.get(position), side, side);
        }

        @Override
        public int getItemCount() {
            return mPathLists == null ? 0 : mPathLists.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView mPictureView;
            public ViewHolder(View itemView) {
                super(itemView);
                mPictureView = (ImageView) itemView.findViewById(R.id.picture);

            }
        }

    }

    private ActivityOptionsCompat setTransitions(View view) {
        ActivityOptionsCompat aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(SelectPicActivity.this, view, String.valueOf(view.getTag(R.id.tag_position)) );
        return aoc;
    }


    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUpWindowTrisience() {
        TransitionSet mtransitionset=new TransitionSet();
        mtransitionset.addTransition(new ChangeBounds());
        mtransitionset.addTransition(new ChangeImageTransform());
//        mtransitionset.addTransition(new Fade());
        mtransitionset.setDuration(250);
        getWindow().setEnterTransition(mtransitionset);
        getWindow().setExitTransition(mtransitionset);
        getWindow().setSharedElementEnterTransition(mtransitionset);
        getWindow().setSharedElementExitTransition(mtransitionset);
        getWindow().setAllowEnterTransitionOverlap(false);

    }
}
