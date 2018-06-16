package com.dxp.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.pulltorefresh.base.ViewDirector;

public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {
    private String TAG = "PullToRefreshScrollView";

    public PullToRefreshScrollView(Context context) {
        this(context,null);
    }

    public PullToRefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScrollView setRefreshView(Context context,AttributeSet attrs) {
        View view = getChildAt(0);
        removeView(view);

        ScrollView scrollView= new MyScrollView(context,attrs);
        scrollView.addView(view);
        return scrollView;
    }

    private class MyScrollView extends ScrollView implements ViewDirector{

        public MyScrollView(Context context) {
            super(context);
        }

        public MyScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean isScrolledTop() {
            return getScrollY() == 0;
        }

        @Override
        public boolean isScrolledBottom() {
            View childView = getChildAt(0);
            if(childView.getMeasuredHeight() <= getScrollY() + getHeight()){
                return true;
            }
            return false;
        }

    }

}
