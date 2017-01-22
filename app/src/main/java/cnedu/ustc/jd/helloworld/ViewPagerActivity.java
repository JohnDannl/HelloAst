package cnedu.ustc.jd.helloworld;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import cnedu.ustc.jd.viewpager.MyViewPagerAdapter;

public class ViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ustc.jd5737.helloworld.R.layout.activity_view_pager);
        final ViewPager pager = (ViewPager) findViewById(com.ustc.jd5737.helloworld.R.id.view_pager);
        pager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(),16));
        pager.setOffscreenPageLimit(1);
        pager.setCurrentItem(2);

        TextView tv_msg = (TextView) findViewById(com.ustc.jd5737.helloworld.R.id.vpg_tv_msg);
        tv_msg.setText(this.toString()+"@"+this.getTaskId());

        Button btn_go = (Button) findViewById(com.ustc.jd5737.helloworld.R.id.vpg_btn_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPagerActivity.this,ThirdActivity.class);
                startActivity(intent);
            }
        });

        final ViewTreeObserver.OnGlobalLayoutListener callback = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                android.util.Log.d("XXXX","w:" + pager.getMeasuredWidth() + ",h:" + pager.getMeasuredHeight());
            }
        };
        pager.getViewTreeObserver().addOnGlobalLayoutListener(callback);
    }
}
