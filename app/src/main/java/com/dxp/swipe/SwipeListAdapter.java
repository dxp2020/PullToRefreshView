package com.dxp.swipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public abstract class SwipeListAdapter implements ListAdapter{

    private Context mContext;
    private ListAdapter mAdapter;

    public SwipeListAdapter(Context mContext, ListAdapter mAdapter) {
        this.mContext = mContext;
        this.mAdapter = mAdapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeView view = null;
        if(convertView==null){
            view = new SwipeView(mContext, mAdapter.getView(position,convertView,parent));
            view.addLeftMenu(createLeftMenu());
            view.addRightMenu(createRightMenu());
        }else{
            view = (SwipeView) convertView;
            view.closeMenu();
        }
        return view;
    }

    public abstract SwipeMenu createLeftMenu();

    public abstract SwipeMenu createRightMenu();

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }
}
