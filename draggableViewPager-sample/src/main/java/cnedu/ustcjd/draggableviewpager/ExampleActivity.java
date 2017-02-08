/**
 * Copyright 2012 
 *
 * Nicolas Desjardins  
 * https://github.com/mrKlar
 *
 * Facilite solutions
 * http://www.facilitesolutions.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package cnedu.ustcjd.draggableviewpager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cnedu.ustcjd.widget.draggableviewpager.DragDropGrid;
import cnedu.ustcjd.widget.draggableviewpager.DraggableViewPager;
import cnedu.ustcjd.widget.draggableviewpager.callbacks.OnPageChangedListener;

public class ExampleActivity extends Activity  {

    private String CURRENT_PAGE_KEY = "CURRENT_PAGE_KEY";

    private static int DELAY_TIME=5000;
    private DraggableViewPager mDrgVpg;
    private ImageView imgNavLeft;
    private ImageView imgNavRight;
    private boolean isNavBarShow=false;
    private static final int REQUEST_LAYOUT = 0;
    private static final int INTERVAL_REQUEST_LAYOUT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        final RelativeLayout contentContainer=(RelativeLayout)findViewById(R.id.content_container);
        contentContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height=contentContainer.getHeight();
                int width=contentContainer.getWidth();
                int mHeight=contentContainer.getMeasuredHeight();
                int mWidth=contentContainer.getMeasuredWidth();
                //Log.d("XXXXXX","width:"+width+",mw:"+mWidth+",height:"+height+",mh:"+mHeight);
                contentContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        mDrgVpg = (DraggableViewPager) findViewById(R.id.gridview);

        ExampleDraggableViewPagerAdapter adapter = new ExampleDraggableViewPagerAdapter(this, mDrgVpg);

        mDrgVpg.setAdapter(adapter);
        mDrgVpg.setClickListener(new DragDropGrid.OnDragDropGridItemClickListener() {
            @Override
            public void onClick(View v,int page,int item) {
                //Toast.makeText(this, String.format("Clicked View(%1$s,%2$s)",page+1,item+1), Toast.LENGTH_SHORT).show();
                showNavBar();
            }
            @Override
            public void onDoubleClick(View v,int page,int item) {
                //Toast.makeText(this, String.format("Double Clicked View(%1$s,%2$s)",page+1,item+1), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFullScreenChange(View view, int page, int item, boolean isFullScreen) {
                //Toast.makeText(ExampleActivity.this, String.format("View(%1$s,%2$s) FullScreen:%3$s",page+1,item+1,isFullScreen), Toast.LENGTH_SHORT).show();
                if(isFullScreen){
                    hideNavBar();
                    view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                } else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                }

            }
        });
        mDrgVpg.setOnItemAnimationListener(new DragDropGrid.OnDragDropGridItemAnimationListener() {
            @Override
            public void onDraggedViewAnimationStart(View view, int page, int item) {
                //android.util.Log.d("XXXX","drag start page:"+page+",item:"+item);
            }

            @Override
            public void onDraggedViewAnimationEnd(View view, int page, int item) {
                //android.util.Log.d("XXXX","drag end page:"+page+",item:"+item);
            }

            @Override
            public void onFullScreenChangeAnimationStart(View view, int page, int item, boolean toFullScreen) {
                //android.util.Log.d("XXXX","fullscreen start page:"+page+",item:"+item);
            }

            @Override
            public void onFullScreenChangeAnimationEnd(View view, int page, int item, boolean toFullScreen) {
                //android.util.Log.d("XXXX","fullscreen end page:"+page+",item:"+item);
            }
        });
        mDrgVpg.setOnPageChangedListener(new OnPageChangedListener() {
            @Override
            public void onPageChanged(DraggableViewPager draggableViewPager, int newPageNumber) {
                if(isNavBarShow)showNavBar();
            }
        });

        mDrgVpg.setBackgroundColor(Color.BLACK);

        mDrgVpg.setDragEnabled(true);
        mDrgVpg.setItemDoubleClickFullScreenEnabled(true);
//        mDrgVpg.setPageScrollAnimationEnabled(true);
//        mDrgVpg.setPageScrollSpeed(500);
        mDrgVpg.setPageScrollAnimationEnabled(false);

        imgNavLeft=(ImageView)findViewById(R.id.navi_bar_left);
        imgNavLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrgVpg.scrollLeft();
            }
        });
        imgNavRight=(ImageView)findViewById(R.id.navi_bar_right);
        imgNavRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrgVpg.scrollRight();
            }
        });

        // simulate a loop layout request
        mHandler.sendEmptyMessageDelayed(REQUEST_LAYOUT, INTERVAL_REQUEST_LAYOUT);
    }
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_LAYOUT:
                    //mDrgVpg.requestGridLayout();
                    //sendEmptyMessageDelayed(REQUEST_LAYOUT, INTERVAL_REQUEST_LAYOUT);
                    break;
                default:
                    break;
            }
        }
    };
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int savedPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        mDrgVpg.restoreCurrentPage(savedPage);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CURRENT_PAGE_KEY, mDrgVpg.currentPage());
        super.onSaveInstanceState(outState);
    }

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
        if(mDrgVpg.isFullScreen())return;
        mHandler.removeCallbacks(hideNavBarCallback);
        mHandler.postDelayed(hideNavBarCallback,DELAY_TIME);
        if(mDrgVpg.canScrollToPreviousPage()){
            imgNavLeft.setVisibility(View.VISIBLE);
        }else{
            imgNavLeft.setVisibility(View.GONE);
        }
        if(mDrgVpg.canScrollToNextPage()){
            imgNavRight.setVisibility(View.VISIBLE);
        }else{
            imgNavRight.setVisibility(View.GONE);
        }
        isNavBarShow=true;
    }
}
