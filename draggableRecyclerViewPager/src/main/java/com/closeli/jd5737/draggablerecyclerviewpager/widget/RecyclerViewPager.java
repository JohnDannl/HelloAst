package com.closeli.jd5737.draggablerecyclerviewpager.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.closeli.jd5737.draggablerecyclerviewpager.BuildConfig;

import java.lang.reflect.Field;
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

    private static final int ITEM_COUNT_OF_PAGE = 4;
    private RecyclerView.Adapter mViewPagerAdapter;
    private static float mTriggerOffset = 0.25f;
    private float mFlingFactor = 0.15f;
    private boolean mSinglePageFling = false;
    private List<OnPageChangedListener> mOnPageChangedListeners;
    private int mSmoothScrollTargetPosition = -1;
    private int mPositionBeforeScroll = -1;

    boolean mNeedAdjust;
    private int mPositionOnTouchDown = -1;
    private boolean mHasCalledOnPageChanged = true;
    private boolean reverseLayout = false;

    public RecyclerViewPager(Context context) {
        this(context, null);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setNestedScrollingEnabled(false);
    }

    public void setFlingFactor(float flingFactor) {
        mFlingFactor = flingFactor;
    }

    public float getFlingFactor() {
        return mFlingFactor;
    }

    public void setTriggerOffset(float triggerOffset) {
        mTriggerOffset = triggerOffset;
    }

    public float getTriggerOffset() {
        return mTriggerOffset;
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
        boolean flinging = super.fling((int) (velocityX * mFlingFactor), (int) (velocityY * mFlingFactor));
        if (flinging && getLayoutManager().canScrollHorizontally()) {
            adjustFlingPosition(velocityX);
            Log.d(TAG, "fling, velocityX:" + velocityX + ",velocityY:" + velocityY
                    + ",minVx:" + getMinFlingVelocity() + ",maxVx:" + getMaxFlingVelocity());
        } else {
            Log.d(TAG, "no fling, velocityX:" + velocityX + ",velocityY:" + velocityY
                    + ",minVx:" + getMinFlingVelocity() + ",maxVx:" + getMaxFlingVelocity());
        }
        return flinging;
    }

    @Override
    public void smoothScrollToPosition(int position) {
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
            Log.d(TAG,"Is not LinearLayoutManage");
            super.smoothScrollToPosition(position);
        }
    }

    @Override
    public void scrollToPosition(int position) {
        if (DEBUG) {
            Log.d(TAG, "scrollToPosition:" + position);
        }
        mPositionBeforeScroll = getTopLeftChildPosition();
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
            targetPosition = Math.min(Math.max(targetPosition, 0), mViewPagerAdapter.getItemCount() - 1);

            if (DEBUG) {
                Log.d(TAG,"adjustFlingPosition:" + targetPosition);
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
            if (DEBUG) {
                Log.d(TAG, "dispatchTouchEvent Position:" + mPositionOnTouchDown);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // recording the max/min value in touch track
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == SCROLL_STATE_DRAGGING) {
            Log.d(TAG,"onScrollStateChange : dragging");
            mNeedAdjust = true;
            View child = ViewUtils.getTopLeftChild(this);
            if (child != null) {
                if (mHasCalledOnPageChanged) {
                    // While rvp is scrolling, mPositionBeforeScroll will be previous value.
                    mPositionBeforeScroll = getChildLayoutPosition(child);
                    mHasCalledOnPageChanged = false;
                }
            } else {
                mPositionBeforeScroll = -1;
            }
        } else if (state == SCROLL_STATE_SETTLING) {
            Log.d(TAG,"onScrollStateChange : setting");
            mNeedAdjust = false;
        } else if (state == SCROLL_STATE_IDLE) {
            Log.d(TAG,"onScrollStateChange : idle");
            if (mNeedAdjust) {
                View targetView = ViewUtils.getTopCenterChild(this);
                if (targetView != null) {
                    int targetPosition = getChildAdapterPosition(targetView);
                    targetPosition = adjustTargetPosition(targetView,targetPosition);
                    smoothScrollToPosition(safeTargetPosition(targetPosition, mViewPagerAdapter.getItemCount()));
                    mNeedAdjust = false;
                }
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
    private int adjustTargetPosition(View targetView, int targetPosition) {
        int rvMid = this.getLeft() + this.getWidth() / 2;
        int triggerSpan = (int) (mTriggerOffset * this.getWidth());
        if (targetPosition % ITEM_COUNT_OF_PAGE == 0) {      // the top-left one, scroll to left
            if (rvMid - targetView.getLeft() < triggerSpan) {
                return targetPosition - ITEM_COUNT_OF_PAGE;
            }
        } else if (targetPosition % ITEM_COUNT_OF_PAGE == 2){        // the top-right one, scroll to right
            if (targetView.getRight() - rvMid < triggerSpan) {
                return (targetPosition + ITEM_COUNT_OF_PAGE) / ITEM_COUNT_OF_PAGE * ITEM_COUNT_OF_PAGE;
            }
        }
        return targetPosition / ITEM_COUNT_OF_PAGE * ITEM_COUNT_OF_PAGE; // keeps origin location
    }

    private int getFlingCount(int velocity, int cellSize) {
        if (velocity == 0) {
            return 0;
        }
        int sign = velocity > 0 ? 1 : -1;
        return (int) (sign * Math.ceil((velocity * sign * mFlingFactor / cellSize)
                - mTriggerOffset));
    }

    private int safeTargetPosition(int position, int count) {
        if (position < 0) {
            return 0;
        }
        if (position >= count) {
            return count - 1;
        }
        return position;
    }

    public interface OnPageChangedListener {
        void OnPageChanged(int oldPosition, int newPosition);
    }

}
