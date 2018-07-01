package com.changf.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;

import com.changf.pulltorefresh.base.PullToRefreshAbsListView;
import com.changf.pulltorefresh.base.ViewOrientation;
import com.changf.pulltorefresh.swipe.SwipeListView;

public class PullToRefreshSwipeListView extends PullToRefreshAbsListView<SwipeListView> {
    private String TAG = "PullToRefreshSwipeListView";

    public PullToRefreshSwipeListView(Context context) {
        this(context,null);
    }

    public PullToRefreshSwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected SwipeListView setRefreshView(Context context,AttributeSet attrs) {
        return new MyListView(context,attrs);
    }

    private class MyListView extends SwipeListView implements ViewOrientation {

        public MyListView(Context context) {
            super(context);
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean isScrolledTop() {
            return PullToRefreshSwipeListView.this.isScrolledTop();
        }

        @Override
        public boolean isScrolledBottom() {
            return PullToRefreshSwipeListView.this.isScrolledBottom();
        }
    }

}

