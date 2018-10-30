package cnedu.ustcjd.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StyleActivity extends AppCompatActivity {
    private static boolean bSwithA = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bSwithA) {
            setTheme(R.style.ATheme);
        } else {
            setTheme(R.style.BTheme);
        }

        setContentView(R.layout.activity_style);
    }

    public void onStyleSwitchClick(View view) {
        bSwithA = !bSwithA;
        recreate();
    }
}
