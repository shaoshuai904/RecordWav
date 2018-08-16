package com.maple.recordwav.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author maple
 * @time 16/4/13 下午5:55
 */
public abstract class AbsAdapter<T> extends BaseAdapter {
    public Context mContext;
    public LayoutInflater inflater;

    protected List<T> mDatas;

    public AbsAdapter(Context context, List<T> datas) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);

        if (datas == null) {
            mDatas = new ArrayList<T>();
        } else {
            mDatas = datas;
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public final void remove(int index) {
        if (index < 0 || index >= mDatas.size()) {
            return;
        }
        mDatas.remove(index);
        this.notifyDataSetChanged();

    }

    public final void remove(T data) {
        if (data == null) {
            return;
        }
        mDatas.remove(data);
        this.notifyDataSetChanged();

    }

    public final void add(T t) {
        if (t == null) {
            return;
        }
        mDatas.add(t);
        this.notifyDataSetChanged();
    }

    public void refresh(List<T> datas) {
        if (datas == null) {
            mDatas = new ArrayList<T>();
        } else {
            mDatas = datas;
        }

        this.notifyDataSetChanged();
    }

}
