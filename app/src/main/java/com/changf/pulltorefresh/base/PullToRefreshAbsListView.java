package com.changf.pulltorefresh.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;

public abstract class PullToRefreshAbsListView<T extends AbsListView> extends PullToRefreshBase<T> {
    
    public PullToRefreshAbsListView(Context context) {
        super(context);
    }

    public PullToRefreshAbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isScrolledTop(){
        AbsListView absListView = getRefreshView();
        View firstChild = absListView.getChildAt(0);//返回的是当前屏幕中的第一个子view，非整个列表
        if (firstChild != null) {
            int firstVisiblePos = absListView.getFirstVisiblePosition();//不必完全可见，当前屏幕中第一个可见的子view在整个列表的位置
            if (firstVisiblePos == 0 && firstChild.getTop()-absListView.getPaddingTop() == 0) {
                // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                return  true;
            } else {
                return  false;
            }
        } else {
            // 如果ListView中没有元素，也应该允许下拉刷新
            return  true;
        }
    }

    public boolean isScrolledBottom(){
        AbsListView absListView = getRefreshView();
        //返回的是当前屏幕中的第最后一个子view，非整个列表
        View lastChild = absListView.getChildAt(absListView.getLastVisiblePosition()-absListView.getFirstVisiblePosition());
        if (lastChild != null) {
            int lastVisiblePos = absListView.getLastVisiblePosition();//不必完全可见，当前屏幕中最后一个可见的子view在整个列表的位置
            if (lastVisiblePos == absListView.getAdapter().getCount()-1 && lastChild.getBottom() == absListView.getMeasuredHeight()-absListView.getPaddingBottom()) {
                // 如果最后一个元素的下边缘，距离父布局值为view的高度，就说明View滚动到了最底部，此时应该允许上拉加载
                return  true;
            } else {
                return  false;
            }
        } else {
            // 如果View中没有元素，也应该允许下拉刷新，但不允许上拉
            return  false;
        }
    }
}
