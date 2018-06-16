package com.dxp.pulltorefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.dxp.pulltorefresh.base.PullToRefreshAbsListView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.pulltorefresh.base.ViewDirector;

public class PullToRefreshRecycleView extends PullToRefreshBase<RecyclerView> {
    private String TAG = "PullToRefreshRecycleView";

    public PullToRefreshRecycleView(Context context) {
        this(context,null);
    }

    public PullToRefreshRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected RecyclerView setRefreshView(Context context,AttributeSet attrs) {
        return new MyRecyclerView(context,attrs);
    }

    private class MyRecyclerView extends RecyclerView implements ViewDirector{

        public MyRecyclerView(Context context) {
            super(context);
        }

        public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean isScrolledTop() {
            View firstChild = getLayoutManager().findViewByPosition(0);//firstChild不必须完全可见
            View firstVisibleChild = getChildAt(0);//返回的是当前屏幕中的第一个子view，非整个列表
            if (firstVisibleChild != null) {
                if (firstChild != null && getLayoutManager().getDecoratedTop(firstChild)-getPaddingTop() == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                //没有元素也允许刷新
                return true;
            }
        }
        
        @Override
        public boolean isScrolledBottom() {
            View lastChild = getLayoutManager().findViewByPosition(getAdapter().getItemCount()-1);//lastChild不必须完全可见
            View firstVisibleChild = getChildAt(0);//返回的是当前屏幕中的第一个子view，非整个列表
            if (firstVisibleChild != null) {
                if (lastChild != null &&
                        getLayoutManager().getDecoratedBottom(lastChild) == getMeasuredHeight()-getPaddingBottom()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                //没有元素也允许刷新，but不允许上拉
                return false;
            }
        }
    }

}
