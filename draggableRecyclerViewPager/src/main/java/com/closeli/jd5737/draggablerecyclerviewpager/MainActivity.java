package com.closeli.jd5737.draggablerecyclerviewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.closeli.jd5737.draggablerecyclerviewpager.widget.RecyclerViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static int COLUMN_SIZE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        RecyclerViewPager viewPager = (RecyclerViewPager) findViewById(R.id.recycler_view_pager);
        viewPager.setHasFixedSize(true);
        viewPager.setAdapter(new RecyclerViewPagerAdapter(this));
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_SIZE, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        viewPager.setLayoutManager(layoutManager);
    }
}
