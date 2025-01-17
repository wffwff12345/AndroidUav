package com.example.uavapplication.dialog;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;

/**
 * <p>确认框</p>
 */
public class ConfirmDialog extends BaseDialog {
    /**
     * 当前上下文
     */
    private Context context;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * true: 只有“确定”按钮
     * false: 确定和取消按钮
     */
    private boolean isSingleButton;

    private int position = -1;

    public ConfirmDialog(Context context, int calledByViewId, String title, String content, boolean isSingleButton) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.calledByViewId = calledByViewId;
        this.isSingleButton = isSingleButton;
        initialize();
    }

    public ConfirmDialog(Context context, int calledByViewId, String title, String content, boolean isSingleButton, int position) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.calledByViewId = calledByViewId;
        this.isSingleButton = isSingleButton;
        this.position = position;
        initialize();
    }

    public ConfirmDialog(Fragment fragment, int calledByViewId, String title, String content, boolean isSingleButton) {
        this(fragment.getActivity(), calledByViewId, title, content, isSingleButton);
        setResult(fragment);
    }
    private void initialize() {
        title(title);
        content(content);
        positiveText("确定");
        if (position != -1) {
            onPositive((dialog, which) -> onResultClick(DialogAction.POSITIVE, String.valueOf(this.position)));
        } else {
            onPositive((dialog, which) -> onResultClick(DialogAction.POSITIVE));
        }
        if (isSingleButton == false) {
            negativeText("取消");
            onNegative((dialog, which) -> onResultClick(DialogAction.NEGATIVE));
        }
    }
}
