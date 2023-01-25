package com.advanced.minhas.config;

import android.util.Log;

/**
 * Created by mentor on 22/3/18.
 */

public abstract class PrintConsole {

    public static void printLog(String tag, String message) {
        Log.e(tag, message);
    }


}
