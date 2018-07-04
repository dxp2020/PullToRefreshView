package com.changf.pulltorefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.changf.pulltorefresh.base.PullToRefreshBase;
import com.changf.pulltorefresh.base.ViewOrientation;

public class PullToRefreshRecycleView extends PullToRefreshBase<RecyclerView> {

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

    private class MyRecyclerView extends RecyclerView implements ViewOrientation {
        private View emptyView;

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

        private void checkIfEmpty() {
            if (emptyView != null && getAdapter() != null) {
                final boolean emptyViewVisible =
                        getAdapter().getItemCount() == 0;
                emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
                setVisibility(emptyViewVisible ? GONE : VISIBLE);
            }
        }

        @Override
        public void setAdapter(Adapter adapter) {
            final Adapter oldAdapter = getAdapter();
            if (oldAdapter != null) {
                oldAdapter.unregisterAdapterDataObserver(observer);
            }
            super.setAdapter(adapter);
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }

            checkIfEmpty();
        }

        //设置没有内容时，提示用户的空布局
        public void setEmptyView(View emptyView) {
            this.emptyView = emptyView;
            checkIfEmpty();
        }

        final private AdapterDataObserver observer = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                checkIfEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.i(TAG, "onItemRangeInserted" + itemCount);
                checkIfEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                checkIfEmpty();
            }
        };
    }

    public void setEmptyView(View emptyView){
        ViewParent emptyViewParent = emptyView.getParent();
        //从原布局中移除
        if (null != emptyViewParent && emptyViewParent instanceof ViewGroup) {
            ((ViewGroup) emptyViewParent).removeView(emptyView);
        }
        //添加到刷新View所在的布局
        FrameLayout mRefreshableViewWrapper = getRefreshableViewWrapper();
        mRefreshableViewWrapper.addView(emptyView,convertEmptyViewLayoutParams(emptyView.getLayoutParams()));
        //设置emptyView
        ((MyRecyclerView)getRefreshView()).setEmptyView(emptyView);
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
}
