package com.example.uavapplication.dialog;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * <p>基类Dialog</p>
 *
 */
public class BaseDialog extends MaterialDialog.Builder implements View.OnClickListener {
    protected Object resultObject;
    protected int calledByViewId;
    protected MaterialDialog dialog;

    public BaseDialog(Context context) {
        super(context);
        resultObject = context;
        // dialog表示以外区域,点击不能关闭
        cancelable(false);
        canceledOnTouchOutside(false);
    }

    public void setResult(Object result) {
        this.resultObject = result;
    }

    public void onResultClick(DialogAction which, String resultValue) {
        // 事件发送
        DialogEvent dialogEvent = new DialogEvent(calledByViewId, which, resultValue);
        EventBus.getDefault().post(dialogEvent);
    }

    public void onResultClick(DialogAction which) {
        onResultClick(which, "");
    }

    public void setCalledByViewId(int viewId) {
        this.calledByViewId = viewId;
    }

    @Override
    public void onClick(View v) {

    }

    protected void onBeep() {

    }
}
