package com.dxp.swipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dxp.utils.MotionEventUtils;

public class SwipeListView extends ListView{
    private Context mContext;
    private SwipeMenuCreator mSwipeMenuCreator;
    private SwipeDirection mSwipeDirection;
    private boolean isSetItemClick = false;

    public SwipeListView(Context context) {
        this(context,null);
    }

    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SwipeListAdapter(this,mContext,adapter){
            @Override
            public SwipeMenu createLeftMenu() {
                if (mSwipeMenuCreator!=null) {
                    return mSwipeMenuCreator.createLeftMenu();
                }
                return null;
            }
            @Override
            public SwipeMenu createRightMenu() {
                if (mSwipeMenuCreator!=null) {
                    return mSwipeMenuCreator.createRightMenu();
                }
                return null;
            }
        });
    }

    /**
     * 屏蔽掉多手指触控，避免打开多个item的menu
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_POINTER_2_DOWN:
                return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
        if(listener!=null){
            isSetItemClick = true;
        }else{
            isSetItemClick = false;
        }
    }

    public SwipeMenuCreator getSwipeMenuCreator() {
        return mSwipeMenuCreator;
    }

    public void setSwipeMenuCreator(SwipeMenuCreator mSwipeMenuCreator) {
        this.mSwipeMenuCreator = mSwipeMenuCreator;
    }

    public void setSwipeDirection(SwipeDirection pSwipeDirection) {
        mSwipeDirection = pSwipeDirection;
    }

    public SwipeDirection getSwipeDirection() {
        return mSwipeDirection;
    }

    public boolean isSetItemClick() {
        return isSetItemClick;
    }


}
