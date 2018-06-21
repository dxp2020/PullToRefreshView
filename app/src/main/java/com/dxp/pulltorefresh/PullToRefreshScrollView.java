package com.dxp.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.pulltorefresh.base.ViewOrientation;

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
        return new MyScrollView(context,attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getChildAt(getChildCount()-1);
        removeView(view);

        getRefreshView().addView(view);
    }

    private class MyScrollView extends ScrollView implements ViewOrientation {

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
