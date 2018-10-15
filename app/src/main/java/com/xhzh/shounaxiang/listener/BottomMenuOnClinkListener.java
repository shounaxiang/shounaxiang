package com.xhzh.shounaxiang.listener;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xhzh.shounaxiang.R;
import com.xhzh.shounaxiang.util.AppUtils;
import com.xhzh.shounaxiang.util.Constant;

/**
 * Created by wjsay on 2018/10/8
 * Describe:
 */
public class BottomMenuOnClinkListener implements View.OnClickListener {
    private static Activity activity;
    static Dialog mCameraDialog;
    public BottomMenuOnClinkListener(Activity actitity) {
        this.activity = actitity;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_goods:
                //弹出对话框
                setDialog();
                break;
            case R.id.tv_choose_img:
                //选择照片按钮
                openAlbum();
                mCameraDialog.dismiss();
                // Toast.makeText(activity, "请选择照片", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_open_camera:
                //拍照按钮
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activity.startActivityForResult(intent, Constant.ADD_GOODS);
                if (mCameraDialog != null) mCameraDialog.dismiss();
                mCameraDialog.dismiss();
                //Toast.makeText(activity, "即将打开相机", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cancel:
                //取消按钮
                mCameraDialog.dismiss(); // 取消菜单
                Toast.makeText(activity, "取消", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    private void setDialog() {
        mCameraDialog = new Dialog(activity, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(activity).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.tv_choose_img).setOnClickListener(new BottomMenuOnClinkListener(activity));
        root.findViewById(R.id.tv_open_camera).setOnClickListener(new BottomMenuOnClinkListener(activity));
        root.findViewById(R.id.btn_cancel).setOnClickListener(new BottomMenuOnClinkListener(activity));
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) activity.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }
    private void openAlbum() { //选择图片就是这么简单
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        activity.startActivityForResult(intent, Constant.CHOOSE_GOODS); // 打开相册
    }
}
