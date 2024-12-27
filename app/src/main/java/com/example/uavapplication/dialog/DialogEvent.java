package com.example.uavapplication.dialog;

import com.afollestad.materialdialogs.DialogAction;

/**
 * <p>对话框事件</p>
 *
 */
public class DialogEvent {

    /**
     * 回调ID
     */
    private int calledByViewId;
    /**
     * 点击按钮类型
     */
    private DialogAction which;
    /**
     * 返回值
     */
    private String resultValue;

    public DialogEvent(int calledByViewId, DialogAction which, String resultValue) {
        this.calledByViewId = calledByViewId;
        this.which = which;
        this.resultValue = resultValue;
    }

    public int getCalledByViewId() {
        return calledByViewId;
    }

    public DialogAction getWhich() {
        return which;
    }

    public String getResultValue() {
        return resultValue;
    }
}
