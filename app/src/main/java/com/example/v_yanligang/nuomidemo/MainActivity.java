package com.example.v_yanligang.nuomidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.v_yanligang.nuomidemo.behavior.BehaviorActivity;

public class MainActivity extends Activity {

    private Button button;
    private TextView resultText;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private String[] mList = {"扫描二维码","scrollview","选择图片","jsbridge","Behavior","pathView"};
    private Class[] mActivitys = {CaptureActivity.class,
            HSViewActivity.class, SelectPicActivity.class,
            JsBrigeActivity.class, BehaviorActivity.class, PathAnimActivity.class};
    private int[] REQUEST_CODE = {1,0,0,0,0,0};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MyRvadapter());
        resultText = (TextView) this.findViewById(R.id.result);
//        button = (Button) this.findViewById(R.id.scanCodeButton);
//        button.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, com.example.v_yanligang.nuomidemo.CaptureActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
//            }
//        });
//
//
//        findViewById(R.id.demo).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, HSViewActivity.class);
//                startActivity(intent);
//            }
//        });
////        new Observable();
//        findViewById(R.id.picture).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SelectPicActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getBundleExtra("bundle");
                    String resultString = bundle.getString("result");
                    resultText.setText(resultString);
                }
                break;
        }
    }

    public class MyRvadapter extends RecyclerView.Adapter<MyRvadapter.ViewHoder>{


        @Override
        public ViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//            View view = View.inflate(getApplicationContext(), R.layout.item_main_rv, null);
            return new ViewHoder(inflater.inflate(R.layout.item_main_rv, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHoder holder, final int position) {
            holder.mTv.setText(mList[position]);
            holder.mTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, mActivitys[position]);
                    if (REQUEST_CODE[position] != 0) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, REQUEST_CODE[position]);
                        return;
                    }
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.length;
        }

        public class ViewHoder extends RecyclerView.ViewHolder {
            public TextView mTv;
            public ViewHoder(View itemView) {
                super(itemView);
                mTv = (TextView) itemView.findViewById(R.id.tv);
            }
        }
    }
}
