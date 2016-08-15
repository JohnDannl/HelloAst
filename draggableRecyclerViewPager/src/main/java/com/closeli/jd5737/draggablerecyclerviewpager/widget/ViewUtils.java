package com.closeli.jd5737.draggablerecyclerviewpager.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ViewUtils {
    /**
     * Gets top-center child
     */
    public static View getTopCenterChild(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            return findChildViewOfRecyclerViewUnder(recyclerView,
                    recyclerView.getWidth() / 2, recyclerView.getHeight() / 4);
        }
        return null;
    }
    /**
     * Gets position of top-center child
     */
    public static int getTopCenterChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            View child = findChildViewOfRecyclerViewUnder(recyclerView,
                    recyclerView.getWidth() / 2, recyclerView.getHeight() / 4);
            return recyclerView.getChildAdapterPosition(child);
        }
        return childCount;
    }
    /**
     * Gets center child in X Axes
     */
    public static View getCenterChild(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            return findChildViewOfRecyclerViewUnder(recyclerView,
                    recyclerView.getWidth() / 2, recyclerView.getHeight() / 2);
        }
        return null;
    }
    /**
     * Gets position of center child in X Axes
     */
    public static int getCenterChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            View child = findChildViewOfRecyclerViewUnder(recyclerView,
                    recyclerView.getWidth() / 2, recyclerView.getHeight() / 2);
            return recyclerView.getChildAdapterPosition(child);
        }
        return childCount;
    }
    /**
     * Gets child on top and left of recyclerview
     */
    public static View getTopLeftChild(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            return findChildViewOfRecyclerViewUnder(recyclerView,
                    recyclerView.getWidth() / 4,  recyclerView.getHeight() / 4);
        }
        return null;
    }
    /**
     * Gets the position of child which locates on top and left of the recyclerview
     * @param recyclerView
     * @return
     */
    public static int getTopLeftChildPosition(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        if (childCount > 0) {
            View child = findChildViewOfRecyclerViewUnder(recyclerView,
                    recyclerView.getWidth() / 4, recyclerView.getHeight() / 4);
            return recyclerView.getChildAdapterPosition(child);
        }
        return childCount;
    }

    public static View findChildViewOfRecyclerViewUnder(RecyclerView recyclerView, int dx, int dy) {
        int[] rvLocationOnScreen = new int[2];
        recyclerView.getLocationOnScreen(rvLocationOnScreen);
        int x = rvLocationOnScreen[0] + dx;
        int y = rvLocationOnScreen[1] + dy;
        return recyclerView.findChildViewUnder(x, y);
    }
}
