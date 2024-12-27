package com.example.uavapplication.dialog;

import android.content.Context;
import android.text.InputType;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;

/**
 * <p>输入对话框</p>
 *
 */
public class InputTextDialog extends BaseDialog {
    /**
     * 文本键盘
     */
    public static final int KEYBOARD_TEXT = 1;
    /**
     * 数字键盘
     */
    public static final int KEYBOARD_NUMBER = 2;
    /**
     * 标题
     */
    private String title;
    /**
     * 键盘类型
     */
    private int keyboardType;

    public InputTextDialog(Context context, int calledByViewId, String title) {
        super(context);
        this.title = title;
        this.calledByViewId = calledByViewId;
        initialize(context);
    }

    public InputTextDialog(Fragment fragment, int calledByViewId, String title) {
        this(fragment.getActivity(), calledByViewId, title);
        setResult(fragment);
    }

    private void initialize(final Context context) {
        title(title);
        input(null, "", (dialog, input) -> {
            onResultClick(DialogAction.POSITIVE, String.valueOf(input));
            autoDismiss = true;
        });
        positiveText("确定");
        onPositive((dialog, which) -> autoDismiss = true);
        negativeText("取消");
        onNegative((dialog, which) -> {
            onResultClick(DialogAction.NEGATIVE);
            autoDismiss = true;
        });
    }

    public void setKeyboardType(int keyboardType) {
        this.keyboardType = keyboardType;
        if (keyboardType == KEYBOARD_TEXT) {
            inputType(InputType.TYPE_CLASS_TEXT);
        } else if (keyboardType == KEYBOARD_NUMBER) {
            inputType(InputType.TYPE_CLASS_NUMBER);
        }
    }
}