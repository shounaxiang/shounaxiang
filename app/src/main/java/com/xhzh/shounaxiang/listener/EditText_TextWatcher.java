package com.xhzh.shounaxiang.listener;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.mysql.jdbc.StringUtils;
import com.xhzh.shounaxiang.R;

/**
 * Created by wjsay on 2018/10/8
 * Describe:
 */
public class EditText_TextWatcher implements TextWatcher {
    private Activity m_activity;
    private static Button button;
    private static EditText editText;
    public EditText_TextWatcher(Activity activity, Button btn_add_goods, EditText et_goods_name) {
        if (activity == null || button == null || et_goods_name == null) {
            m_activity = activity;
            button = btn_add_goods;
            editText = et_goods_name;
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        work();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void work() {
        try {
            if (StringUtils.isNullOrEmpty(editText.getText().toString().trim())) {
                button.setBackgroundColor(m_activity.getResources().getColor(R.color.button_color_gray_green));
                button.setEnabled(false);
            }
            else {

                    button.setBackgroundColor(m_activity.getResources().getColor(R.color.button_color_green));
                    button.setEnabled(true);
            }
        } catch (Exception e) {
        // button 空指针异常
        }
    }

}
