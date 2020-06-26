package com.maple.recordwav.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 *
 * @author maple
 * @time 2018-08-06
 */
public abstract class BaseQuickAdapter<T, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> {
    private List<T> mDataList = new ArrayList();

    public void refreshData(List<T> newData) {
        mDataList = newData != null ? newData : new ArrayList<>();
        this.notifyDataSetChanged();
    }

    public List<T> getData() {
        return mDataList;
    }

    public T getItem(int position) {
        return mDataList.get(position);
    }

//    public void add(T t) {
//        if (t == null)
//            return;
//        mDataList.add(t);
//        this.notifyDataSetChanged();
//    }
//
//    public void remove(int index) {
//        if (index < 0 || index >= mDataList.size())
//            return;
//        mDataList.remove(index);
//        this.notifyDataSetChanged();
//    }
//
//    public void remove(T data) {
//        if (data == null)
//            return;
//        mDataList.remove(data);
//        this.notifyDataSetChanged();
//    }
//
//    // 测试此列表是否包含指定的对象。
//    public boolean contains(T t) {
//        return mDataList.contains(t);
//    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    // ---------------------------- listener -------------------------------

    // 在holder中手动绑定
    protected void bindViewClickListener(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            if (itemClickListener != null) {
                viewHolder.itemView.setOnClickListener(v ->
                        itemClickListener.onItemClick(getItem(position), position)
                );
            }
            if (itemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener(v ->
                        itemLongClickListener.onLongClick(v, position)
                );
            }
        }
    }

    // item click
    private OnItemClickListener<T> itemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        itemClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        // void onItemClick(View v, int position);
        void onItemClick(T item, int position);
    }

    // item long click
    private OnItemLongClickListener itemLongClickListener = null;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        itemLongClickListener = listener;
    }

    public interface OnItemLongClickListener {
        boolean onLongClick(View v, int position);
    }

}
