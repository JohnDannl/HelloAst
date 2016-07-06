package com.arcsoft.jd5737.helloworld;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private LinearLayout viewContainer;
    private ArrayList<TextView> addedViews=new ArrayList<TextView>();
    private boolean start=false;
    private boolean isPortait=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        viewContainer=(LinearLayout) findViewById(R.id.adaptive_view_container);
        EditText searchView=(EditText) LayoutInflater.from(this).inflate(R.layout.et_add_sub_view,null);
        /*LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1);
        viewContainer.addView(searchView,llp);*/
        viewContainer.addView(searchView);
        Button btn_add=(Button)findViewById(R.id.btn_add_view);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv=(TextView)LayoutInflater.from(mContext).inflate(R.layout.tv_add_sub_view,null);
                tv.setText(getString(R.string.btn_prefix,addedViews.size()+1));
                viewContainer.addView(tv,addedViews.size());
                addedViews.add(tv);
            }
        });
        Button btn_rm=(Button)findViewById(R.id.btn_rm_view);
        btn_rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addedViews.size()>0){
                    viewContainer.removeViewAt(addedViews.size()-1);
                    addedViews.remove(addedViews.size()-1);
                }
            }
        });
        final Button btn_anim=(Button)findViewById(R.id.btn_anim);
        final ScaleAnimation scale = new ScaleAnimation(.667f, 1.5f, .667f, 1.5f, btn_anim.getMeasuredWidth(), btn_anim.getMeasuredHeight() * 3 / 4);
        scale.setDuration(200);
        scale.setRepeatMode(Animation.REVERSE);
        scale.setRepeatCount(Animation.INFINITE);
        btn_anim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!start){
                    start=true;
                    btn_anim.clearAnimation();
                    btn_anim.startAnimation(scale);
                }else{
                    start=false;
                    btn_anim.clearAnimation();
                }
            }
        });
        Button btn_rotate=(Button)findViewById(R.id.btn_rotate);
        btn_rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPortait){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    isPortait=true;
                }else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    isPortait=false;
                }
            }
        });
        Button btn_toast=(Button)findViewById(R.id.btn_toast);
        btn_toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                Toast.makeText(MainActivity.this,"width:"+width+",height:"+height,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
