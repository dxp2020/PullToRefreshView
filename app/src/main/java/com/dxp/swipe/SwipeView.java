package com.dxp.swipe;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.dxp.R;
import com.dxp.pulltorefresh.base.ViewOrientation;
import com.dxp.utils.MotionEventUtils;

public class SwipeView extends ViewGroup {
    /**
     * 上次手指按下时的屏幕横坐标
     */
    private float mLastX = -1;
    /**
     * 上次手指按下时的屏幕纵坐标
     */
    private float mLastY = -1;

    private int MAX_Y = 5;

    private int MAX_X = 3;

    private View swipeContent;
    private View swipeMenu;

    public SwipeView(Context context) {
        this(context,null);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);

        swipeContent = new View(context);
        swipeContent.setBackgroundColor(0xFF009FD9);
        swipeContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        swipeMenu = new View(context);
        swipeMenu.setBackgroundColor(0xFFFF4081);

        addView(swipeContent);
        addView(swipeMenu);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        swipeContent.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                dp2px(50), MeasureSpec.EXACTLY));

        swipeMenu.measure(MeasureSpec.makeMeasureSpec(dp2px(50),
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                dp2px(50), MeasureSpec.EXACTLY));

        setMeasuredDimension(swipeContent.getMeasuredWidth()+swipeMenu.getMeasuredWidth(),swipeContent.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            swipeContent.layout(0,0,swipeContent.getMeasuredWidth(),swipeContent.getMeasuredHeight());
            swipeMenu.layout(swipeContent.getMeasuredWidth(),0,swipeContent.getMeasuredWidth()+swipeMenu.getMeasuredWidth(),swipeMenu.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        MotionEventUtils.println(ev.getAction(),"");
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(ev.getX()-mLastX);
                if(deltaX>MAX_X){
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(ev.getX()-mLastX);
                mLastY = ev.getY();
                if(deltaX>0){
                    moveMenu(deltaX);
                }
                return true;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(ev);
    }

    private void moveMenu(float deltaX) {
        scrollBy((int) deltaX, 0);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
