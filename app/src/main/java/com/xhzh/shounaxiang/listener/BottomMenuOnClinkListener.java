package com.xhzh.shounaxiang.listener;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xhzh.shounaxiang.R;

/**
 * Created by wjsay on 2018/10/8
 * Describe:
 */
public class BottomMenuOnClinkListener implements View.OnClickListener {
    Context context;
    public BottomMenuOnClinkListener(Context context) {
        this.context = context;
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
                Toast.makeText(context, "请选择照片", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_open_camera:
                //拍照按钮
                Toast.makeText(context, "即将打开相机", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cancel:
                //取消按钮
                // mCameraDialog.dimiss(); // 取消菜单
                Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    private void setDialog() {
        Dialog mCameraDialog = new Dialog(context, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.tv_choose_img).setOnClickListener(new BottomMenuOnClinkListener(context));
        root.findViewById(R.id.tv_open_camera).setOnClickListener(new BottomMenuOnClinkListener(context));
        root.findViewById(R.id.btn_cancel).setOnClickListener(new BottomMenuOnClinkListener(context));
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }
}
