package cnedu.ustcjd.rangeseekbar;

import android.app.Activity;
import android.os.Bundle;

import cnedu.ustcjd.widget.MultiSlider;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cnedu.ustcjd.rangeseekbar.R.layout.activity_main);
        MultiSlider multiSlider5 = (MultiSlider)findViewById(cnedu.ustcjd.rangeseekbar.R.id.range_slider5);

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
