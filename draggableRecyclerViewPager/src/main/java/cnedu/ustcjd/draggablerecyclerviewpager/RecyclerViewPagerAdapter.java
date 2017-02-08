package cnedu.ustcjd.draggablerecyclerviewpager;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import cnedu.ustcjd.draggablerecyclerviewpager.widget.DraggableItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jd5737 on 2016/8/16.
 */
public class RecyclerViewPagerAdapter extends RecyclerView.Adapter<RecyclerViewPagerAdapter.ItemViewHolder> implements DraggableItemTouchHelperAdapter {

    private static final String TAG = "RecyclerViewPagerAdp";
    private final List<Item> mItems = new ArrayList<Item>();
    private Context mContext;
    private static final int ITEM_COUNT_OF_PAGE = 4;
    private int displayWidth = 1280;    // initializes to 720P
    private int displayHeight = 720;
    private static int TYPE_NORMAL = 0;
    private static int TYPE_FOOTER = 1;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ItemViewHolder(final View itemView, int viewType) {
            super(itemView);
            textView = (TextView) itemView.findViewById(cnedu.ustcjd.draggablerecyclerviewpager.R.id.info_text);
            ViewGroup.LayoutParams lp = textView.getLayoutParams();
            lp.width = displayWidth / 2;
            lp.height = displayHeight / 2;
            textView.setLayoutParams(lp);
            /*textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"Click " + ((TextView) v).getText(),Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

    public RecyclerViewPagerAdapter(Context context) {
        mContext = context;
        final WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        display.getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;
        for (int i = 0; i < 13; i++) {
            mItems.add(new Item(i + 1, "Item " + (i + 1), cnedu.ustcjd.draggablerecyclerviewpager.R.drawable.ic_launcher));
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        ItemViewHolder itemViewHolder = null;
        if (viewType == TYPE_NORMAL) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(cnedu.ustcjd.draggablerecyclerviewpager.R.layout.recycler_view_item_normal, parent, false);
            itemViewHolder = new ItemViewHolder(itemView, viewType);
            itemViewHolder.textView = (TextView) itemView.findViewById(cnedu.ustcjd.draggablerecyclerviewpager.R.id.info_text);
        } else if (viewType == TYPE_FOOTER){
            itemView = LayoutInflater.from(parent.getContext()).inflate(cnedu.ustcjd.draggablerecyclerviewpager.R.layout.recycler_view_item_footer, parent, false);
            itemViewHolder = new ItemViewHolder(itemView, viewType);
            itemViewHolder.textView = (TextView) itemView.findViewById(cnedu.ustcjd.draggablerecyclerviewpager.R.id.info_text);
        }
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (position < mItems.size()) {
            holder.textView.setText(mItems.get(position).getName());
        } else {
            holder.textView.setText("Empty " + (position - mItems.size() + 1));
        }
    }

    @Override
    public int getItemCount() {
        return (mItems.size() + ITEM_COUNT_OF_PAGE - 1) / ITEM_COUNT_OF_PAGE * ITEM_COUNT_OF_PAGE;
    }

    public int getDataItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= mItems.size()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // Notify that item reflected at fromPosition has been moved to toPosition.
        // This is a structural change event. Representations of other existing items in the data set
        // are still considered up to date and will not be rebound,though their positions may be
        // altered, so you need to swap one by one to move the item to target position
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i+1);
            }
        } else if (fromPosition > toPosition) {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        Log.d(TAG, String.format("onItemMove %s to %s", fromPosition, toPosition));
        /*String msg = "";
        for (int i = 0; i <  mItems.size(); i++) {
            msg += mItems.get(i).getId() + ",";
        }
        Log.d(TAG, msg);*/
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }
}
