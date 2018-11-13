package cnedu.ustcjd.draggableviewpager;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cnedu.ustcjd.widget.draggableviewpager.DraggableViewPagerAdapter;

public class ExampleDraggableViewPagerAdapter implements DraggableViewPagerAdapter {

    /**
     * 每行item个数
     */
    private static final int ROW_SIZE = 2;
    /**
     * 每列item个数
     */
    private static final int COLUMN_SIZE = 2;
    private static final int PAGE_ITEM_SIZE = 4;

    private List<Item> items = new ArrayList<>();
    private Context context;
    private ItemViewCallback mItemCallback;

    public ExampleDraggableViewPagerAdapter(Context context, ItemViewCallback itemViewCallback) {
        super();
        this.context = context;
        this.mItemCallback = itemViewCallback;

        int totalCount = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < PAGE_ITEM_SIZE; j++){
                totalCount +=1;
                items.add(new Item(totalCount, "Item"+totalCount, R.drawable.ic_launcher));
            }
        }
    }

    @Override
    public int pageCount() {
        return (items.size() + PAGE_ITEM_SIZE - 1) / PAGE_ITEM_SIZE;
    }

    private List<Item> itemsInPage(int page) {
        if (pageCount() > page) {
            int max = (page + 1) * PAGE_ITEM_SIZE;
            if ((page + 1) * PAGE_ITEM_SIZE > items.size()) max = items.size();
            return items.subList(page * PAGE_ITEM_SIZE, max);
        }
        return Collections.emptyList();
    }

    @Override
    public View view(final int page, final int index) {
        final Item item = getItem(page, index);
        GridItemView gridItem = new GridItemView(context, item.getName());
        gridItem.setItemDeleteCallback(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemCallback != null) mItemCallback.onItemViewDeleteClick(item);
            }
        });
        gridItem.setItemAddCallback(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemCallback != null) mItemCallback.onItemViewAddClick(item);
            }
        });
        return gridItem.getView();
    }

    private Item getItem(int page, int index) {
        List<Item> items = itemsInPage(page);
        return items.get(index);
    }

    @Override
    public int rowCount() {
        return 2;
    }

    @Override
    public int columnCount() {
        return 2;
    }

    @Override
    public int itemCountInPage(int page) {
        return itemsInPage(page).size();
    }

    public void printLayout() {
        int i = 0;
        for (int page = 0; page < pageCount(); page++) {
            Log.d("Page", Integer.toString(page));

            String msg = "";
            for (Item item : itemsInPage(page)) {
                msg += Long.toString(item.getId()) + ",";
            }
            Log.d("Item", msg);
        }
    }

    private List<Item> getPage(int pageIndex) {
        return itemsInPage(pageIndex);
    }

    @Override
    public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
        int srcIndex = pageIndex * PAGE_ITEM_SIZE + itemIndexA;
        int desIndex = pageIndex * PAGE_ITEM_SIZE + itemIndexB;
        Collections.swap(items, srcIndex, desIndex);
        printLayout();
    }

    @Override
    public void moveItemToPreviousPage(int pageIndex, int itemIndex) {
        if (pageIndex > 0) {
            int startIndex = pageIndex * PAGE_ITEM_SIZE + itemIndex;
            int destIndex = pageIndex * PAGE_ITEM_SIZE - 1;
            Collections.swap(items, startIndex, destIndex);
        }
        printLayout();
    }

    @Override
    public void moveItemToNextPage(int pageIndex, int itemIndex) {
        if (pageIndex + 1 < pageCount()) {
            int srcIndex = pageIndex * PAGE_ITEM_SIZE + itemIndex;
            int desIndex = (pageIndex + 1) * PAGE_ITEM_SIZE;
            Collections.swap(items, srcIndex, desIndex);
        }
        printLayout();
    }

    @Override
    public void deleteItem(Object item) {
        if (items.contains(item)) items.remove(item);
    }

    /**
     * add the item to adapter end
     *
     * @param obj
     */
    @Override
    public void addItem(Object obj) {
        items.add((Item) obj);
    }


    @Override
    public int getPageWidth() {
        return 0;
    }

    @Override
    public Object getItemAt(int page, int index) {
        return getPage(page).get(index);
    }

    @Override
    public boolean disableZoomAnimationsOnChangePage() {
        return false;
    }

    @Override
    public void destroyPage(int page) {
        //android.util.Log.d("XXXX","destroy page: "+page);
        }

    @Override
    public boolean containsObject(Object obj) {
        return items.contains(obj);
    }

    @Override
    public int size() {
        return items.size();
    }

    public interface ItemViewCallback {
        void onItemViewDeleteClick(Object obj);
        void onItemViewAddClick(Object obj);
    }
}
