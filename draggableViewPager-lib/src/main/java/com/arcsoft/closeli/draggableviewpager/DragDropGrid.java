package com.arcsoft.closeli.draggableviewpager;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DragDropGrid extends ViewGroup implements OnTouchListener, OnLongClickListener {

    public static int ROW_HEIGHT = 300;
    private static int DRAGGED_MOVE_ANIMATION_DURATION = 200;
    private static int DRAGGED_ZOOM_IN_ANIMATION_DURATION = 200;
    private static int FULLSCREEN_ANIMATION_DURATION=100;
    private static final long DOUBLE_CLICK_INTERVAL = 250; // in millis
    private boolean hasDoubleClick=false;
    private long lastClickTime=0L;
    private int lastClickItem=-1;
    private int fullScreenItem =-1;
    private boolean hasFullScreen=false;
    private boolean enableDrag=true;
    private boolean enableDragAnim=false;
    private boolean enableFullScreen=false;
    private static final boolean noStatusBar=true;

    private static int EGDE_DETECTION_MARGIN = 35;
    private DraggableViewPagerAdapter adapter;
    private OnDragDropGridItemClickListener onItemClickListener = null;
    private OnDragDropGridItemAnimationListener mItemAnimationListener=null;
    private ViewPagerContainer container;
    private List<View> views = new ArrayList<View>();
    private int gridPageWidth = 0;
    private int dragged = -1;
    private int columnWidthSize;
    private int rowHeightSize;
    private int computedColumnCount;
    private int computedRowCount;
    private LockableScrollView lockableScrollView;

    private int initialX;
    private int initialY;
    private boolean movingView;
    private int lastTarget = -1;
    private boolean wasOnEdgeJustNow = false;
    private Timer edgeScrollTimer;
    private int lastTouchX;
    private int lastTouchY;

    /**
     * The width of the screen.
     */
    private int displayWidth;
    /**
     * The height of the screen.
     */
    private int displayHeight;
    private int measuredHeight;

    public DragDropGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DragDropGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragDropGrid(Context context) {
        super(context);
        init();
    }

    public DragDropGrid(Context context, AttributeSet attrs, int defStyle, DraggableViewPagerAdapter adapter, ViewPagerContainer container) {
        super(context, attrs, defStyle);
        this.adapter = adapter;
        this.container = container;
        init();
    }

    public DragDropGrid(Context context, AttributeSet attrs, DraggableViewPagerAdapter adapter, ViewPagerContainer container) {
        super(context, attrs);
        this.adapter = adapter;
        this.container = container;
        init();
    }

    public DragDropGrid(Context context, DraggableViewPagerAdapter adapter, ViewPagerContainer container) {
        super(context);
        this.adapter = adapter;
        this.container = container;
        init();
    }

    private void init() {
        if (isInEditMode() && adapter == null) {
            useEditModeAdapter();
        }
        getDisplayDimensions();
        setOnTouchListener(this);
        setOnLongClickListener(this);
    }

    private void getDisplayDimensions() {
        final WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        display.getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;
        if(noStatusBar){
            ROW_HEIGHT=displayHeight/2;
        }else{
            int statusHeight=getStatusBarHeight();
            ROW_HEIGHT=(displayHeight-statusHeight)/2;
        }
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private void useEditModeAdapter() {
        adapter = new DraggableViewPagerAdapter() {

            @Override
            public View view(int page, int index) {
                return null;
            }

            @Override
            public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {

            }

            @Override
            public int rowCount() {
                return -1;
            }

            @Override
            public void printLayout() {

            }

            @Override
            public int pageCount() {
                return -1;
            }

            @Override
            public void moveItemToPreviousPage(int pageIndex, int itemIndex) {

            }

            @Override
            public void moveItemToNextPage(int pageIndex, int itemIndex) {

            }

            @Override
            public int itemCountInPage(int page) {
                return 0;
            }

            @Override
            public void deleteItem(int pageIndex, int itemIndex) {

            }

            @Override
            public int columnCount() {
                return 0;
            }

            @Override
            public int getPageWidth(int page) {
                return 0;
            }

            @Override
            public Object getItemAt(int page, int index) {
                return null;
            }

            @Override
            public boolean disableZoomAnimationsOnChangePage() {
                return false;
            }
        };
    }

    public void setAdapter(DraggableViewPagerAdapter adapter) {
        this.adapter = adapter;
        addChildViews();
    }

    public void setOnItemClickListener(OnDragDropGridItemClickListener l) {
        onItemClickListener = l;
    }

    public void setOnItemAnimationListener(OnDragDropGridItemAnimationListener listener){
        mItemAnimationListener=listener;
    }
    public void setDragEnabled(boolean enabled){
        this.enableDrag=enabled;
    }

    public void setDragZoomInAnimEnabled(boolean enabled){
        enableDragAnim=enabled;
    }

    public void setItemDoubleClickFullScreenEnabled(boolean enabled){
        enableFullScreen=enabled;
    }

    private void addChildViews() {
        for (int page = 0; page < adapter.pageCount(); page++) {
            for (int item = 0; item < adapter.itemCountInPage(page); item++) {
                View v = adapter.view(page, item);
                v.setTag(adapter.getItemAt(page, item));
                //final LayoutParams layoutParams = new LayoutParams((displayWidth - getPaddingLeft() - getPaddingRight())/2, ROW_HEIGHT - getPaddingLeft() - getPaddingRight());
                LayoutParams layoutParams = new LayoutParams((displayWidth - getPaddingLeft() - getPaddingRight())/adapter.columnCount(),ROW_HEIGHT);
                addView(v, layoutParams);
                views.add(v);
            }
        }
    }

    public void reloadViews() {
        for (int page = 0; page < adapter.pageCount(); page++) {
            for (int item = 0; item < adapter.itemCountInPage(page); item++) {
                if (indexOfItem(page, item) == -1) {
                    View v = adapter.view(page, item);
                    v.setTag(adapter.getItemAt(page, item));
                    addView(v);
                }
            }
        }
    }

    public int indexOfItem(int page, int index) {
        Object item = adapter.getItemAt(page, index);

        for (int i = 0; i < this.getChildCount(); i++) {
            View v = this.getChildAt(i);
            if (item.equals(v.getTag()))
                return i;
        }
        return -1;
    }

    public void removeItem(int page, int index) {
        Object item = adapter.getItemAt(page, index);
        for (int i = 0; i < this.getChildCount(); i++) {
            View v = (View) this.getChildAt(i);
            if (item.equals(v.getTag())) {
                this.removeView(v);
                return;
            }
        }
    }

    private void cancelAnimations() {
        for (int i = 0; i < getItemViewCount(); i++) {
            View child = getChildAt(i);
            child.clearAnimation();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        return onTouch(null, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(event);
                break;
        }
        if (aViewIsDragged()){
            return true;
        }
        return false;
    }

    private boolean scrollIfNeeded() {
        int height = displayHeight;
        final View draggedView = getDraggedView();
        int hoverHeight = draggedView.getHeight();
        int scrollAmount = 20;


        int[] locations = new int[2];
        draggedView.getLocationOnScreen(locations);
        int y = locations[1];
        if (y <= 0) {
            lockableScrollView.scrollBy(0, -scrollAmount);
            return true;
        }
        if (y + hoverHeight >= height) {
            lockableScrollView.scrollBy(0, scrollAmount);
            return true;
        }

        return false;
    }

    private void touchUp(MotionEvent event) {
        if (!aViewIsDragged()) {
            long clickTime=event.getEventTime();
            final int childIndex=getTargetAtCoor((int) event.getX(), (int) event.getY());
            if(clickTime-lastClickTime<=DOUBLE_CLICK_INTERVAL&&childIndex==lastClickItem){
                hasDoubleClick=true;
                if(enableFullScreen){
                    onItemDoubleClick(childIndex);
                }
            }else{
                hasDoubleClick=false;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!hasDoubleClick){
                            onItemClick(childIndex);
                        }
                    }
                },DOUBLE_CLICK_INTERVAL);
            }
            lastClickTime=clickTime;
            lastClickItem=childIndex;
        } else {
            cancelAnimations();
            cancelEdgeTimer();
            restoreDraggedItem();

            lockableScrollView.setScrollingEnabled(true);
            container.enableScroll();

            movingView = false;
            dragged = -1;
            lastTarget = -1;
        }
    }
    private void onItemClick(int childIndex){
        if (onItemClickListener != null) {
            ItemPosition itemPosition= getItemPositionOf(childIndex);
            View clickedView = getChildView(childIndex);
            if (clickedView != null&&itemPosition!=null){
                onItemClickListener.onClick(clickedView,itemPosition.pageIndex,itemPosition.itemIndex);
            }else{
                onItemClickListener.onClick(null,-1,-1);
            }

        }
    }
    private void onItemDoubleClick(int childIndex){
        ItemPosition itemPosition= getItemPositionOf(childIndex);
        View clickedView = getChildView(childIndex);

        if(!hasFullScreen&&clickedView != null&&itemPosition!=null){
            fullScreenItem =childIndex;
            hasFullScreen=true;
            //extendToFullScreen();
            //extendToFullScreenWithViewAnimation();
            extendToFullScreenWithValueAnimation();
            if(onItemClickListener != null&&clickedView != null&&itemPosition!=null){
                onItemClickListener.onFullScreenChange(clickedView, itemPosition.pageIndex, itemPosition.itemIndex,true);
            }
        }else{
            //shrinkToNormalScreen();
            //shrinkToNormalScreenWithViewAnimation();
            shrinkToNormalScreenWithValueAnimation();
            ItemPosition shrinkItemPosition= getItemPositionOf(fullScreenItem);
            View shrinkItemView = getChildView(fullScreenItem);
            if(onItemClickListener != null&&shrinkItemView != null&&shrinkItemPosition!=null){
                onItemClickListener.onFullScreenChange(shrinkItemView, shrinkItemPosition.pageIndex, shrinkItemPosition.itemIndex,false);
            }
            fullScreenItem = -1;
            hasFullScreen=false;
        }

        if(onItemClickListener != null){
            if (clickedView != null&&itemPosition!=null) {
                onItemClickListener.onDoubleClick(clickedView, itemPosition.pageIndex, itemPosition.itemIndex);
            }else{
                onItemClickListener.onDoubleClick(null, -1, -1);
            }
        }

    }

    public boolean isFullScreen() {
        return hasFullScreen;
    }

    public void exitFullScreen() {
        if (fullScreenItem >= 0 && enableFullScreen) {
            onItemDoubleClick(fullScreenItem);
        }
    }

    private void extendToFullScreen(){
        container.disableScroll();
        lockableScrollView.setScrollingEnabled(false);

        if (fullScreenItem != -1) {
            View fullScreenView = views.get(fullScreenItem);
            LayoutParams vlp=fullScreenView.getLayoutParams();
            vlp.width=displayWidth;
            vlp.height=displayHeight;
            fullScreenView.setLayoutParams(vlp);
            bringFullScreenItemToFront();

            int page=currentPage();
            for(int i=0;i<adapter.itemCountInPage(page);i++){
                int position=positionOfItem(page,i);
                if(position!=fullScreenItem){
                    View childView=getChildView(position);
                    //LayoutParams cvlp=new LayoutParams(0,0);
                    LayoutParams cvlp=childView.getLayoutParams();
                    cvlp.width=0;
                    cvlp.height=0;
                    childView.setLayoutParams(cvlp);
                    //childView.setVisibility(View.GONE);
                }
            }
        }
    }
    private void shrinkToNormalScreen(){
        if (fullScreenItem != -1) {
            int page=currentPage();
            for(int i=0;i<adapter.itemCountInPage(page);i++){
                int position=positionOfItem(page,i);
                if(position!=fullScreenItem){
                    View childView=getChildView(position);
                    LayoutParams cvlp =childView.getLayoutParams();
                    cvlp.width=(displayWidth - getPaddingLeft() - getPaddingRight())/adapter.columnCount();
                    cvlp.height=ROW_HEIGHT;
                    childView.setLayoutParams(cvlp);
                    //childView.setVisibility(View.VISIBLE);
                }
            }

            View fullScreenView = views.get(fullScreenItem);
            LayoutParams vlp =fullScreenView.getLayoutParams();
            vlp.width=(displayWidth - getPaddingLeft() - getPaddingRight())/adapter.columnCount();
            vlp.height=ROW_HEIGHT;
            fullScreenView.setLayoutParams(vlp);
        }

        lockableScrollView.setScrollingEnabled(true);
        container.enableScroll();
    }
    private void extendToFullScreenWithViewAnimation(){
        container.disableScroll();
        lockableScrollView.setScrollingEnabled(false);

        if (fullScreenItem != -1) {
            View fullView=getChildView(fullScreenItem);
            bringFullScreenItemToFront();
            int width=fullView.getMeasuredWidth();
            int height=fullView.getMeasuredHeight();
            int left=0;
            int top=0;
            switch(getItemPositionOf(fullScreenItem).itemIndex){
                case 0:
                    left=0;
                    top=0;
                    break;
                case 1:
                    left=width;
                    top=0;
                    break;
                case 2:
                    left=0;
                    top=height;
                    break;
                case 3:
                    left=width;
                    top=height;
                    break;
                default:
                    break;
            }
            ScaleAnimation scale = new ScaleAnimation(1f, 2.0f, 1f, 2.0f, Animation.ABSOLUTE,left,Animation.ABSOLUTE,top);
            scale.setDuration(FULLSCREEN_ANIMATION_DURATION);
            scale.setFillAfter(true);
            scale.setFillEnabled(true);
            fullView.clearAnimation();
            fullView.startAnimation(scale);
        }
    }
    private void shrinkToNormalScreenWithViewAnimation(){
        if(fullScreenItem!=-1){
            View fullView=getChildView(fullScreenItem);
            //fullView.clearAnimation();
            int width=fullView.getMeasuredWidth();
            int height=fullView.getMeasuredHeight();
            int left=0;
            int top=0;
            switch(getItemPositionOf(fullScreenItem).itemIndex){
                case 0:
                    left=0;
                    top=0;
                    break;
                case 1:
                    left=width;
                    top=0;
                    break;
                case 2:
                    left=0;
                    top=height;
                    break;
                case 3:
                    left=width;
                    top=height;
                    break;
                default:
                    break;
            }
            ScaleAnimation scale = new ScaleAnimation(2f, 1.0f, 2f, 1.0f, Animation.ABSOLUTE,left,Animation.ABSOLUTE,top);
            scale.setDuration(FULLSCREEN_ANIMATION_DURATION);
            scale.setFillAfter(true);
            scale.setFillEnabled(true);
            fullView.clearAnimation();
            fullView.startAnimation(scale);
        }
        lockableScrollView.setScrollingEnabled(true);
        container.enableScroll();
    }
    private void extendToFullScreenWithValueAnimation(){
        container.disableScroll();
        lockableScrollView.setScrollingEnabled(false);

        if (fullScreenItem != -1) {
            final View fullView=getChildView(fullScreenItem);
            bringFullScreenItemToFront();
            final ViewGroup.LayoutParams layoutAnimateScale=fullView.getLayoutParams();
            final ViewGroup.LayoutParams newLayoutParam=new ViewGroup.LayoutParams(displayWidth,displayHeight);
            layoutAnimateScale(layoutAnimateScale,newLayoutParam,fullView,fullScreenItem,true);
        }
    }
    private void layoutAnimateScale( ViewGroup.LayoutParams oldLayoutParam, ViewGroup.LayoutParams newLayoutParam,
                                     final View fullView,final int fullScreenItem,final boolean toFullScreen){
        ValueAnimator layoutAnim=ValueAnimator.ofObject(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                float width=((ViewGroup.LayoutParams)startValue).width
                        +fraction*(((ViewGroup.LayoutParams)endValue).width-((ViewGroup.LayoutParams)startValue).width);
                float height=((ViewGroup.LayoutParams)startValue).height
                        +fraction*(((ViewGroup.LayoutParams)endValue).height-((ViewGroup.LayoutParams)startValue).height);
                ViewGroup.LayoutParams tmpLayoutParam=new ViewGroup.LayoutParams((int)width,(int)height);
                return tmpLayoutParam;
            }
        },oldLayoutParam,newLayoutParam);
        layoutAnim.setDuration(FULLSCREEN_ANIMATION_DURATION).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams tmpLayoutParam=(ViewGroup.LayoutParams)animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParam=fullView.getLayoutParams();
                layoutParam.width=tmpLayoutParam.width;
                layoutParam.height=tmpLayoutParam.height;
                fullView.setLayoutParams(layoutParam);
            }
        });
        layoutAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ItemPosition itemPos= getItemPositionOf(fullScreenItem);
                if(mItemAnimationListener!=null&&itemPos!=null){
                    mItemAnimationListener.onFullScreenChangeAnimationStart(fullView,itemPos.pageIndex,itemPos.itemIndex,toFullScreen);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ItemPosition itemPos= getItemPositionOf(fullScreenItem);
                if(mItemAnimationListener!=null&&itemPos!=null){
                    mItemAnimationListener.onFullScreenChangeAnimationEnd(fullView,itemPos.pageIndex,itemPos.itemIndex,toFullScreen);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        layoutAnim.start();
    }
    private void shrinkToNormalScreenWithValueAnimation(){
        if(fullScreenItem!=-1){
            View fullView=getChildView(fullScreenItem);
            final ViewGroup.LayoutParams layoutAnimateScale=fullView.getLayoutParams();
            final ViewGroup.LayoutParams newLayoutParam=new ViewGroup.LayoutParams(columnWidthSize,rowHeightSize);
            layoutAnimateScale(layoutAnimateScale,newLayoutParam,fullView,fullScreenItem,false);
        }
        lockableScrollView.setScrollingEnabled(true);
        container.enableScroll();
    }
    private void restoreDraggedItem() {
        Point targetCoor=getCoorForIndex(dragged);
        View targetView=getChildView(dragged);
        targetView.layout(targetCoor.x,targetCoor.y,
                targetCoor.x+targetView.getMeasuredWidth(),targetCoor.y+targetView.getMeasuredHeight());
        if(mItemAnimationListener!=null){
            ItemPosition itemPos=getItemPositionOf(dragged);
            mItemAnimationListener.onDraggedViewAnimationEnd(targetView,itemPos.pageIndex,itemPos.itemIndex);
        }
    }

    private void tellAdapterDraggedIsDeleted(Integer newDraggedPosition) {
        ItemPosition position = getItemPositionOf(newDraggedPosition);
        adapter.deleteItem(position.pageIndex, position.itemIndex);
    }

    private void touchDown(MotionEvent event) {
        initialX = (int) event.getRawX();
        initialY = (int) event.getRawY();

        //lastTouchX = (int) event.getRawX() + (currentPage() * gridPageWidth);
        lastTouchX = (int) event.getX();
        lastTouchY = (int) event.getRawY();
    }

    private void touchMove(MotionEvent event) {
        if (movingView && aViewIsDragged()) {
            lastTouchX = (int) event.getX();
            lastTouchY = (int) event.getY();

            ensureThereIsNoArtifact();
            moveDraggedView(lastTouchX, lastTouchY);
            manageSwapPosition(lastTouchX, lastTouchY);
            manageEdgeCoordinates(lastTouchX);
            scrollIfNeeded();
        }
    }

    private void ensureThereIsNoArtifact() {
        invalidate();
    }


    private void moveDraggedView(int x, int y) {
        View childAt = getDraggedView();

        int width = childAt.getMeasuredWidth();
        int height = childAt.getMeasuredHeight();

        int l = x - (1 * width / 2);
        int t = y - (1 * height / 2);

        childAt.layout(l, t, l + width, t + height);
    }

    private void manageSwapPosition(int x, int y) {
        int target = getTargetAtCoor(x, y);
        if (childHasMoved(target) && target != lastTarget) {
            animateGap(target);
            lastTarget = target;
        }
    }

    private void manageEdgeCoordinates(int x) {
        final boolean onRightEdge = onRightEdgeOfScreen(x);
        final boolean onLeftEdge = onLeftEdgeOfScreen(x);

        if (canScrollToEitherSide(onRightEdge, onLeftEdge)) {
            if (!wasOnEdgeJustNow) {
                startEdgeDelayTimer(onRightEdge, onLeftEdge);
                wasOnEdgeJustNow = true;
            }
        } else {
            if (wasOnEdgeJustNow) {
                stopAnimateOnTheEdge();
            }
            wasOnEdgeJustNow = false;
            cancelEdgeTimer();
        }
    }

    private void stopAnimateOnTheEdge() {
        View draggedView = getDraggedView();
        draggedView.clearAnimation();
        animateDragged();
    }

    private void cancelEdgeTimer() {

        if (edgeScrollTimer != null) {
            edgeScrollTimer.cancel();
            edgeScrollTimer = null;
        }
    }

    private void startEdgeDelayTimer(final boolean onRightEdge, final boolean onLeftEdge) {
        if (canScrollToEitherSide(onRightEdge, onLeftEdge)) {
            animateOnTheEdge();
            if (edgeScrollTimer == null) {
                edgeScrollTimer = new Timer();
                scheduleScroll(onRightEdge, onLeftEdge);
            }
        }
    }

    private void scheduleScroll(final boolean onRightEdge, final boolean onLeftEdge) {
        edgeScrollTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (wasOnEdgeJustNow) {
                    wasOnEdgeJustNow = false;
                    post(new Runnable() {
                        @Override
                        public void run() {
                            scroll(onRightEdge, onLeftEdge);
                            cancelAnimations();
                            animateDragged();
                        }
                    });
                }
            }
        }, 400);
    }

    private boolean canScrollToEitherSide(final boolean onRightEdge, final boolean onLeftEdge) {
        return (onLeftEdge && container.canScrollToPreviousPage()) || (onRightEdge && container.canScrollToNextPage());
    }

    private void scroll(boolean onRightEdge, boolean onLeftEdge) {
        cancelEdgeTimer();

        if (onLeftEdge && container.canScrollToPreviousPage()) {
            scrollToPreviousPage();
        } else if (onRightEdge && container.canScrollToNextPage()) {
            scrollToNextPage();
        }
        wasOnEdgeJustNow = false;
    }

    private void scrollToNextPage() {
        if(!aViewIsDragged())return; // error occurs if dragged equals -1
        tellAdapterToMoveItemToNextPage(dragged);
        moveDraggedToNextPage();

        container.scrollRight();
        stopAnimateOnTheEdge();
        lockableScrollView.scrollTo(0, 0);
    }

    private void scrollToPreviousPage() {
        if(!aViewIsDragged())return;
        tellAdapterToMoveItemToPreviousPage(dragged);
        moveDraggedToPreviousPage();

        container.scrollLeft();
        stopAnimateOnTheEdge();
        lockableScrollView.scrollTo(0, 0);
    }

    private void moveDraggedToPreviousPage() {
        final Point draggedViewCoor = getCoorForIndex(dragged);
        int indexFirstElementInCurrentPage = findTheIndexOfFirstElementInCurrentPage();
        int indexOfDraggedOnNewPage = indexFirstElementInCurrentPage - 1;
        final int targetIndex=indexOfDraggedOnNewPage;
        final View targetView=getChildView(targetIndex);
        final Point targetViewCoor = getCoorForIndex(targetIndex);
        Collections.swap(views,targetIndex,dragged);
        dragged=targetIndex;
        animateMoveView(draggedViewCoor,targetViewCoor,targetView);
    }

    private void moveDraggedToNextPage() {
        final Point draggedViewCoor = getCoorForIndex(dragged);
        int indexFirstElementInNextPage = findTheIndexFirstElementInNextPage();
        int indexOfDraggedOnNewPage = indexFirstElementInNextPage;
        int targetIndex=indexOfDraggedOnNewPage;
        final View targetView = getChildView(targetIndex);
        final Point targetViewCoor = getCoorForIndex(targetIndex);
        Collections.swap(views,targetIndex,dragged);
        dragged=targetIndex;
        animateMoveView(draggedViewCoor,targetViewCoor,targetView);
    }

    private int findTheIndexOfFirstElementInCurrentPage() {
        int currentPage = currentPage();
        int indexFirstElementInCurrentPage = 0;
        for (int i = 0; i < currentPage; i++) {
            indexFirstElementInCurrentPage += adapter.itemCountInPage(i);
        }
        return indexFirstElementInCurrentPage;
    }

    private void removeItemChildren(List<View> children) {
        /*for (View child : children) {
            removeView(child);
            views.remove(child);
        }*/
        removeAllViews();
        views.removeAll(children);
    }

    private int findTheIndexLastElementInNextPage() {
        int currentPage = currentPage();
        int indexLastElementInNextPage = 0;
        for (int i = 0; i <= currentPage + 1; i++) {
            indexLastElementInNextPage += adapter.itemCountInPage(i);
        }
        return indexLastElementInNextPage;
    }
    private int findTheIndexFirstElementInNextPage() {
        int currentPage = currentPage();
        int indexFirstElementInNextPage = 0;
        for (int i = 0; i <= currentPage; i++) {
            indexFirstElementInNextPage += adapter.itemCountInPage(i);
        }
        return indexFirstElementInNextPage;
    }

    private boolean onLeftEdgeOfScreen(int x) {
        int currentPage = container.currentPage();

        int leftEdgeXCoor = currentPage * gridPageWidth;
        int distanceFromEdge = x - leftEdgeXCoor;
        return (x > 0 && distanceFromEdge <= EGDE_DETECTION_MARGIN);
    }

    private boolean onRightEdgeOfScreen(int x) {
        int currentPage = container.currentPage();

        int rightEdgeXCoor = (currentPage * gridPageWidth) + gridPageWidth;
        int distanceFromEdge = rightEdgeXCoor - x;
        return (x > (rightEdgeXCoor - EGDE_DETECTION_MARGIN)) && (distanceFromEdge < EGDE_DETECTION_MARGIN);
    }

    private void animateOnTheEdge() {
        if (!adapter.disableZoomAnimationsOnChangePage()) {
            View v = getDraggedView();

            ScaleAnimation scale = new ScaleAnimation(.9f, 1.1f, .9f, 1.1f, v.getMeasuredWidth() * 3 / 4, v.getMeasuredHeight() * 3 / 4);
            scale.setDuration(200);
            scale.setRepeatMode(Animation.REVERSE);
            scale.setRepeatCount(Animation.INFINITE);

            v.clearAnimation();
            v.startAnimation(scale);
        }
    }

    private void animateGap(int targetLocationInGrid) {
        int viewAtPosition = targetLocationInGrid;
        if (viewAtPosition == dragged) {
            return;
        }
        final View targetView = getChildView(viewAtPosition);
        final Point draggedViewCoor = getCoorForIndex(dragged);
        final Point targetViewCoor = getCoorForIndex(viewAtPosition);
        Collections.swap(views,dragged,viewAtPosition);
        tellAdapterToSwapDraggedWithTarget(dragged, viewAtPosition);
        dragged=viewAtPosition;

        /*targetView.layout(draggedViewCoor.x,draggedViewCoor.y,
                draggedViewCoor.x+targetView.getMeasuredWidth(),draggedViewCoor.y+targetView.getMeasuredHeight());*/
        animateMoveView(draggedViewCoor,targetViewCoor,targetView);
    }

    private void animateMoveView(Point draggedViewCoor, Point targetViewCoor, final View targetView){
        ValueAnimator animMove=ValueAnimator.ofObject(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                float x=((Point)startValue).x+fraction*(((Point)endValue).x-((Point)startValue).x);
                float y=((Point)startValue).y+fraction*(((Point)endValue).y-((Point)startValue).y);
                return new Point((int)x,(int)y);
            }
        },targetViewCoor,draggedViewCoor);
        animMove.setDuration(DRAGGED_MOVE_ANIMATION_DURATION).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point layoutCoor=(Point)animation.getAnimatedValue();
                targetView.layout(layoutCoor.x,layoutCoor.y,
                        layoutCoor.x+targetView.getMeasuredWidth(),layoutCoor.y+targetView.getMeasuredHeight());
            }
        });
        animMove.start();
    }

    private Point getCoorForIndex(int index) {
        ItemPosition page = getItemPositionOf(index);

        int row = page.itemIndex / computedColumnCount;
        int col = page.itemIndex - (row * computedColumnCount);

        int x = (currentPage() * gridPageWidth) + (columnWidthSize * col);
        int y = rowHeightSize * row;

        return new Point(x, y);
    }

    private int getTargetAtCoor(int x, int y) {
        int page = currentPage();

        int col = getColumnOfCoordinate(x, page);
        int row = getRowOfCoordinate(y);
        int positionInPage = col + (row * computedColumnCount);

        return positionOfItem(page, positionInPage);
    }

    private int getColumnOfCoordinate(int x, int page) {
        int col = 0;
        int pageLeftBorder = (page) * gridPageWidth;
        for (int i = 1; i <= computedColumnCount; i++) {
            int colRightBorder = (i * columnWidthSize) + pageLeftBorder;
            if (x < colRightBorder) {
                break;
            }
            col++;
        }
        return col;
    }

    private int getRowOfCoordinate(int y) {
        int row = 0;
        for (int i = 1; i <= computedRowCount; i++) {
            if (y < i * rowHeightSize) {
                break;
            }
            row++;
        }
        return row;
    }

    private int currentPage() {
        return container.currentPage();
    }

    private boolean childHasMoved(int position) {
        return position != -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        widthSize = remeasure(widthMode, widthSize);
        int largestSize = getLargestPageSize();

        measuredHeight = adapter.rowCount() * rowHeightSize;//(largestSize+adapter.columnCount()-1)/adapter.columnCount() * rowHeightSize;
        setMeasuredDimension(widthSize * adapter.pageCount(), measuredHeight);
    }

    private int remeasure(int widthMode, int widthSize) {
        widthSize = acknowledgeWidthSize(widthMode, widthSize, displayWidth);
        measureChildren(MeasureSpec.EXACTLY, MeasureSpec.UNSPECIFIED);
        computedColumnCount = adapter.columnCount();
        computedRowCount = 16;
        columnWidthSize = widthSize / adapter.columnCount();
        rowHeightSize = ROW_HEIGHT;
        return widthSize;
    }

    private int getLargestPageSize() {
        int size = 0;
        for (int page = 0; page < adapter.pageCount(); page++) {
            final int currentSize = adapter.itemCountInPage(page);
            if (currentSize > size) {
                size = currentSize;
            }
        }
        return size;
    }


    private int getItemViewCount() {
        return views.size();
    }

    private int acknowledgeWidthSize(int widthMode, int widthSize, int displayWidth) {
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = displayWidth;
        }

        if (adapter.getPageWidth(currentPage()) != 0) {
            widthSize = adapter.getPageWidth(currentPage());
        }

        gridPageWidth = widthSize;
        return widthSize;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //If we don't have pages don't do layout
        if (adapter.pageCount() == 0)
            return;
        int pageWidth = (r-l) / adapter.pageCount();

        for (int page = 0; page < adapter.pageCount(); page++) {
            layoutPage(pageWidth, page);
        }
        if (weWereMovingDragged()) {
            bringDraggedToFront();
        }
        if(fullScreenItem!=-1){
            bringFullScreenItemToFront();
        }
    }

    private boolean weWereMovingDragged() {
        return dragged != -1;
    }

    private void layoutPage(int pageWidth, int page) {
        int col = 0;
        int row = 0;
        for (int childIndex = 0; childIndex < adapter.itemCountInPage(page); childIndex++) {
            layoutAChild(pageWidth, page, col, row, childIndex);
            col++;
            if (col == computedColumnCount) {
                col = 0;
                row++;
            }
        }
    }

    private void layoutAChild(int pageWidth, int page, int col, int row, int childIndex) {
        int position = positionOfItem(page, childIndex);

        View child = views.get(position);

        if(child.getVisibility()==View.GONE)return;

        int left = 0;
        int top = 0;
        if (position == dragged && lastTouchOnEdge()) {
            left = computePageEdgeXCoor(child);
            top = lastTouchY - (child.getMeasuredHeight() / 2);
        } else if(position == fullScreenItem){
            switch(getItemPositionOf(fullScreenItem).itemIndex){
                case 0:
                    left=page*pageWidth;
                    top=0;
                    break;
                case 1:
                    left=page*pageWidth + pageWidth-child.getMeasuredWidth();
                    top=0;
                    break;
                case 2:
                    left=page*pageWidth;
                    top=displayHeight-child.getMeasuredHeight();
                    break;
                case 3:
                    left=page*pageWidth + pageWidth-child.getMeasuredWidth();
                    top=displayHeight-child.getMeasuredHeight();
                    break;
                default:
                    break;
            }
        }else {
            left = (page * pageWidth) + (col * columnWidthSize) + ((columnWidthSize - child.getMeasuredWidth()) / 2);
            top = (row * rowHeightSize) + ((rowHeightSize - child.getMeasuredHeight()) / 2);
        }
        child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
    }

    private boolean lastTouchOnEdge() {
        return onRightEdgeOfScreen(lastTouchX) || onLeftEdgeOfScreen(lastTouchX);
    }

    private int computePageEdgeXCoor(View child) {
        int left;
        left = lastTouchX - (child.getMeasuredWidth() / 2);
        if (onRightEdgeOfScreen(lastTouchX)) {
            left = left - gridPageWidth;
        } else if (onLeftEdgeOfScreen(lastTouchX)) {
            left = left + gridPageWidth;
        }
        return left;
    }

    @Override
    public boolean onLongClick(View v) {
        if (positionForView(v) != -1&&enableDrag&&!isFullScreen()) {
            container.disableScroll();
            lockableScrollView.setScrollingEnabled(false);

            movingView = true;
            dragged = positionForView(v);

            bringDraggedToFront();
            animateDragged();

            return true;
        }

        return false;
    }

    private void bringDraggedToFront() {
        View draggedView = getChildView(dragged);
        draggedView.bringToFront();
    }
    private void bringFullScreenItemToFront(){
        View fullScreenView = getChildView(fullScreenItem);
        fullScreenView.bringToFront();
    }
    private View getDraggedView() {
        return views.get(dragged);
    }

    private void animateDragged() {

        if(mItemAnimationListener!=null){
            ItemPosition itemPos=getItemPositionOf(dragged);
            mItemAnimationListener.onDraggedViewAnimationStart(getChildView(dragged),itemPos.pageIndex,itemPos.itemIndex);
        }
        if(!enableDragAnim)return;
        ScaleAnimation scale = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, displayWidth / 2, ROW_HEIGHT / 2);
        scale.setDuration(DRAGGED_ZOOM_IN_ANIMATION_DURATION);
        scale.setFillAfter(true);
        scale.setFillEnabled(true);

        if (aViewIsDragged()) {
            View draggedView = getDraggedView();
            draggedView.clearAnimation();
            draggedView.startAnimation(scale);
        }
    }

    private boolean aViewIsDragged() {
        return weWereMovingDragged();
    }


    private int positionForView(View v) {
        for (int index = 0; index < getItemViewCount(); index++) {
            View child = getChildView(index);
            if (isPointInsideView(initialX, initialY, child)) {
                return index;
            }
        }
        return -1;
    }

    private View getChildView(int index) {
        if(index>=0&&index<views.size()){
            return views.get(index);
        }else{
            return null;
        }
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        if (pointIsInsideViewBounds(x, y, view, viewX, viewY)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean pointIsInsideViewBounds(float x, float y, View view, int viewX, int viewY) {
        return (x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight()));
    }

    public void setContainer(DraggableViewPager container) {
        this.container = container;
    }

    private int positionOfItem(int pageIndex, int childIndex) {
        int currentGlobalIndex = 0;
        for (int currentPageIndex = 0; currentPageIndex < adapter.pageCount(); currentPageIndex++) {
            int itemCount = adapter.itemCountInPage(currentPageIndex);
            if(pageIndex != currentPageIndex){
                currentGlobalIndex+=itemCount;
                continue;
            }
            for (int currentItemIndex = 0; currentItemIndex < itemCount; currentItemIndex++) {
                if (pageIndex == currentPageIndex && childIndex == currentItemIndex) {
                    return currentGlobalIndex;
                }
                currentGlobalIndex++;
            }
        }
        return -1;
    }

    private ItemPosition getItemPositionOf(int position) {
        int currentGlobalIndex = 0;
        for (int currentPageIndex = 0; currentPageIndex < adapter.pageCount(); currentPageIndex++) {
            int itemCount = adapter.itemCountInPage(currentPageIndex);
            if(currentGlobalIndex+itemCount<=position){
                currentGlobalIndex+=itemCount;
                continue;
            }
            for(int itemIndex=0;itemIndex<itemCount;itemIndex++){
                if(currentGlobalIndex==position){
                    return new ItemPosition(currentPageIndex,itemIndex);
                }
                currentGlobalIndex++;
            }

        }
        return null;
    }

    private void tellAdapterToSwapDraggedWithTarget(int dragged, int target) {
        ItemPosition draggedItemPositionInPage = getItemPositionOf(dragged);
        ItemPosition targetItemPositionInPage = getItemPositionOf(target);
        if (draggedItemPositionInPage != null && targetItemPositionInPage != null) {
            adapter.swapItems(draggedItemPositionInPage.pageIndex, draggedItemPositionInPage.itemIndex, targetItemPositionInPage.itemIndex);
        }
    }

    private void tellAdapterToMoveItemToPreviousPage(int itemIndex) {
        ItemPosition itemPosition = getItemPositionOf(itemIndex);
        adapter.moveItemToPreviousPage(itemPosition.pageIndex, itemPosition.itemIndex);
    }

    private void tellAdapterToMoveItemToNextPage(int itemIndex) {
        ItemPosition itemPosition = getItemPositionOf(itemIndex);
        adapter.moveItemToNextPage(itemPosition.pageIndex, itemPosition.itemIndex);
    }

    public void setLockableScrollView(LockableScrollView lockableScrollView) {
        this.lockableScrollView = lockableScrollView;
        lockableScrollView.setScrollingEnabled(true);
    }

    private class ItemPosition {
        public int pageIndex;
        public int itemIndex;

        public ItemPosition(int pageIndex, int itemIndex) {
            super();
            this.pageIndex = pageIndex;
            this.itemIndex = itemIndex;
        }
    }
    public interface OnDragDropGridItemClickListener {
        public void onClick(View view, int page, int item);
        public void onDoubleClick(View view, int page, int item);
        public void onFullScreenChange(View view, int page, int item, boolean isFullScreen);
    }
    public interface OnDragDropGridItemAnimationListener {
        public void onDraggedViewAnimationStart(View view, int page, int item);
        public void onDraggedViewAnimationEnd(View view, int page, int item);
        public void onFullScreenChangeAnimationStart(View view,int page,int item,boolean toFullScreen);
        public void onFullScreenChangeAnimationEnd(View view,int page,int item,boolean toFullScreen);
    }
}
