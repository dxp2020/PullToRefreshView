package com.changf.pulltorefresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.changf.pulltorefresh.base.PullToRefreshAbsListView;
import com.changf.pulltorefresh.base.ViewOrientation;

public class PullToRefreshListView extends PullToRefreshAbsListView<ListView> {

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
