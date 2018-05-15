package cnedu.ustcjd.helloworld;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import cnedu.ustcjd.util.IntentWrapper;

public class BatteryOptimizationActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_optimization);
    }

    public void onClickCheck(View view) {
        boolean ret = IntentWrapper.checkIfMyselfInPowerWhiteList(this);
        Toast.makeText(this, String.format("SDK_Ver:%s, in white list:%s", Build.VERSION.SDK_INT, ret), Toast.LENGTH_SHORT).show();
    }

    public void onClickRequest(View view) {
        List<IntentWrapper> ret = IntentWrapper.showBatteryOptimizationSettingDialog(this);
        Toast.makeText(this, String.format("intent list:%s, type:%s", ret.size(), ret.size() > 0 ? ret.get(0).getType() : -1), Toast.LENGTH_SHORT).show();
    }
}
