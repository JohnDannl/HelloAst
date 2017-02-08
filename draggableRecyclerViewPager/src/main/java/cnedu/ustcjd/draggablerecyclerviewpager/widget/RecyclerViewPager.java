package cnedu.ustcjd.draggablerecyclerviewpager.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import cnedu.ustcjd.draggablerecyclerviewpager.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewPager
 *
 * @author Green
 */
public class RecyclerViewPager extends RecyclerView {
    private static final String TAG = "RecyclerViewPager";
    public static final boolean DEBUG = BuildConfig.DEBUG;

    private OnItemClickListener mItemClickListener = null;

    public static final int ITEM_COUNT_OF_PAGE = 4;
    private RecyclerView.Adapter mViewPagerAdapter;
    private static final float TRIGGER_OFFSET = 0.25f;
    private static final float FLING_FACTOR = 0.15f;
    private boolean mSinglePageFling = false;
    private List<OnPageChangedListener> mOnPageChangedListeners;
    private int mSmoothScrollTargetPosition = 0;
    private int mPositionBeforeScroll = 0;

    boolean mNeedAdjust;
    private int mPositionOnTouchDown = -1;
    private boolean mHasCalledOnPageChanged = true;
    private boolean reverseLayout = false;
    private Context mContext;
    private GestureDetector mGestureDetector;

    private static int displayWidth = 1280;     // Initializes to 720P
    private static int displayHeight = 720;
    private int mFullScreenItem = -1;

    public RecyclerViewPager(Context context) {
        this(context, null);
        mContext = context;
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setNestedScrollingEnabled(false);
        mContext = context;
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(mContext,new OnClickGestureListener());
        final WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setSinglePageFling(boolean singlePageFling) {
        mSinglePageFling = singlePageFling;
    }

    public boolean isSinglePageFling() {
        return mSinglePageFling;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mViewPagerAdapter = adapter;
        super.setAdapter(mViewPagerAdapter);
    }

    @Override
    public void swapAdapter(Adapter adapter, boolean removeAndRecycleExistingViews) {
        mViewPagerAdapter = adapter;
        super.swapAdapter(mViewPagerAdapter, removeAndRecycleExistingViews);
    }

    @Override
    public Adapter getAdapter() {
        return mViewPagerAdapter;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);

        if (layout instanceof LinearLayoutManager) {
            reverseLayout = ((LinearLayoutManager) layout).getReverseLayout();
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        boolean flinging = super.fling((int) (velocityX * FLING_FACTOR), (int) (velocityY * FLING_FACTOR));
        if (flinging && getLayoutManager().canScrollHorizontally()) {
            adjustFlingPosition(velocityX);
            Log.d(TAG, "fling:" + flinging +", velocityX:" + velocityX + ",velocityY:" + velocityY
                    + ",minVx:" + getMinFlingVelocity() + ",maxVx:" + getMaxFlingVelocity());
        }
        return flinging;
    }

    @Override
    public void smoothScrollToPosition(int position) {
        if (isLayoutFrozen()) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "smoothScrollToPosition:" + position);
        }
        mSmoothScrollTargetPosition = position;

