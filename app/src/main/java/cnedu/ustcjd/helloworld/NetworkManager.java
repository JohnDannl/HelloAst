package cnedu.ustcjd.helloworld;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd5737 on 2016/12/30.
 */

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    public static final String Network_Connected_Changed = "cnedu.ustcjd.helloworld.Network_Connected_Changed";
    public static final String Network_State_Changed = "cnedu.ustcjd.helloworld.Network_State_Changed";
    public static final String Netwodk_Current_State = "cnedu.ustcjd.helloworld.Netwodk_Current_State";
    private static List<INetworkStatusListener> sListeners = new ArrayList<INetworkStatusListener>();
    private static boolean isNetworkConnected = true;

    public static void init(final Context context) {
        isNetworkConnected = isWiFiActive(context) || isMobileActive(context) || isEthernetActive(context);
    }

    public static void updateNetworkState(final Context context) {
        try {
            synchronized (NetworkManager.class) {
                if (isWiFiActive(context) || isMobileActive(context) || isEthernetActive(context)) {
                    if (isWiFiActive(context)) {
                        Log.d(TAG, "network now is Wifi");
                    }
                    if (isMobileActive(context)) {
                        Log.d(TAG, "network now is Mobile");
                    }
                    if (isEthernetActive(context)) {
                        Log.d(TAG, "network now is Ethernet");
                    }
                    Log.i(TAG, "network real state: " + true);
                    if (!isNetworkConnected) {
                        notifyNetworkChanged(context, true);
                    } else {
                        if (!HelloApplication.isAppSuspended()) {
                            Log.d(TAG, "notify network connected changed: ");
                            Intent intent = new Intent(Network_Connected_Changed);
                            context.sendBroadcast(intent);
                        } else {
                            Log.d(TAG, "App is suspended, notify all when come back");
                        }
                        Log.i(TAG, "skipped");
                    }
                } else {
                    Log.i(TAG, "network real state: " + false);
                    if (isNetworkConnected) {
                        notifyNetworkChanged(context, false);
                    } else {
                        Log.i(TAG, "skipped");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void notifyNetworkChanged(final Context context, final boolean connected) {
        isNetworkConnected = connected;
        if (!HelloApplication.isAppSuspended()) {
            Log.d(TAG, "notify network changed: " + connected);
            Intent intent = new Intent(Network_State_Changed);
            intent.putExtra(Netwodk_Current_State, isNetworkConnected);
            context.sendBroadcast(intent);

            for (final INetworkStatusListener listener : sListeners) {
                listener.onNetworkStatusChanged(isNetworkConnected);
            }
        } else {
            Log.d(TAG, "App is suspended, notify all when come back");
        }
    }

    public static boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    public static boolean isWiFiActive(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi == null) {
            return false;
        }
        return wifi.isConnected();
    }

    public static boolean isMobileActive(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return  mobile != null && mobile.isConnected();
    }

    public static boolean isEthernetActive(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethernet = conMan.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (ethernet == null) {
            return false;
        }
        return ethernet.isConnected();
    }

    public static void addListener(INetworkStatusListener listener) {
        sListeners.add(listener);
    }

    public static void removeListener(INetworkStatusListener listener) {
        sListeners.remove(listener);
    }

    public static interface INetworkStatusListener {
        void onNetworkStatusChanged(final boolean connectedFlag);
    }
}

