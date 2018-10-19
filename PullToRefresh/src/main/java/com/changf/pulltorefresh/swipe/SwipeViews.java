package com.changf.pulltorefresh.swipe;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SwipeViews extends ViewGroup implements SwipeParent{
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
    private SwipeMenuCreator mSwipeMenuCreator;
    private OnOpenedMenuListener mOnOpenedMenuListener;

    public SwipeViews(Context context) {
        this(context,null);
    }

    public SwipeViews(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        mScroller = new Scroller(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(contentView==null&&getChildCount()>0){
            contentView = getChildAt(0);
            contentView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
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
                leftMenu.layout(-leftMenu.getMeasuredWidth(),0,0,leftMenu.getMeasuredHeight());
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
                    if(leftMenu.getRight()==0&&(contentView.getRight()==rightMenu.getLeft())){
                        //滑出左侧菜单 ,加swipeStatus状态限制，是因为不允许，同时左滑右滑
                        if(deltaX>0&&(swipeStatus==null||swipeStatus == SwipeDirection.RIGHT)){
                            moveLeftMenu(deltaX);
                            //滑出右侧菜单
                        }else if(deltaX<0&&(swipeStatus==null||swipeStatus == SwipeDirection.LEFT)){
                            moveRightMenu(deltaX);
                        }
                        //已滑出左滑
                    }else if(leftMenu.getRight()>0){
                        moveLeftMenu(deltaX);
                        //已滑出右滑
                    }else if(rightMenu.getLeft()<contentView.getRight()){
                        moveRightMenu(deltaX);
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isRelesedFinger = true;
                int scrolledX = getScrolledX();
                //添加回弹效果
                if(scrolledX==0){
                    swipeStatus = null;
                }else if(swipeStatus == SwipeDirection.LEFT){
                    if(Math.abs(scrolledX)<getRightMenuWidth()/2){
                        mScroller.startScroll(rightMenu.getLeft(),0,scrolledX,0);
                        notifyClosed();
                    }else if(Math.abs(scrolledX)>=getRightMenuWidth()/2){
                        mScroller.startScroll(rightMenu.getLeft(),0,scrolledX-getRightMenuWidth(),0);
                        notifyOpened();
                    }
                    invalidate();
                }else if(swipeStatus == SwipeDirection.RIGHT){
                    if(Math.abs(scrolledX)<getLeftMenuWidth()/2){
                        mScroller.startScroll(leftMenu.getLeft(),0,-scrolledX,0);
                        notifyClosed();
                    }else if(Math.abs(scrolledX)>=getLeftMenuWidth()/2){
                        mScroller.startScroll(leftMenu.getLeft(),0,getLeftMenuWidth()-scrolledX,0);
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
            if(rightMenu!=null&&rightMenu.getLeft()<contentView.getRight()){
                rightMenu.offsetLeftAndRight(mScroller.getCurrX()-rightMenu.getLeft());
            }else if(leftMenu!=null&&leftMenu.getRight()>0){
                leftMenu.offsetLeftAndRight(mScroller.getCurrX()-leftMenu.getLeft());
            }
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
            int distance = contentView.getLeft()-leftMenu.getLeft();
            if(distance>Math.abs(deltaX)){
                leftMenu.offsetLeftAndRight((int) deltaX);
            }else{
                leftMenu.offsetLeftAndRight(distance);
            }
            //左滑
        }else{
            int distance = leftMenu.getRight();
            if(distance>Math.abs(deltaX)){
                leftMenu.offsetLeftAndRight((int) deltaX);
            }else{
                leftMenu.offsetLeftAndRight(-distance);
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
            int distance = getRightMenuWidth() - (contentView.getRight() - rightMenu.getLeft());
            if(distance>Math.abs(deltaX)){
                rightMenu.offsetLeftAndRight((int) deltaX);
            }else{
                rightMenu.offsetLeftAndRight(-distance);
            }
            //右滑
        }else{
            int distance = contentView.getRight() - rightMenu.getLeft();
            if(distance>deltaX){
                rightMenu.offsetLeftAndRight((int) deltaX);
            }else{
                rightMenu.offsetLeftAndRight(distance);
            }
        }
        if(mOnOpenedMenuListener!=null){
            mOnOpenedMenuListener.startOpen(this);
        }
    }

    @Override
    public void setOnClickListener(@Nullable View.OnClickListener l) {
        if(contentView==null&&getChildCount()>0){
            contentView = getChildAt(0);
        }
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }

    public void smoothCloseMenu(){
        if((rightMenu!=null&&rightMenu.getLeft()<contentView.getRight())){
            mScroller.startScroll(rightMenu.getLeft(),0,contentView.getRight()-rightMenu.getLeft(),0);
            invalidate();
        }else if((leftMenu!=null&&leftMenu.getRight()>0)){
            mScroller.startScroll(leftMenu.getLeft(),0,-leftMenu.getRight(),0);
            invalidate();
        }
    }

    public void closeMenu() {
        leftMenu.offsetLeftAndRight(-Math.abs(leftMenu.getMeasuredWidth()+leftMenu.getLeft()));
        rightMenu.offsetLeftAndRight(contentView.getRight()-rightMenu.getLeft());
        notifyClosed();
    }

    @Override
    public int getScrolledX() {
        if(rightMenu!=null&&rightMenu.getLeft()<contentView.getRight()){
            return contentView.getRight() - rightMenu.getLeft();
        }else if(leftMenu!=null&&leftMenu.getRight()>0){
            return leftMenu.getRight() - contentView.getLeft();
        }
        return 0;
    }

    public interface OnClickListener{
        void onClick(SwipeViews view);
    }

    public void setOnOpenedMenuListener(OnOpenedMenuListener mOnOpenedMenuListener) {
        this.mOnOpenedMenuListener = mOnOpenedMenuListener;
    }

    public interface OnOpenedMenuListener {
        void startOpen(SwipeViews view);
        void onOpened(SwipeViews view);
        void onClosed(SwipeViews view);
    }

    public SwipeMenuCreator getSwipeMenuCreator() {
        return mSwipeMenuCreator;
    }

    public void setSwipeMenuCreator(SwipeMenuCreator mSwipeMenuCreator) {
        if(mSwipeMenuCreator!=null){
            leftMenu = mSwipeMenuCreator.createLeftMenu();
            rightMenu = mSwipeMenuCreator.createRightMenu();
        }
        if(leftMenu==null&&rightMenu==null){
            swipeDirection = SwipeDirection.NONE;
        }else if(leftMenu!=null&&rightMenu==null){
            swipeDirection = SwipeDirection.RIGHT;
            leftMenu.setSwipeView(this);
            addView(leftMenu);
        }else if(leftMenu==null&&rightMenu!=null){
            swipeDirection = SwipeDirection.LEFT;
            rightMenu.setSwipeView(this);
            addView(rightMenu);
        }else if(leftMenu!=null&&rightMenu!=null){
            swipeDirection = SwipeDirection.BOTH;
            leftMenu.setSwipeView(this);
            rightMenu.setSwipeView(this);
            addView(leftMenu);
            addView(rightMenu);
        }
        this.mSwipeMenuCreator = mSwipeMenuCreator;
        requestLayout();
    }

    public void setSwipeDirection(SwipeDirection pSwipeDirection) {
        swipeDirection = pSwipeDirection;
    }

    public SwipeDirection getSwipeDirection() {
        return swipeDirection;
    }
}
