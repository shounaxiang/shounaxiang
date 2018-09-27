package com.xhzh.shounaxiang.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by wjsay on 2018/9/27
 * Describe:
 */
public class PermUtil {
    public static boolean checkPerm(Context context, String permName) {
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(context,
                permName);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

}