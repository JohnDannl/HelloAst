package com.arcsoft.jd5737.helloworld;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.arcsoft.jd5737.viewpager.MyViewPagerAdapter;

public class ViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(),8));
        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(2);
    }
}
