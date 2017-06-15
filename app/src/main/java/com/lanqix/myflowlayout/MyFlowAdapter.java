package com.lanqix.myflowlayout;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


import com.lanqix.flowlayout.*;

import java.util.List;

/**
 * Created by Administrator on 2017/6/5.
 */
public class MyFlowAdapter extends FlowAdapter<TextView> {

    private Context context;
    private List<String> data;
    private OnItemClickListener<String> itemClickListener;

    public MyFlowAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public TextView onCreateViewHolder(FlowLayout flowLayout, int viewType) {
        TextView textView = new TextView(context);
        textView.setBackgroundResource(R.drawable.selector_radius);
        textView.setTextColor(context.getResources().getColorStateList(R.color.selector_c));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    @Override
    public void onBindViewHolder(TextView view, int position) {
        view.setText(data.get(position));
        view.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position, data.get(position), view);
            }
        });
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setItemClickListener(OnItemClickListener<String> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data, View view);
    }
}