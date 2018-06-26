package com.dxp.swipe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeListView extends ListView{
    private Context mContext;
    private SwipeMenuCreator mSwipeMenuCreator;

    public SwipeListView(Context context) {
        this(context,null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SwipeListAdapter(mContext,adapter){
            @Override
            public SwipeMenu createLeftMenu() {
                if (mSwipeMenuCreator!=null) {
                    return mSwipeMenuCreator.createLeftMenu();
                }
                return null;
            }
            @Override
            public SwipeMenu createRightMenu() {
                if (mSwipeMenuCreator!=null) {
                    return mSwipeMenuCreator.createRightMenu();
                }
                return null;
            }
        });
    }

    public SwipeMenuCreator getSwipeMenuCreator() {
        return mSwipeMenuCreator;
    }

    public void setSwipeMenuCreator(SwipeMenuCreator mSwipeMenuCreator) {
        this.mSwipeMenuCreator = mSwipeMenuCreator;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
