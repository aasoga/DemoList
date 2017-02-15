package com.example.v_yanligang.nuomidemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;

import com.example.v_yanligang.nuomidemo.video.PlayNativeVideoActivity;
import com.example.v_yanligang.nuomidemo.view.PathAnimView;

/**
 * Created by v_yanligang on 2017/1/22.
 */
public class PathAnimActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pathanim);
        initView();
    }

    private void initView() {
        PathAnimView animView = (PathAnimView) findViewById(R.id.path);
        Path path = new Path();
        path.addCircle(100,100,100,Path.Direction.CW);
        path.moveTo(50, 100);
        path.lineTo(100,150);
        path.lineTo(150,50);
        animView.setSourcePath(path);
        animView.setIsShowBg(false);
//        animView.setIsInfinite(false);
        animView.startAnim();

        findViewById(R.id.bt_play).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_play:
                Intent intent = new Intent(this, PlayNativeVideoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
