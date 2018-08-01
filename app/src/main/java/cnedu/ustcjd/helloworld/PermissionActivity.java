package cnedu.ustcjd.helloworld;

import android.Manifest;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = "PermissionActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Log.d(TAG, "onCreate");
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(new String[] {Manifest.permission.WRITE_SETTINGS, Manifest.permission.CAMERA})
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   Toast.makeText(PermissionActivity.this, "permit:" + aBoolean, Toast.LENGTH_SHORT).show();
                                   Log.d(TAG, "permit:" + aBoolean);
                                   if (aBoolean) {
                                       try {
                                           int mAcceleramater = Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
                                           Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0);
                                           Log.i(TAG, "mAcceleramater = " + mAcceleramater);
                                       } catch (Settings.SettingNotFoundException e) {
                                           e.printStackTrace();
                                       }
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

        rxPermissions.requestEach(new String[] {Manifest.permission.WRITE_SETTINGS, Manifest.permission.CAMERA}).subscribe(new Consumer<Permission>() {
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
}
