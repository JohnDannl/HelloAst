package cnedu.ustcjd.helloworld;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import cnedu.ustcjd.widget.CustomPopupMenu;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = "PermissionActivity";
    private CustomPopupMenu mPopupMenu;
    private ImageView ivLight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        ivLight = findViewById(R.id.iv_light);
        Log.d(TAG, "onCreate");
    }


    public void onClickBtnRotate(View view) {
        boolean isLand = view.getTag() != null && (Boolean) view.getTag();
        view.setTag(!isLand);
        setRequestedOrientation(isLand ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void onClickIvLight(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        View lightMode = inflater.inflate(R.layout.light_mode, null);
        mPopupMenu = new CustomPopupMenu(PermissionActivity.this, ivLight, lightMode,
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        lightMode.findViewById(R.id.iv_light_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu.dismiss();
                ivLight.setImageLevel(1);
            }
        });
        lightMode.findViewById(R.id.iv_light_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu.dismiss();
                ivLight.setImageLevel(0);
            }
        });
        lightMode.findViewById(R.id.iv_light_auto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupMenu.dismiss();
                ivLight.setImageLevel(2);
            }
        });
        mPopupMenu.show();
    }
    private void requestPermission() {
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(new String[] {Manifest.permission.WRITE_SETTINGS})
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   Toast.makeText(PermissionActivity.this, "permit:" + aBoolean, Toast.LENGTH_SHORT).show();
                                   Log.d(TAG, "permit:" + aBoolean);
                                   try {
                                       int mAcceleramater = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                                       Log.i(TAG, "mAcceleramater before= " + mAcceleramater);
                                       Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                                       mAcceleramater = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                                       Log.i(TAG, "mAcceleramater after= " + mAcceleramater);
                                   } catch (Settings.SettingNotFoundException e) {
                                       e.printStackTrace();
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(PermissionActivity.this, "denied", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "denied:" + throwable.getMessage());
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                Toast.makeText(PermissionActivity.this, "complete", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "complete");
                            }
                        });

        rxPermissions.requestEach(new String[] {Manifest.permission.WRITE_SETTINGS}).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                Log.d(TAG, String.format("prmission:%s, %s, %s", permission.name, permission.granted, permission.shouldShowRequestPermissionRationale));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        }, new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }
}
