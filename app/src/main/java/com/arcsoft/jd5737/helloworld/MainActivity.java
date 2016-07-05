package com.arcsoft.jd5737.helloworld;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private LinearLayout viewContainer;
    private ArrayList<TextView> addedViews=new ArrayList<TextView>();

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
    }
}
