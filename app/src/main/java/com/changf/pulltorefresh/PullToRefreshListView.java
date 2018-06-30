package com.changf.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.changf.pulltorefresh.base.PullToRefreshAbsListView;
import com.changf.pulltorefresh.base.ViewOrientation;
import com.changf.utils.MotionEventUtils;

public class PullToRefreshListView extends PullToRefreshAbsListView<ListView> {
    private String TAG = "PullToRefreshListView";

    public PullToRefreshListView(Context context) {
        this(context,null);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ListView setRefreshView(Context context,AttributeSet attrs) {
        return new MyListView(context,attrs);
    }

    private class MyListView extends ListView implements ViewOrientation {

        public MyListView(Context context) {
            super(context);
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean isScrolledTop() {
            return PullToRefreshListView.this.isScrolledTop();
        }

        @Override
        public boolean isScrolledBottom() {
            return PullToRefreshListView.this.isScrolledBottom();
        }
    }

}