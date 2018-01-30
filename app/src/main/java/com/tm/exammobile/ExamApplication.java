package com.tm.exammobile;

import android.app.Application;

import com.tm.exammobile.listeners.ConnectionReceiver;

/**
 * Last edit by tudor.maier on 30/01/2018.
 */

public class ExamApplication extends Application {

    private static ExamApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

    }

    public static synchronized ExamApplication getInstance() {
        return mInstance;
    }

    public void setConnectionListener(ConnectionReceiver.ConnectionReceiverListener listener) {
        ConnectionReceiver.connectionReceiverListener = listener;
    }
}