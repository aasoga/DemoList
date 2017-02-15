package com.example.v_yanligang.nuomidemo.behavior;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.v_yanligang.nuomidemo.R;
import com.example.v_yanligang.nuomidemo.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_yanligang on 2016/12/14.
 */

public class BehaviorDetailActivity extends Activity{
    private List<View> mViewList = new ArrayList<>();
    private int[] imgs = {R.drawable.pizza, R.drawable.pic2, R.drawable.pic3};
    private boolean isFirst = true;
    private ZoomHeaderViewPager mViewPager;
    private ZoomHeadView mHeadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behaviordetail);
        initView();
    }

    private void initView() {
        for (int i=0; i <=2; i++) {
            mViewList.add(View.inflate(BehaviorDetailActivity.this, R.layout.item_behaviordetail, null));
        }

        mViewPager = (ZoomHeaderViewPager) findViewById(R.id.vp);
        mViewPager.setAdapter(new Myadapter());
        mViewPager.setOffscreenPageLimit(4);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setAdapter(new RvAdapter());
        CtrlLinearLayoutManager manager = new CtrlLinearLayoutManager(this);
        rv.setLayoutManager(manager);

        mHeadView = (ZoomHeadView) findViewById(R.id.zoomheader);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirst) {
            isFirst = false;
            // 将文字布局放在图片下面
            for (int i = 0; i <mViewPager.getChildCount(); i++) {
                View bottom = mViewPager.getChildAt(i).findViewById(R.id.ll_bottom);
                bottom.setY(mViewPager.getChildAt(i).findViewById(R.id.imageView).getHeight());
                bottom.setX(50);
                mHeadView.setY(mHeadView.getY() -1);
            }
        }
    }

    public class Myadapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LogUtils.e("activity", "length" + position%imgs.length);
            mViewList.get(position).findViewById(R.id.imageView).setBackgroundResource(imgs[(position%imgs.length)]);
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


    public class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case 0:
                    return new ViewHolder(inflater.inflate(R.layout.item_address, parent, false));
                case 1:
                    return new ViewHolder(inflater.inflate(R.layout.item_time, parent, false));
                case 2:
                    return new ViewHolder(inflater.inflate(R.layout.item_recommend, parent, false));
                case 3:
                    return new ViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
            }
            return new ViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
            }
            return 4;
        }

        @Override
        public int getItemCount() {
            return 15;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
