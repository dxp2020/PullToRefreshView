package com.dxp.swipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.dxp.utils.MotionEventUtils;

public class SwipeListView extends ListView{
    private Context mContext;
    private SwipeMenuCreator mSwipeMenuCreator;
    private SwipeDirection mSwipeDirection;

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

    public SwipeMenuCreator getSwipeMenuCreator() {
        return mSwipeMenuCreator;
    }

    public void setSwipeMenuCreator(SwipeMenuCreator mSwipeMenuCreator) {
        this.mSwipeMenuCreator = mSwipeMenuCreator;
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

    public void setSwipeDirection(SwipeDirection pSwipeDirection) {
        mSwipeDirection = pSwipeDirection;
    }

    public SwipeDirection getSwipeDirection() {
        return mSwipeDirection;
    }
}
