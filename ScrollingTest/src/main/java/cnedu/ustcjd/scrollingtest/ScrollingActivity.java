package cnedu.ustcjd.scrollingtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cnedu.ustcjd.scrollingtest.R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(cnedu.ustcjd.scrollingtest.R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(cnedu.ustcjd.scrollingtest.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView rvList = (RecyclerView) findViewById(R.id.rv_list_with_delete);
        rvList.setNestedScrollingEnabled(false);
        LinearLayoutManager listLM = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(listLM);
        List<String>  infoList = new ArrayList<String>();
        for(int i = 0; i < 10; i++) {
            infoList.add("item swipe to delete " + i);
        }
        RecyclerViewListAdapter listAdapter = new RecyclerViewListAdapter(this, infoList);
        rvList.setAdapter(listAdapter);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(cnedu.ustcjd.scrollingtest.R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == cnedu.ustcjd.scrollingtest.R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
