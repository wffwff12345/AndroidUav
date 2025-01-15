package com.example.uavapplication.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ctc.wstx.util.StringUtil;
import com.example.uavapplication.R;
import com.example.uavapplication.model.LatLngDto;
import com.example.uavapplication.utils.StringUtils;
import com.example.uavapplication.utils.ToastUtils;

public class TaskDetailDialog extends Dialog {
    private TextView tvTitle;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private EditText etALtitude;
    private EditText etHoverTime;
    private Button btnSave;
    private Button btnCancel;
    private LatLngDto latLngDto;
    private String title;
    private OnSaveClickListener onSaveClickListener;

    public TaskDetailDialog(@NonNull Context context) {
        super(context);
    }

    public TaskDetailDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TaskDetailDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public TaskDetailDialog(@NonNull Context context, LatLngDto latLngDto, String title) {
        super(context);
        this.latLngDto = latLngDto;
        this.title = title;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_task_detail);
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        tvLatitude = findViewById(R.id.tv_latitude);
        tvLatitude.setText("纬度: " + latLngDto.getLat() + "");
        tvLongitude = findViewById(R.id.tv_longitude);
        tvLongitude.setText("经度: " + latLngDto.getLng() + "");
        etALtitude = findViewById(R.id.et_altitude);
        etALtitude.setText(latLngDto.getAlt() + "");
        etHoverTime = findViewById(R.id.et_hover_time);
        etHoverTime.setText(latLngDto.getParam1() + "");
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.i("TaskDetailDialog", "onClick: ");
                    if (StringUtils.isBlank(etALtitude.getText().toString()) || StringUtils.isBlank(etHoverTime.getText().toString())) {
                        ToastUtils.toast(getContext(), "高度或时间不为空！");
                        return;
                    }
                    Float newHeight = Float.parseFloat(etALtitude.getText().toString());
                    Float newHoverTime = Float.parseFloat(etHoverTime.getText().toString());
                    latLngDto.setAlt(newHeight);
                    latLngDto.setParam1(newHoverTime);
                    if (onSaveClickListener != null) {
                        onSaveClickListener.onSaveClick(latLngDto);
                    }
                    dismiss();
                } catch (NumberFormatException e) {
                    ToastUtils.toast(getContext(), "高度或时间格式错误！");
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnSaveClickListener(OnSaveClickListener onSaveClickListener) {
    }

    public interface OnSaveClickListener {
        void onSaveClick(LatLngDto latLngDto);
    }
}
