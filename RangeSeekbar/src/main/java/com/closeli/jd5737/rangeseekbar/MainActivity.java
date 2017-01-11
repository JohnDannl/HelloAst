package com.closeli.jd5737.rangeseekbar;

import android.app.Activity;
import android.os.Bundle;

import com.closeli.jd5737.widget.MultiSlider;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MultiSlider multiSlider5 = (MultiSlider)findViewById(R.id.range_slider5);

        multiSlider5.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {

                } else {

                }
            }
        });
    }
}
