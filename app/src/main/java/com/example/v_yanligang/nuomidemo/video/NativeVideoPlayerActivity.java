package com.example.v_yanligang.nuomidemo.video;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.example.v_yanligang.nuomidemo.R;
import com.example.v_yanligang.nuomidemo.util.LogUtils;

import static com.example.v_yanligang.nuomidemo.R.id.vv;

/**
 * Created by v_yanligang on 2017/2/14.
 */
public class NativeVideoPlayerActivity extends Activity implements View.OnClickListener {
    private Video mVideo;
    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nativeplaye);
        initData();
        initView();
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(vv);
        mVideoView.setVideoPath(mVideo.path);
        findViewById(R.id.start_bt).setOnClickListener(this);
    }

    private void initData() {
        mVideo = (Video) getIntent().getSerializableExtra("video");
        LogUtils.e("play", "video" + mVideo.path);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_bt:
                mVideoView.start();
                break;
        }
    }
}
