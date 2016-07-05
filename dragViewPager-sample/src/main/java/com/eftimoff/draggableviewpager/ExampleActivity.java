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
package com.eftimoff.draggableviewpager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ExampleActivity extends Activity implements OnClickListener {

    private String CURRENT_PAGE_KEY = "CURRENT_PAGE_KEY";

    private DraggableViewPager gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        gridview = (DraggableViewPager) findViewById(R.id.gridview);

        ExampleDraggableViewPagerAdapter adapter = new ExampleDraggableViewPagerAdapter(this, gridview);

        gridview.setAdapter(adapter);
        gridview.setClickListener(this);

        gridview.setBackgroundColor(Color.BLACK);


    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int savedPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
        gridview.restoreCurrentPage(savedPage);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt(CURRENT_PAGE_KEY, gridview.currentPage());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Clicked View", Toast.LENGTH_SHORT).show();
    }
}
