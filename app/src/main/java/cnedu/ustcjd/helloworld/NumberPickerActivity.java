package cnedu.ustcjd.helloworld;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by jd5737 on 2017/5/2.
 */

public class NumberPickerActivity extends Activity {
    private static final String TAG = "NumberPicker";
    private TextView tvTime;
    private int value = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cnedu.ustcjd.helloworld.R.layout.activity_number_picker);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }
    private void showTimePicker() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog);
        dialog.setContentView(R.layout.dialog_pick_time);
        dialog.setTitle(R.string.btn_ok);
        NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.np_time);
        numberPicker.setMinValue(5);
        numberPicker.setMaxValue(23);
        numberPicker.setValue(value);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%d : 00", value);
            }
        });
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, "new value:" + newVal);
                value = newVal;
            }
        });
        try {   // fix Android's bug ,the setValue not shown
            Field f = NumberPicker.class.getDeclaredField("mInputText");
            f.setAccessible(true);
            EditText inputText = (EditText) f.get(numberPicker);
            inputText.setFilters(new InputFilter[0]);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTime.setText(String.valueOf(value));
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.btn_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
