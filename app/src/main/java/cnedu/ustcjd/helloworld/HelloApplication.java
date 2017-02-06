package cnedu.ustcjd.helloworld;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by jd5737 on 2016/12/30.
 */

public class HelloApplication extends Application {
    private static String TAG = "HelloApplication";
    private static Context mContext;
    private static boolean isPaused = false;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION) ||
                    action.equalsIgnoreCase(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                Log.i(TAG, "received network action: " + action);
                NetworkManager.updateNetworkState(HelloApplication.this.getBaseContext());
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManager.init(getBaseContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(receiver, filter);
    }

    public static void onPause() {
        isPaused = true;
    }
    public static void onResume() {
        isPaused = false;
    }
    public static boolean isAppSuspended() {
        return isPaused;
    }
}
