package com.xhzh.shounaxiang.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.xhzh.shounaxiang.activity.MainActivity;

public class AppUtils {

    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    public static void showShortToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static Bitmap getBitmapFromSDCard(String img_name) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/xhzh/PicturesProfile/" + img_name + ".JPG";
        return BitmapFactory.decodeFile(path);
    }
}
