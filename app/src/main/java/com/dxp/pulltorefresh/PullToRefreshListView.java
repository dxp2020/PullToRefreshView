package com.dxp.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.dxp.pulltorefresh.base.PullToRefreshAbsListView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.pulltorefresh.base.ViewDirector;

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

    private class MyListView extends ListView implements ViewDirector{

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
