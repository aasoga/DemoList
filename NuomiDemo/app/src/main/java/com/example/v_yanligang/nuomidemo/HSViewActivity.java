package com.example.v_yanligang.nuomidemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.v_yanligang.nuomidemo.view.HScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by v_yanligang on 2016/6/20.
 */
public class HSViewActivity extends Activity {
    public static final String TAG = HSViewActivity.class.getSimpleName();
    private LinearLayout mLayout;
    private Myadapter mAdapter;
    private TextView mNumTv;

    @BindView(R.id.hs) HScrollView mHScrollview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs);
        ButterKnife.bind(this);
        mLayout = (LinearLayout) findViewById(R.id.ll);
        mNumTv = (TextView) findViewById(R.id.tv_num);
        mAdapter = new Myadapter();
        mHScrollview.setAdapter(mAdapter);
        mHScrollview.setOnCurrentItemClick(new HScrollView.OnCurrentItemClick() {
            @Override
            public void click(View v, int currentPosition) {
                updateView(currentPosition);
            }
        });

        mHScrollview.setOnSelectListener(new HScrollView.OnSelectListener() {
            @Override
            public void onSelectCurrent(int position, int diff) {
//                mHScrollview.setmCurrentPosition(position);
                Log.e(TAG, "onSelectCurrent" + position + "diff" + diff);
            }

            @Override
            public void onSelectNext(int position) {
                Log.e(TAG, "onSelectNext" + position);
                updateView(position);
            }
        });

    }

    private void updateView(int currentPosition) {
        mLayout.removeAllViews();
        for (int i = 0; i < currentPosition; i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageResource(R.mipmap.ic_launcher);
            mLayout.addView(imageView);
        }
        mHScrollview.setmCurrentPosition(currentPosition);
        mNumTv.setText(currentPosition + "");
    }

    public class Myadapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 15;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new LinearLayout(getApplicationContext());
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.bottomMargin = 20;

            TextView textView = new TextView(getApplicationContext());
            (textView).setText("Position" + position);
            (textView).setTextSize(20);

            ((LinearLayout) convertView).addView(textView, layoutParams);
            return convertView;
        }
    }
}
