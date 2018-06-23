package com.dxp.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.dxp.R;
import com.dxp.pulltorefresh.base.PullDirection;
import com.dxp.pulltorefresh.base.ViewOrientation;
import com.dxp.utils.MotionEventUtils;

public class SwipeView extends ViewGroup {
    private String TAG = "SwipeView";

    /**
     * 上次手指按下时的屏幕横坐标
     */
    private float mLastX = -1;

    /**
     * 上次手指按下时的屏幕纵坐标
     */
    private float mLastY = -1;

    /**
     * 水平方向滑动最小距离算滑动
     */
    private int MAX_X = dp2px(3);

    private int swipeMenuWidth = dp2px(50);

    private int swipeViewHeight = dp2px(50);

    private Scroller mScroller;

    /**
     * 是否释放了手指
     */
    private boolean isRelesedFinger = true;

    /**
     * 滑动的状态
     */
    private SwipeDirection swipeStatus = null;

    /**
     * 滑动的方向
     */
    private SwipeDirection swipeDirection = SwipeDirection.BOTH;

    private View swipeContent;
    private View rightMenu;
    private View leftMenu;


    public SwipeView(Context context) {
        this(context,null);
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.SwipeViewLayout, 0, 0);
        String direction = a.getString(R.styleable.SwipeViewLayout_swipe_direction);
        if("left".equals(direction)){
            swipeDirection = SwipeDirection.LEFT;
        }else if("right".equals(direction)){
            swipeDirection = SwipeDirection.RIGHT;
        }else if("both".equals(direction)){
            swipeDirection = SwipeDirection.BOTH;
        }else{
            swipeDirection = SwipeDirection.NONE;
        }
        a.recycle();

        mScroller = new Scroller(context);

        swipeContent = new View(context);
        swipeContent.setBackgroundColor(0xFF009FD9);
        swipeContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        rightMenu = new View(context);
        rightMenu.setBackgroundColor(0xFFFF4081);

        leftMenu = new View(context);
        leftMenu.setBackgroundColor(0xFFFF4081);

        addView(leftMenu);
        addView(swipeContent);
        addView(rightMenu);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        swipeContent.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                swipeViewHeight, MeasureSpec.EXACTLY));

        leftMenu.measure(MeasureSpec.makeMeasureSpec(swipeMenuWidth,
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                swipeViewHeight, MeasureSpec.EXACTLY));

        rightMenu.measure(MeasureSpec.makeMeasureSpec(swipeMenuWidth,
                MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                swipeViewHeight, MeasureSpec.EXACTLY));

        setMeasuredDimension(swipeContent.getMeasuredWidth()+leftMenu.getMeasuredWidth()+rightMenu.getMeasuredWidth(),swipeContent.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            leftMenu.layout(-leftMenu.getMeasuredWidth(),0,0,rightMenu.getMeasuredHeight());
            swipeContent.layout(0,0,swipeContent.getMeasuredWidth(),swipeContent.getMeasuredHeight());
            rightMenu.layout(swipeContent.getMeasuredWidth(),0,swipeContent.getMeasuredWidth()+rightMenu.getMeasuredWidth(),rightMenu.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(swipeDirection==SwipeDirection.NONE){
            return false;
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                isRelesedFinger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(ev.getX()-mLastX);
                if(deltaX>MAX_X){
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                swipeStatus = null;
                isRelesedFinger = true;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                float deltaX = ev.getX()-mLastX;
                mLastY = ev.getY();
                //只往左滑
                if(swipeDirection==SwipeDirection.LEFT){
                    moveRightMenu(deltaX);
                //只往右滑
                }else if(swipeDirection==SwipeDirection.RIGHT){
                    moveLeftMenu(deltaX);
                //既可以往左滑，也可以往右划
                }else if(swipeDirection==SwipeDirection.BOTH) {
                    //未滑动
                    if(getScrollX()==0){
                        //滑出左侧菜单 ,加swipeStatus状态限制，是因为不允许，同时左滑右滑
                        if(deltaX>0&&(swipeStatus==null||swipeStatus == SwipeDirection.LEFT)){
                            moveLeftMenu(deltaX);
                        //滑出右侧菜单
                        }else if(deltaX<0&&(swipeStatus==null||swipeStatus == SwipeDirection.RIGHT)){
                            moveRightMenu(deltaX);
                        }
                    //已滑出左滑
                    }else if(getScrollX()<0){
                        moveLeftMenu(deltaX);
                    //已滑出右滑
                    }else if(getScrollX()>0){
                        moveRightMenu(deltaX);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                isRelesedFinger = true;
                //添加回弹效果
                if(getScrollX()==0){
                    swipeStatus = null;
                }else if(swipeStatus == SwipeDirection.LEFT){
                    if(Math.abs(getScrollX())<swipeMenuWidth/2){
                        mScroller.startScroll(getScrollX(),0,-getScrollX(),0);
                    }else if(Math.abs(getScrollX())>swipeMenuWidth/2){
                        mScroller.startScroll(getScrollX(),0,-(swipeMenuWidth+getScrollX()),0);
                    }
                    invalidate();
                }else if(swipeStatus == SwipeDirection.RIGHT){
                    if(Math.abs(getScrollX())<swipeMenuWidth/2){
                        mScroller.startScroll(getScrollX(),0,-getScrollX(),0);
                    }else if(Math.abs(getScrollX())>swipeMenuWidth/2){
                        mScroller.startScroll(getScrollX(),0,swipeMenuWidth-getScrollX(),0);
                    }
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }else{
            if(isRelesedFinger){
                swipeStatus = null;
            }
        }
    }

    /**
     * 移动左侧的menu
     *
     * distance 为menu可滑动距离
     * 右滑-getScrollX永远不会大于menu的宽度
     * @param deltaX
     */
    private void moveLeftMenu(float deltaX) {
        swipeStatus = SwipeDirection.LEFT;
        deltaX/=4;//添加阻尼系数
        //右滑
        if(deltaX>0){
            float distance = swipeMenuWidth + getScrollX();
            if(distance>deltaX){
                scrollBy((int) -deltaX, 0);
            }else if(distance<deltaX){
                scrollBy((int) -distance, 0);
            }
        //左滑
        }else{
            float distance = Math.abs(getScrollX());
            if(distance>Math.abs(deltaX)){
                scrollBy((int) -deltaX, 0);
            }else if(distance>0){
                scrollBy((int) distance, 0);
            }
        }
    }

    /**
     * 移动右侧的menu
     *
     * distance 为menu可滑动距离
     * 左滑getScrollX永远不会大于menu的宽度
     * @param deltaX
     */
    private void moveRightMenu(float deltaX) {
        swipeStatus = SwipeDirection.RIGHT;
        deltaX/=4;//添加阻尼系数
        //左滑
        if(deltaX<0){
            float distance = swipeMenuWidth - getScrollX();
            if(distance>Math.abs(deltaX)){
                scrollBy((int) -deltaX, 0);
            }else if(distance>0){
                scrollBy((int) distance, 0);
            }
        //右滑
        }else{
            float distance = getScrollX();
            if(distance>deltaX){
                scrollBy((int) -deltaX, 0);
            }else{
                scrollBy((int) -distance, 0);
            }
        }
    }

    public SwipeDirection getSwipeStatus() {
        return swipeStatus;
    }

    public void setSwipeStatus(SwipeDirection swipeStatus) {
        this.swipeStatus = swipeStatus;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
