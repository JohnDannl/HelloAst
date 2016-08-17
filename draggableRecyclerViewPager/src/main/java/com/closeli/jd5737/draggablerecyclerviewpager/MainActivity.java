package com.closeli.jd5737.draggablerecyclerviewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.closeli.jd5737.draggablerecyclerviewpager.widget.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static int COLUMN_SIZE = 2;
    private static int DELAY_TIME = 5000;
    private ImageView imgNavLeft;
    private ImageView imgNavRight;
    private boolean isNavBarShow = false;
    private RecyclerViewPager recyclerViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        recyclerViewPager = (RecyclerViewPager) findViewById(R.id.recycler_view_pager);
        recyclerViewPager.setHasFixedSize(true);
        recyclerViewPager.setAdapter(new RecyclerViewPagerAdapter(this));
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_SIZE, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewPager.setLayoutManager(layoutManager);
        recyclerViewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
                if(isNavBarShow) {
                    showNavBar();
                }
            }
        });

        recyclerViewPager.setOnClickListener(new RecyclerViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int childIndex) {
                //Toast.makeText(MainActivity.this,"click:" + childIndex, Toast.LENGTH_SHORT).show();
                showNavBar();
            }

            @Override
            public void onItemDoubleClick(int childIndex) {
                Toast.makeText(MainActivity.this,"Double click:" + childIndex, Toast.LENGTH_SHORT).show();
            }
        });

        imgNavLeft=(ImageView)findViewById(R.id.navi_bar_left);
        imgNavLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPager.scrollLeft();
            }
        });
        imgNavRight=(ImageView)findViewById(R.id.navi_bar_right);
        imgNavRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPager.scrollRight();
            }
        });
    }
    Handler mHandler = new Handler();
    private Runnable hideNavBarCallback =new Runnable() {
        @Override
        public void run() {
            imgNavLeft.setVisibility(View.GONE);
            imgNavRight.setVisibility(View.GONE);
            isNavBarShow=false;
        }
    };
    private void hideNavBar(){
        imgNavLeft.setVisibility(View.GONE);
        imgNavRight.setVisibility(View.GONE);
        isNavBarShow=false;
    }
    private void showNavBar(){
        if(recyclerViewPager.isFullScreen())return;
        mHandler.removeCallbacks(hideNavBarCallback);
        mHandler.postDelayed(hideNavBarCallback,DELAY_TIME);
        if(recyclerViewPager.canScrollLeft()){
            imgNavLeft.setVisibility(View.VISIBLE);
        }else{
            imgNavLeft.setVisibility(View.GONE);
        }
        if(recyclerViewPager.canScrollRight()){
            imgNavRight.setVisibility(View.VISIBLE);
        }else{
            imgNavRight.setVisibility(View.GONE);
        }
        isNavBarShow=true;
    }
}
