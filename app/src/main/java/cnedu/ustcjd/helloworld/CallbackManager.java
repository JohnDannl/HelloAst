package cnedu.ustcjd.helloworld;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd5737 on 2017/10/26.
 */

public class CallbackManager {
    private static String TAG = "Callback";
    private List<Callback> callbackList = new ArrayList<Callback>();

    static class ManagerHolder {
        static CallbackManager instance = new CallbackManager();
    }

    public static CallbackManager getInstance() {
        return ManagerHolder.instance;
    }

    public void addCallback(Callback callback) {
        callbackList.add(callback);
    }

    public void removeCallback(Callback callback) {
        callbackList.remove(callback);
    }

    public int getCallbackSize() {
        return callbackList.size();
    }

    public void call() {
        for (Callback callback : callbackList) {
            if (callback != null)callback.onCallback(getCallbackSize());
            else Log.d(TAG, "callback is null");
        }
    }

    public interface Callback {
        void onCallback(int i);
    }
}
