package cnedu.ustc.jd.draggablerecyclerviewpager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import cnedu.ustc.jd.draggablerecyclerviewpager.widget.DraggableItemTouchHelperCallback;
import cnedu.ustc.jd.draggablerecyclerviewpager.widget.RecyclerViewPager;

public class MainActivity extends Activity {
    private static final String TAG = "RecyclerViewPagerMain";
    private static int COLUMN_SIZE = 2;
    private static int DELAY_TIME = 5000;
    private ImageView imgNavLeft;
    private ImageView imgNavRight;
    private boolean isNavBarShow = false;
    private RecyclerViewPager mRecyclerViewPager;
    private RecyclerViewPagerAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(com.ustc.jd5737.draggablerecyclerviewpager.R.layout.activity_main);

        mRecyclerViewPager = (RecyclerViewPager) findViewById(com.ustc.jd5737.draggablerecyclerviewpager.R.id.recycler_view_pager);
        mRecyclerViewPager.setHasFixedSize(true);
        mAdapter = new RecyclerViewPagerAdapter(this);
        mRecyclerViewPager.setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_SIZE, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerViewPager.setLayoutManager(layoutManager);
        mRecyclerViewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
                if(isNavBarShow) {
                    showNavBar();
                }
            }
        });

        mRecyclerViewPager.setOnClickListener(new RecyclerViewPager.OnItemClickListener() {
            @Override
            public void onItemClick(int childIndex, View child) {
                //Toast.makeText(MainActivity.this,"click:" + childIndex, Toast.LENGTH_SHORT).show();
                showNavBar();
            }

            @Override
            public void onItemDoubleClick(int childIndex, View child) {
                int offset = mRecyclerViewPager.computeHorizontalScrollOffset();
                Log.d(TAG, "offset:" + offset + ",left:" + child.getLeft() + ",top:" + child.getTop());
                Toast.makeText(MainActivity.this,"Double click:" + childIndex, Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.Callback itemTouchCallback = new DraggableItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerViewPager);

        imgNavLeft=(ImageView)findViewById(com.ustc.jd5737.draggablerecyclerviewpager.R.id.navi_bar_left);
        imgNavLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewPager.scrollLeft();
            }
        });
        imgNavRight=(ImageView)findViewById(com.ustc.jd5737.draggablerecyclerviewpager.R.id.navi_bar_right);
        imgNavRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewPager.scrollRight();
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
        if(mRecyclerViewPager.isFullScreen())return;
        mHandler.removeCallbacks(hideNavBarCallback);
        mHandler.postDelayed(hideNavBarCallback,DELAY_TIME);
        if(mRecyclerViewPager.canScrollLeft()){
            imgNavLeft.setVisibility(View.VISIBLE);
        }else{
            imgNavLeft.setVisibility(View.GONE);
        }
        if(mRecyclerViewPager.canScrollRight()){
            imgNavRight.setVisibility(View.VISIBLE);
        }else{
            imgNavRight.setVisibility(View.GONE);
        }
        isNavBarShow=true;
    }
}
