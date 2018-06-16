package com.dxp.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.dxp.pulltorefresh.base.PullToRefreshAbsListView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.pulltorefresh.base.ViewDirector;

public class PullToRefreshGridView extends PullToRefreshAbsListView<GridView> {
    private String TAG = "PullToRefreshGridView";

    public PullToRefreshGridView(Context context) {
        this(context,null);
    }

    public PullToRefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected GridView setRefreshView(Context context,AttributeSet attrs) {
        return new MyGridView(context,attrs);
    }

    private class MyGridView extends GridView implements ViewDirector{

        public MyGridView(Context context) {
            super(context);
        }

        public MyGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean isScrolledTop() {
           return PullToRefreshGridView.this.isScrolledTop();
        }

        @Override
        public boolean isScrolledBottom() {
            return PullToRefreshGridView.this.isScrolledBottom();
        }
    }

}
