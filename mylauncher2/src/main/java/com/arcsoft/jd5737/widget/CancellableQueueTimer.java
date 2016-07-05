package com.arcsoft.jd5737.widget;

import android.os.Handler;

/**
 * 定时任务
 * @author JChun
 *
 */
public class CancellableQueueTimer implements Runnable {
    private Runnable callback;
    private Handler handler;

    public CancellableQueueTimer(Handler handler, int delay, Runnable runnable) {
        this.handler = handler;
        this.handler.postDelayed(this, delay);
        callback = runnable;
    }

    public void cancel() {
        if (handler != null) {
            if (callback != null) {
                handler.removeCallbacks(callback);
            }
            handler = null;
        }
        callback = null;
    }

    @Override
    public void run() {
        handler = null;
        if (callback != null) {
            callback.run();
            callback = null;
        }
    }
}