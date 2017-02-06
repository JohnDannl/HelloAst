package cnedu.ustcjd.helloworld;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private LinearLayout viewContainer;
    private ArrayList<TextView> addedViews=new ArrayList<TextView>();
    private boolean isPortait=true;
    private int displayWidth;
    private int displayHeight;
    private  ScaleAnimation scale;
    private TranslateAnimation translate;
    private boolean anim1 =false;
    private boolean anim2 =false;
    private boolean anim3 =false;
    private ObjectAnimator objAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ustc.jd.helloworld.R.layout.activity_main);
        mContext=this;
        viewContainer=(LinearLayout) findViewById(com.ustc.jd.helloworld.R.id.adaptive_view_container);
        EditText searchView=(EditText) LayoutInflater.from(this).inflate(com.ustc.jd.helloworld.R.layout.et_add_sub_view,null);
        /*LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1);
        viewContainer.addView(searchView,llp);*/
        viewContainer.addView(searchView);
        searchView.setText(this.toString()+"@"+this.getTaskId());
        final Button btn_add=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_add_view);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv=(TextView)LayoutInflater.from(mContext).inflate(com.ustc.jd.helloworld.R.layout.tv_add_sub_view,null);
                tv.setText(getString(com.ustc.jd.helloworld.R.string.btn_prefix,addedViews.size()+1));
                viewContainer.addView(tv,addedViews.size());
                addedViews.add(tv);
            }
        });
        Button btn_rm=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_rm_view);
        btn_rm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addedViews.size()>0){
                    viewContainer.removeViewAt(addedViews.size()-1);
                    addedViews.remove(addedViews.size()-1);
                }
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth=size.x;
        displayHeight=size.y;

        final Button btn_rotate=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_rotate);
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
        Button btn_toast=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_toast);
        btn_toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                displayWidth = size.x;
                displayHeight = size.y;
                Toast.makeText(MainActivity.this,"width:"+displayWidth+",height:"+displayHeight,Toast.LENGTH_SHORT).show();
            }
        });
        final Button btn_anim1=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_anim_1);
        final Button btn_anim2=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_anim_2);
        final Button btn_anim3=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_anim_3);
        final LinearLayout animContainer = (LinearLayout)findViewById(com.ustc.jd.helloworld.R.id.anim_container);
        btn_anim1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!anim1){
                    anim1 =true;
                    btn_anim1.clearAnimation();
                    btn_anim1.startAnimation(scale);
                    animContainer.bringToFront();
                }else{
                    anim1 =false;
                    btn_anim1.clearAnimation();
                }
            }
        });
        btn_anim2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!anim2){
                    btn_anim2.startAnimation(translate);
                }else{
                    btn_anim2.clearAnimation();
                }
                anim2=!anim2;

            }
        });

        btn_anim3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!anim3){
                    objAnim.start();
                }else{
                    objAnim.reverse();
                }
//                android.util.Log.d("XXXX","left:"+btn_anim3.getLeft()+",top:"+btn_anim3.getTop());
                anim3 =!anim3;
                //btn_anim3.animate().x(0).y(0);
            }
        });
        final ViewTreeObserver.OnGlobalLayoutListener callback=new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                Log.d("XXXXani1","x:"+btn_anim1.getX()+",width:"+btn_anim1.getMeasuredWidth()+",left:"+btn_anim1.getLeft()+",right:"+btn_anim1.getRight());
//                Log.d("XXXXani1","y:"+btn_anim1.getY()+",height:"+btn_anim1.getMeasuredHeight()+",top:"+btn_anim1.getTop()+",bottom:"+btn_anim1.getBottom());
//                Log.d("XXXXani2","x:"+btn_anim2.getX()+",width:"+btn_anim2.getMeasuredWidth()+",left:"+btn_anim2.getLeft()+",right:"+btn_anim2.getRight());
//                Log.d("XXXXani2","y:"+btn_anim2.getY()+",height:"+btn_anim2.getMeasuredHeight()+",top:"+btn_anim2.getTop()+",bottom:"+btn_anim2.getBottom());
                float left=btn_anim1.getX();
                float top=btn_anim1.getTop();
                float right=btn_anim1.getRight();
                float bottom=btn_anim1.getBottom();
                int width=btn_anim1.getMeasuredWidth();
                int height=btn_anim1.getMeasuredHeight();
                scale = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f, Animation.ABSOLUTE,0, Animation.ABSOLUTE,0);
                scale.setDuration(200);
//        scale.setRepeatMode(Animation.REVERSE);
//        scale.setRepeatCount(Animation.INFINITE);
                scale.setFillAfter(true);
                scale.setFillEnabled(true);

                translate=createTranslateAnimation(-btn_anim2.getWidth()/2,btn_anim2.getWidth()/2,0,btn_anim2.getHeight()/2);
                // note : the viewAnimation's location is related to view's left and top,
                // but objectAnimator's location is related to view's parent's left and top
                objAnim=ObjectAnimator.ofFloat(btn_anim3,"x",btn_anim3.getLeft(),0f);
                animContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        };
        animContainer.getViewTreeObserver().addOnGlobalLayoutListener(callback);

        Button btnToViewPager=(Button)findViewById(com.ustc.jd.helloworld.R.id.btn_to_view_pager);
        btnToViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ViewPagerActivity.class);
                startActivity(intent);
            }
        });

        findViewById(com.ustc.jd.helloworld.R.id.ll_text_view_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Click me",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private TranslateAnimation createTranslateAnimation(int oldX,int newX,int oldY,int newY) {
        TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldX,
                Animation.ABSOLUTE, newX,
                Animation.ABSOLUTE, oldY,
                Animation.ABSOLUTE, newY);
        translate.setDuration(250);
        translate.setFillEnabled(true);
        translate.setFillAfter(true);
        translate.setInterpolator(new AccelerateDecelerateInterpolator());
        return translate;
    }
}
