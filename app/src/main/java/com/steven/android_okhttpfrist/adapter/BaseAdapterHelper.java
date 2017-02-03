package com.steven.android_okhttpfrist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public abstract class BaseAdapterHelper<T> extends BaseAdapter {
    private List<T> list = null;
    private LayoutInflater inflater = null;

    public BaseAdapterHelper(List<T> list, Context context) {
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void reloadData(List<T> data , boolean isClear) {
        if (list != null) {
            if (isClear) {
                list.clear();
            }
            list.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clearAll() {
        if (list != null) {
            list.clear();
            notifyDataSetChanged();
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position, convertView, parent, list, inflater);
    }

    public abstract View getItemView(int position, View convertView, ViewGroup parent, List<T> list, LayoutInflater inflater);

}
