package com.arcsoft.jd5737.draggablerecyclergrid;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static int COLUMN_SIZE = 2;
    private static int displayWidth, displayHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        final WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        display.getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new RecyclerGridAdapter());
        GridLayoutManager layoutManager = new GridLayoutManager(this, COLUMN_SIZE, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }
    class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerGridAdapter.ItemViewHolder> {
        private final List<String> mItems = new ArrayList<String>();

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            public ItemViewHolder(final View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.info_text);
                ViewGroup.LayoutParams lp = textView.getLayoutParams();
                lp.width = displayWidth / 2;
                lp.height = displayHeight / 2;
                textView.setLayoutParams(lp);
                        textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this,"Click " + ((TextView) v).getText(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        public RecyclerGridAdapter() {
            for (int i = 0; i < 12; i++) {
                mItems.add("Item " + (i + 1));
            }
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
            itemViewHolder.textView = (TextView) itemView.findViewById(R.id.info_text);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.textView.setText(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    };
}