        if (getLayoutManager() != null && (getLayoutManager() instanceof LinearLayoutManager)) {
            // exclude item decoration
            LinearSmoothScroller linearSmoothScroller =
                    new LinearSmoothScroller(getContext()) {
                        @Override
                        public PointF computeScrollVectorForPosition(int targetPosition) {
                            if (getLayoutManager() == null) {
                                return null;
                            }
                            return ((LinearLayoutManager) getLayoutManager())
                                    .computeScrollVectorForPosition(targetPosition);
                        }

                        @Override
                        protected void onTargetFound(View targetView, State state, Action action) {
                            if (getLayoutManager() == null) {
                                return;
                            }
                            // scrolls to target position and aligns to left of parent view
                            int dx = calculateDxToMakeVisible(targetView,
                                    SNAP_TO_START);
                            int dy = calculateDyToMakeVisible(targetView,
                                    getVerticalSnapPreference());
                            if (dx > 0) {
                                dx = dx - getLayoutManager()
                                        .getLeftDecorationWidth(targetView);
                            } else {
                                dx = dx + getLayoutManager()
                                        .getRightDecorationWidth(targetView);
                            }
                            if (dy > 0) {
                                dy = dy - getLayoutManager()
                                        .getTopDecorationHeight(targetView);
                            } else {
                                dy = dy + getLayoutManager()
                                        .getBottomDecorationHeight(targetView);
                            }
                            final int distance = (int) Math.sqrt(dx * dx + dy * dy);
                            final int time = calculateTimeForDeceleration(distance);
                            if (time > 0) {
                                action.update(-dx, -dy, time, mDecelerateInterpolator);
                            }
                        }
                    };
            linearSmoothScroller.setTargetPosition(position);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            getLayoutManager().startSmoothScroll(linearSmoothScroller);
        } else {
            Log.d(TAG,"Is not LinearLayoutManager");
            super.smoothScrollToPosition(position);
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (DEBUG) {
            Log.d(TAG, "scrollToPosition:" + position);
        }
        mSmoothScrollTargetPosition = position;
        super.scrollToPosition(position);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                if (mSmoothScrollTargetPosition >= 0 && mSmoothScrollTargetPosition < mViewPagerAdapter.getItemCount()) {
                    if (mOnPageChangedListeners != null) {
                        for (OnPageChangedListener onPageChangedListener : mOnPageChangedListeners) {
                            if (onPageChangedListener != null) {
                                onPageChangedListener.OnPageChanged(mPositionBeforeScroll, getTopLeftChildPosition());
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     *  Gets item position on the top and left of viewpager
     * @return
     */
    public int getTopLeftChildPosition() {
        return ViewUtils.getTopLeftChildPosition(this);
    }

    /***
     * Adjusts position before Touch event complete and fling action start.
     */
    protected void adjustFlingPosition(int velocityX) {
        if (reverseLayout) velocityX *= -1;

        int childCount = getChildCount();
        if (childCount > 0) {
            int curPosition = getTopLeftChildPosition();
            int pageWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int flingCount = getFlingCount(velocityX, pageWidth);
            int targetPosition = (curPosition / ITEM_COUNT_OF_PAGE + flingCount) * ITEM_COUNT_OF_PAGE;
            if (mSinglePageFling) {
                flingCount = Math.max(-1, Math.min(1, flingCount));
                targetPosition = (mPositionOnTouchDown / ITEM_COUNT_OF_PAGE + flingCount) * ITEM_COUNT_OF_PAGE;
            }
            smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter.getItemCount()));
        }
    }

    public void addOnPageChangedListener(OnPageChangedListener listener) {
        if (mOnPageChangedListeners == null) {
            mOnPageChangedListeners = new ArrayList<>();
        }
        mOnPageChangedListeners.add(listener);
    }

    public void removeOnPageChangedListener(OnPageChangedListener listener) {
        if (mOnPageChangedListeners != null) {
            mOnPageChangedListeners.remove(listener);
        }
    }

    public void clearOnPageChangedListeners() {
        if (mOnPageChangedListeners != null) {
            mOnPageChangedListeners.clear();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && getLayoutManager() != null) {
            mPositionOnTouchDown = getTopLeftChildPosition();
           /* if (DEBUG) {
                Log.d(TAG, "dispatchTouchEvent Position:" + mPositionOnTouchDown);
            }*/
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // Passes all the motion events to the gesture detector instance
        mGestureDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_DRAGGING) {
            //Log.d(TAG,"onScrollStateChange : dragging");
            mNeedAdjust = true;
            View child = ViewUtils.getTopLeftChild(this);
            if (child != null) {
                if (mHasCalledOnPageChanged) {
                    // While rvp is scrolling, mPositionBeforeScroll will be previous value.
                    mPositionBeforeScroll = getChildLayoutPosition(child);
                    mHasCalledOnPageChanged = false;
                }
            } else {
                mPositionBeforeScroll = 0;
            }
        } else if (state == SCROLL_STATE_SETTLING) {
            //Log.d(TAG, "onScrollStateChange : setting");
            mNeedAdjust = false;
        } else if (state == SCROLL_STATE_IDLE) {
            //Log.d(TAG, "onScrollStateChange : idle");
            if (mNeedAdjust) {
                adjustScrollPosition();
                mNeedAdjust = false;
            }

            if (mSmoothScrollTargetPosition != mPositionBeforeScroll) {
                if (DEBUG) {
                    Log.d(TAG, "onPageChanged position:" + mPositionBeforeScroll  + " to " + mSmoothScrollTargetPosition);
                }
                if (mOnPageChangedListeners != null) {
                    for (OnPageChangedListener onPageChangedListener : mOnPageChangedListeners) {
                        if (onPageChangedListener != null) {
                            onPageChangedListener.OnPageChanged(mPositionBeforeScroll, mSmoothScrollTargetPosition);
                        }
                    }
                }
                mHasCalledOnPageChanged = true;
                mPositionBeforeScroll = mSmoothScrollTargetPosition;
            }
        }
    }

    /**
     * Adjusts the display page according to the centerX item and scrolling direction
     */
    public void adjustScrollPosition() {
        View targetView = ViewUtils.getTopCenterChild(this);
        if (targetView != null) {
            int targetPosition = getChildAdapterPosition(targetView);
            targetPosition = adjustTargetPosition(targetView,targetPosition);
            smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter.getItemCount()));
        }
    }
    private int adjustTargetPosition(View targetView, int targetPosition) {
        int rvMid = this.getLeft() + this.getWidth() / 2;
        int triggerSpan = (int) (TRIGGER_OFFSET * this.getWidth());
        // whether is in the same page
        boolean isInSamePage = mPositionBeforeScroll / ITEM_COUNT_OF_PAGE == targetPosition / ITEM_COUNT_OF_PAGE;
        boolean isToRight = mPositionBeforeScroll / ITEM_COUNT_OF_PAGE < targetPosition / ITEM_COUNT_OF_PAGE;
        boolean isToLeft = mPositionBeforeScroll / ITEM_COUNT_OF_PAGE > targetPosition / ITEM_COUNT_OF_PAGE;
        boolean targetOnLeft = targetPosition % ITEM_COUNT_OF_PAGE == 0;
        boolean targetOnRight = targetPosition % ITEM_COUNT_OF_PAGE == 2;
        if (isInSamePage
                || (isToRight && targetOnRight)
                || (isToLeft && targetOnLeft)) {
            if (targetOnLeft) {      // the top-left one, scroll to left
                if (rvMid - targetView.getLeft() < triggerSpan) {
                    return targetPosition - ITEM_COUNT_OF_PAGE;
                }
            } else if (targetOnRight){        // the top-right one, scroll to right
                if (targetView.getRight() - rvMid < triggerSpan) {
                    return (targetPosition + ITEM_COUNT_OF_PAGE) / ITEM_COUNT_OF_PAGE * ITEM_COUNT_OF_PAGE;
                }
            }
        }
        return targetPosition / ITEM_COUNT_OF_PAGE * ITEM_COUNT_OF_PAGE; // stays in the same page
    }


    private int getFlingCount(int velocity, int cellSize) {
        if (velocity == 0) {
            return 0;
        }
        int sign = velocity > 0 ? 1 : -1;
        return (int) (sign * Math.ceil((velocity * sign * FLING_FACTOR / cellSize)
                - TRIGGER_OFFSET));
    }

    private int safeTargetPosition(int position, int count) {
       return  Math.min(Math.max(position, 0), mViewPagerAdapter.getItemCount() - 1);
    }

    public boolean canScrollLeft() {
        if (mSmoothScrollTargetPosition - ITEM_COUNT_OF_PAGE >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canScrollRight() {
        if (mSmoothScrollTargetPosition + ITEM_COUNT_OF_PAGE < mViewPagerAdapter.getItemCount()) {
            return true;
        } else {
            return false;
        }
    }

    public void scrollLeft() {
        int targetPosition = mSmoothScrollTargetPosition - ITEM_COUNT_OF_PAGE;
        smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter.getItemCount()));
    }

    public void scrollRight() {
        int targetPosition = mSmoothScrollTargetPosition + ITEM_COUNT_OF_PAGE;
        smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter.getItemCount()));
    }

    public boolean isFullScreen() {
        return false;
    }

    private void onItemDoubleClick(int childIndex, View child) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemDoubleClick(childIndex, child);
        }
    }

    private void onItemClick(int childIndex, View child) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(childIndex, child);
        }
    }

    class OnClickGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return  true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getRawX();
            float y = e.getRawY();
            View child = findChildViewUnder(x, y);
            final int childIndex = getChildLayoutPosition(child);
            onItemClick(childIndex, child);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getRawX();
            float y = e.getRawY();
            View child = findChildViewUnder(x, y);
            final int childIndex = getChildLayoutPosition(child);
            if (mFullScreenItem == -1) {
                mFullScreenItem = childIndex;
                ViewCompat.setElevation(child, 1);
                //propertyAnimationToFullScreen(child);
                viewAnimationToFullScreen(child, childIndex);
                //extendToFullScreen(child);
            } else {
                mFullScreenItem = -1;
                viewAnimationToNormalScreen(child);
                //shrinkToNormalScreen(child);
                ViewCompat.setElevation(child, 0);

            }
            onItemDoubleClick(childIndex, child);
            return false;
        }
    }

    private void propertyAnimationToFullScreen(View child) {
        child.animate().scaleX(2.0f).scaleY(2.0f).start();
    }

    private void viewAnimationToFullScreen(View child, int childIndex) {
        int pivotX = 0;
        int pivotY = 0;
        switch(childIndex % ITEM_COUNT_OF_PAGE) {
            case 0:
                pivotX = 0;
                pivotY = 0;
                break;
            case 1:
                pivotX = 0;
                pivotY = child.getHeight();
                break;
            case 2:
                pivotX = child.getWidth();
                pivotY = 0;
                break;
            case 3:
                pivotX = child.getWidth();
                pivotY = child.getHeight();
                break;
        }
        ScaleAnimation scale = new ScaleAnimation(1f, 2.0f, 1f, 2.0f, Animation.ABSOLUTE, pivotX, Animation.ABSOLUTE, pivotY);
        scale.setDuration(200);
        scale.setFillAfter(true);
        scale.setFillEnabled(true);
        child.clearAnimation();
        child.startAnimation(scale);
    }

    private void viewAnimationToNormalScreen(View child) {
        child.clearAnimation();
        /*int left = 0;
        int top = 0;
        ScaleAnimation scale = new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f, Animation.ABSOLUTE, left, Animation.ABSOLUTE, top);
        scale.setDuration(200);
        scale.setFillAfter(true);
        scale.setFillEnabled(true);
        child.clearAnimation();
        child.startAnimation(scale);*/
    }

    private void extendToFullScreen(View child) {
        RecyclerView.LayoutParams layoutParams =(RecyclerView.LayoutParams) child.getLayoutParams();
        layoutParams.width = displayWidth;
        layoutParams.height = displayHeight;
        child.setLayoutParams(layoutParams);
    }

    private void shrinkToNormalScreen(View child) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        layoutParams.width = displayWidth / 2;
        layoutParams.height = displayHeight / 2;
        child.setLayoutParams(layoutParams);
    }

    public interface OnPageChangedListener {
        void OnPageChanged(int oldPosition, int newPosition);
    }

    public interface OnItemClickListener {
        void onItemClick(int childIndex, View child);
        void onItemDoubleClick(int childIndex, View child);
    }
}
