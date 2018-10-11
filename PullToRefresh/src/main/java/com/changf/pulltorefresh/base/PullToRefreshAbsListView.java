package com.changf.pulltorefresh.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class PullToRefreshAbsListView<T extends AbsListView> extends PullToRefreshBase<T> {

    public PullToRefreshAbsListView(Context context) {
        super(context);
    }

    public PullToRefreshAbsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp) {
        FrameLayout.LayoutParams newLp = null;
        if (null != lp) {
            newLp = new FrameLayout.LayoutParams(lp);

            if (lp instanceof LinearLayout.LayoutParams) {
                newLp.gravity = ((LinearLayout.LayoutParams) lp).gravity;
            } else {
                newLp.gravity = Gravity.CENTER;
            }
        }
        return newLp;
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

    public void setEmptyView(View emptyView){
        emptyView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ViewParent emptyViewParent = emptyView.getParent();
        //从原布局中移除
        if (null != emptyViewParent && emptyViewParent instanceof ViewGroup) {
            ((ViewGroup) emptyViewParent).removeView(emptyView);
        }
        //添加到刷新View所在的布局
        FrameLayout mRefreshableViewWrapper = getRefreshableViewWrapper();
        mRefreshableViewWrapper.addView(emptyView,convertEmptyViewLayoutParams(emptyView.getLayoutParams()));
        //设置emptyView
        getRefreshView().setEmptyView(emptyView);
    }

    public void setAdapter(ListAdapter adapter) {
        getRefreshView().setAdapter(adapter);
    }

    @Override
    public void smoothToBottom() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getRefreshView().setSelection(ListView.FOCUS_DOWN);
            }
        });
    }
}
