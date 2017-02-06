package cnedu.ustcjd.draggablerecyclerviewpager.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by jd5737 on 2016/8/23.
 */
public class FullScreenGridLayoutManager extends GridLayoutManager{

    public FullScreenGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FullScreenGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public FullScreenGridLayoutManager(Context context, int spanCount, int orientation,
                             boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }


}
