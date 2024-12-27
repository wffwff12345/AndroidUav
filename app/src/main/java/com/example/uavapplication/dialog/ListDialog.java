package com.example.uavapplication.dialog;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;

import java.util.List;

/**
 * <p>列表</p>
 *
 */
public class ListDialog extends BaseDialog {
    private String title;
    private List<String> values;

    public ListDialog(Context context, String title, List<String> values) {
        super(context);
        this.title = title;
        this.values = values;
        initialize(context, values);
    }

    public ListDialog(Fragment fragment, String title, List<String> values) {
        this(fragment.getActivity(), title, values);
        setResult(fragment);
    }

    public ListDialog(Context context, int calledByViewId, String title, List<String> values) {
        super(context);
        this.title = title;
        this.calledByViewId = calledByViewId;
        this.values = values;
        initialize(context, values);
    }

    public ListDialog(Fragment fragment, int calledByViewId, String title, List<String> values) {
        this(fragment.getActivity(), calledByViewId, title, values);
        setResult(fragment);
    }

    private void itemsCallBack(int selectedIndex) {
        itemsCallbackSingleChoice(selectedIndex, (dialog, itemView, which, text) -> {
            onResultClick(DialogAction.POSITIVE, values.get(which));
            return false;
        });
    }

    private void initialize(final Context context, List<String> values) {
        title(title);
        positiveText("确定");
        negativeText("取消");
        items(values);
        autoDismiss(false);
        itemsCallBack(0);
        onPositive((dialog, which) -> autoDismiss = true);
        onNegative((dialog, which) -> {
            onResultClick(DialogAction.NEGATIVE);
            autoDismiss = true;
        });
    }
}