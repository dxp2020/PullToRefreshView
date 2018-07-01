package com.changf.pulltorefresh.swipe;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public abstract class SwipeListAdapter implements ListAdapter{
    private String TAG = "SwipeListAdapter";

    private Context mContext;
    private SwipeListView mListView;
    private ListAdapter mAdapter;
    private int openedPosition = -1;
    private SwipeDirection openedItemSwipeDirection;

    public SwipeListAdapter(SwipeListView mListView,Context mContext, ListAdapter mAdapter) {
        this.mListView = mListView;
        this.mContext = mContext;
        this.mAdapter = mAdapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeView view = null;
        if(convertView==null){
            view = new SwipeView(mContext, mAdapter.getView(position, convertView, parent), handleClickEvent());
            view.addLeftMenu(createLeftMenu());
            view.addRightMenu(createRightMenu());
        }else{
            view = (SwipeView) convertView;
        }
        view.setSwipeDirection(mListView.getSwipeDirection());
        view.setPosition(position);

        view.setOnOpenedMenuListener(new SwipeView.OnOpenedMenuListener(){
            @Override
            public void startOpen(SwipeView view) {
                if(openedPosition!=-1&&openedPosition !=  view.getPosition()){
                    View childView = mListView.getChildAt(openedPosition - mListView.getFirstVisiblePosition());
                    if(childView instanceof SwipeView){
                        ((SwipeView) childView).smoothCloseMenu();
                    }
                    openedPosition = -1;
                    openedItemSwipeDirection = null;
                }
            }
            @Override
            public void onOpened(SwipeView view) {
                openedPosition = view.getPosition();
                openedItemSwipeDirection = view.getOpendMenuDirection();
            }
            @Override
            public void onClosed(SwipeView view) {
                openedPosition = -1;
                openedItemSwipeDirection = null;
            }
        });

        if(openedPosition==position){
            view.openMenu(openedItemSwipeDirection);
        }else{
            view.closeMenu();
        }
        return view;
    }

    private SwipeView.OnClickListener handleClickEvent() {
        if(mListView.isSetItemClick()){
           return new SwipeView.OnClickListener() {
                @Override
                public void onClick(SwipeView view) {
                    mListView.getOnItemClickListener().onItemClick(mListView,view,view.getPosition(),getItemId(view.getPosition()));
                }
            };
        }else{
            return null;
        }
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
