package cnedu.ustcjd.helloworld;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
                .request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean aBoolean) throws Exception {
                                   Toast.makeText(PermissionActivity.this, "permit", Toast.LENGTH_SHORT).show();
                                   Log.d(TAG, "permit");
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(PermissionActivity.this, "denied", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "denied");
                            }
                        },
                        new Action() {
                            @Override
                            public void run() throws Exception {
                                Toast.makeText(PermissionActivity.this, "complete", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "complete");
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
