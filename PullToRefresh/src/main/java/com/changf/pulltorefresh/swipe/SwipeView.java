package com.changf.pulltorefresh.swipe;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;


public class SwipeView extends ViewGroup implements SwipeParent{
    private String TAG = "SwipeView";

    private Context context;
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
    private SwipeDirection swipeDirection = SwipeDirection.NONE;

    private Scroller mScroller;
    private View contentView;
    private SwipeMenu rightMenu;
    private SwipeMenu leftMenu;
    private OnOpenedMenuListener mOnOpenedMenuListener;
    private int position=-1;

    public SwipeView(Context context,View contentView) {
        super(context);
        this.contentView = contentView;
        init(context,null);
    }

    public SwipeView(Context context,View contentView,OnClickListener pOnClickListener) {
        super(context);
        this.contentView = contentView;
        init(context,pOnClickListener);
    }

    private void init(Context context,final OnClickListener pOnClickListener) {
        mScroller = new Scroller(context);
        addView(contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpen()){
                    smoothCloseMenu();
                    return;
                }
                if(pOnClickListener!=null){
                    pOnClickListener.onClick(SwipeView.this);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        contentView.measure(widthMeasureSpec, heightMeasureSpec);

        if (leftMenu!=null) {
            leftMenu.measure(MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                    contentView.getMeasuredHeight(), MeasureSpec.EXACTLY));
        }

        if (rightMenu!=null) {
            rightMenu.measure(MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
                    contentView.getMeasuredHeight(), MeasureSpec.EXACTLY));
        }

