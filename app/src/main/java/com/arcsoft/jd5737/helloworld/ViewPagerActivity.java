package com.arcsoft.jd5737.helloworld;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        TextView tv_msg = (TextView) findViewById(R.id.vpg_tv_msg);
        tv_msg.setText(this.toString()+"@"+this.getTaskId());

        Button btn_go = (Button) findViewById(R.id.vpg_btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPagerActivity.this,ThirdActivity.class);
                startActivity(intent);
            }
        });
    }
}
