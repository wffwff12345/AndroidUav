package com.example.uavapplication.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.uavapplication.R;

import java.util.List;

public class TaskItemRecyclerViewAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public TaskItemRecyclerViewAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv, item);
    }
}