package com.maple.recorder;

import android.os.Handler;
import android.os.Looper;

/**
 * A {@code UiThread} is representation of Ui / main thread.
 *
 * @author Kailash Dabhi
 * @date 25-07-2016
 */
final class UiThread implements ThreadAction {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * executes the {@code Runnable} on UI Thread.
     */
    @Override
    public void execute(Runnable runnable) {
        handler.post(runnable);
    }

}