        setMeasuredDimension(contentView.getMeasuredWidth()+getLeftMenuWidth()+getRightMenuWidth(),contentView.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            if (leftMenu!=null) {
                leftMenu.layout(-leftMenu.getMeasuredWidth(),0,0,rightMenu.getMeasuredHeight());
            }
            if (rightMenu!=null) {
                rightMenu.layout(contentView.getMeasuredWidth(),0,contentView.getMeasuredWidth()+rightMenu.getMeasuredWidth(),rightMenu.getMeasuredHeight());
            }
            contentView.layout(0,0,contentView.getMeasuredWidth(),contentView.getMeasuredHeight());
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
                mLastX = ev.getX();
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
                        if(deltaX>0&&(swipeStatus==null||swipeStatus == SwipeDirection.RIGHT)){
                            moveLeftMenu(deltaX);
                            //滑出右侧菜单
                        }else if(deltaX<0&&(swipeStatus==null||swipeStatus == SwipeDirection.LEFT)){
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
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isRelesedFinger = true;
                //添加回弹效果
                if(getScrollX()==0){
                    swipeStatus = null;
                }else if(swipeStatus == SwipeDirection.LEFT){
                    if(Math.abs(getScrollX())<getRightMenuWidth()/2){
                        mScroller.startScroll(getScrollX(),0,-getScrollX(),0);
                        notifyClosed();
                    }else if(Math.abs(getScrollX())>=getRightMenuWidth()/2){
                        mScroller.startScroll(getScrollX(),0,getRightMenuWidth()-getScrollX(),0);
                        notifyOpened();
                    }
                    invalidate();
                }else if(swipeStatus == SwipeDirection.RIGHT){
                    if(Math.abs(getScrollX())<getLeftMenuWidth()/2){
                        mScroller.startScroll(getScrollX(),0,-getScrollX(),0);
                        notifyClosed();
                    }else if(Math.abs(getScrollX())>=getLeftMenuWidth()/2){
                        mScroller.startScroll(getScrollX(),0,-(getLeftMenuWidth()+getScrollX()),0);
                        notifyOpened();
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
        swipeStatus = SwipeDirection.RIGHT;
        //右滑
        if(deltaX>0){
            float distance = getLeftMenuWidth() + getScrollX();
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
        if(mOnOpenedMenuListener!=null){
            mOnOpenedMenuListener.startOpen(this);
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
        swipeStatus = SwipeDirection.LEFT;
        //左滑
        if(deltaX<0){
            float distance = getRightMenuWidth() - getScrollX();
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
        if(mOnOpenedMenuListener!=null){
            mOnOpenedMenuListener.startOpen(this);
        }
    }

    private int getLeftMenuWidth(){
        if(leftMenu!=null){
            return leftMenu.getMeasuredWidth();
        }
        return 0;
    }

    private int getRightMenuWidth(){
        if(rightMenu!=null){
            return rightMenu.getMeasuredWidth();
        }
        return 0;
    }

    public SwipeMenu getLeftMenu() {
        return leftMenu;
    }

    public SwipeMenu getRightMenu() {
        return rightMenu;
    }

    public void addLeftMenu(SwipeMenu leftMenu) {
        if(leftMenu==null){
            return;
        }
        this.leftMenu = leftMenu;
        this.leftMenu.setSwipeView(this);
        addView(leftMenu,getChildCount());
        requestLayout();
    }

    public void addRightMenu(SwipeMenu rightMenu) {
        if(rightMenu==null){
            return;
        }
        this.rightMenu = rightMenu;
        this.rightMenu.setSwipeView(this);
        addView(rightMenu,getChildCount());
        requestLayout();
    }

    public void smoothCloseMenu(){
        if((leftMenu!=null&&leftMenu.isOpen())||(rightMenu!=null&&rightMenu.isOpen())){
            mScroller.startScroll(getScrollX(),0,-getScrollX(),0);
            invalidate();
        }
    }

    public void smoothOpenMenu() {
        if(rightMenu!=null&&(swipeDirection==SwipeDirection.LEFT||swipeDirection==SwipeDirection.BOTH)){
            if(!rightMenu.isOpen()){
                mScroller.startScroll(getScrollX(),0,getRightMenuWidth()-getScrollX(),0);
                invalidate();
            }
        }else if(leftMenu!=null&&(swipeDirection==SwipeDirection.RIGHT)){
            if(!leftMenu.isOpen()){
                mScroller.startScroll(getScrollX(),0,-(getLeftMenuWidth()+getScrollX()),0);
                invalidate();
            }
        }
    }

    @Override
    public int getScrolledX() {
        return getScrollX();
    }

    public boolean isOpen(){
        if((leftMenu!=null&&leftMenu.isOpen())||(rightMenu!=null&&rightMenu.isOpen())){
            return true;
        }
        return false;
    }

    public void closeMenu() {
        //scrollBy x为正，则内容往左移，x为负内容往右移
        scrollBy(-getScrollX(), 0);
        notifyClosed();
    }

    protected void openMenu(SwipeDirection pSwipeDirection){
        if(isOpen()){
            return;
        }
        if(pSwipeDirection==SwipeDirection.LEFT){
            scrollBy(getRightMenuWidth(), 0);
        }else{
            scrollBy(-getLeftMenuWidth(), 0);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        contentView.setOnClickListener(l);
        contentView.setOnTouchListener(null);
    }

    private void notifyOpened(){
        if(mOnOpenedMenuListener!=null){
            mOnOpenedMenuListener.onOpened(this);
        }
    }

    private void notifyClosed(){
        if(mOnOpenedMenuListener!=null){
            mOnOpenedMenuListener.onClosed(this);
        }
    }

    public void setSwipeDirection(SwipeDirection mSwipeDirection){
        if(leftMenu!=null&&mSwipeDirection==SwipeDirection.RIGHT){
            swipeDirection=SwipeDirection.RIGHT;
        }else if(rightMenu!=null&&mSwipeDirection==SwipeDirection.LEFT){
            swipeDirection=SwipeDirection.LEFT;
        }else if(leftMenu!=null&&rightMenu!=null&&mSwipeDirection==SwipeDirection.BOTH){
            swipeDirection=SwipeDirection.BOTH;
        }
    }

    public SwipeDirection getOpendMenuDirection(){
        if(rightMenu!=null&&(getScrollX()-getRightMenuWidth()/2)>0){
            return SwipeDirection.LEFT;
        }else if(leftMenu!=null&&(getScrollX()+getLeftMenuWidth()/2)<0){
            return SwipeDirection.RIGHT;
        }
        return null;
    }

    public View getContentView() {
        return contentView;
    }

    public SwipeDirection getSwipeDirection(){
        return swipeDirection;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface OnClickListener{
        void onClick(SwipeView view);
    }

    public void setOnOpenedMenuListener(OnOpenedMenuListener mOnOpenedMenuListener) {
        this.mOnOpenedMenuListener = mOnOpenedMenuListener;
    }

    public interface OnOpenedMenuListener {
        void startOpen(SwipeView view);
        void onOpened(SwipeView view);
        void onClosed(SwipeView view);
    }
}
