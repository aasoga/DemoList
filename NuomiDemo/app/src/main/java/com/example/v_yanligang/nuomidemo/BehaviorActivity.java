package com.example.v_yanligang.nuomidemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.v_yanligang.nuomidemo.behavior.BehaviorDetailActivity;

/**
 * Created by v_yanligang on 2016/12/14.
 */
public class BehaviorActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        rv.setAdapter(new Myadapter());
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    public class Myadapter extends RecyclerView.Adapter<BehaviorActivity.Myadapter.ViewHolder>{


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(View.inflate(getApplicationContext(), R.layout.item_behavior, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BehaviorActivity.this, BehaviorDetailActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 15;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
