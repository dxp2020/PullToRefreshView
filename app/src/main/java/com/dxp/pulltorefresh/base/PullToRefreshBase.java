package com.dxp.pulltorefresh.base;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.dxp.R;
import com.dxp.utils.MotionEventUtils;
import com.dxp.utils.ViewUtils;

public abstract class PullToRefreshBase<T extends View>  extends ViewGroup {
    private String TAG = "PullToRefreshBase";

    private T refreshView;

    private Scroller mScroller;
    /**
     * 在被判定为滚动之前用户手指可以移动的最大值。
     */
    private int touchSlop;

    /**
     * 下拉头的高度
     */
    private int hideHeaderHeight;
    /**
     * 上拉底部的高度
     */
    private int hideFooterHeight;

    /**
     * 下拉头的View
     */
    private View header;
    /**
     * 上拉底部的View
     */
    private View footer;

    /**
     * 需要去刷新和加载的View
     */
    private View mView;
    /**
     * 本控件内容区域的宽高
     */
    private int maxWidth,maxHeight;

    /**
     * footer的进度条
     */
    private ProgressBar footerProgressBar;
    /**
     * footer的进度条
     */
    private ImageView footerArrow;
    /**
     * footer的文字描述
     */
    private TextView footerDescription;
    /**
     * 刷新时显示的进度条
     */
    private ProgressBar progressBar;

    /**
     * 指示下拉和释放的箭头
     */
    private ImageView arrow;

    /**
     * 指示下拉和释放的文字描述
     */
    private TextView description;

    private Mode mode = Mode.BOTH;

    /**
     * 当前是否在view的顶部，只有View滚动到头的时候才允许下拉
     */
    private boolean isTop;
    /**
     * 当前是否在view的底部，只有View滚动到底的时候才允许上拉
     */
    private boolean isBottom;
    /**
     * 上次手指按下时的屏幕纵坐标
     */
    private float mLastY = -1;
    /**
     * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
     * STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
     */
    private int currentStatus = STATUS_REFRESH_FINISHED;
    /**
     * 下拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 0;

    /**
     * 释放立即刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 1;

    /**
     * 正在刷新状态
     */
    public static final int STATUS_REFRESHING = 2;

    /**
     * 刷新完成或未刷新状态
     */
    public static final int STATUS_REFRESH_FINISHED = 3;

    /**
     * 滚动结束
     */
    public static final int ACTION_SCROLL_FINISHED = 10;

    /**
     * 当前处理什么状态，STATUS_LOAD_NORMAL,STATUS_RELEASE_LOAD, STATUS_LOADING, STATUS_LOAD_FINISHED
     */
    private int currentFooterStatus=STATUS_LOAD_FINISHED;
    /**
     * 上拉状态
     */
    public static final int STATUS_LOAD_NORMAL = 4;
    /**
     * 放开加载更多
     */
    public static final int STATUS_RELEASE_LOAD = 5;
    /**
     * 正在加载状态
     */
    public static final int STATUS_LOADING = 6;
    /**
     * 加载结束
     */
    public static final int STATUS_LOAD_FINISHED = 7;

    /**
     * 下拉刷新上拉加载的回调接口
     */
    private RefreshLoadListener mListener;

    /**
     * 是否释放了手指
     */
    private boolean isRelesedFinger = true;

    private static final float DEFAULT_RATIO = 2f;
    /**
     * 拖动阻力系数
     */
    private float ratio = DEFAULT_RATIO;

    /**
     * 上拉、下拉方向
     */
    private PullDirection pullDirection = PullDirection.NONE;//考虑到上拉、下拉方向因素，是因为webview中存在isTop、isDown均为true的情况

    private int screenHeight;


    public PullToRefreshBase(Context context) {
        this(context,null);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        refreshView = setRefreshView(context,attrs);

        mScroller = new Scroller(context);
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        header = LayoutInflater.from(context).inflate(R.layout.refresh_header, null, false);
        progressBar = header.findViewById(R.id.progress_bar);
        arrow = header.findViewById(R.id.arrow);
        description = header.findViewById(R.id.description);
        arrow.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.default_ptr_flip ));

        footer = LayoutInflater.from(context).inflate(R.layout.loadmore_footer, null, false);
        footerArrow = footer.findViewById(R.id.iv_footer_arrow);
        footerProgressBar = footer.findViewById(R.id.footer_progress_bar);
        footerDescription = footer.findViewById(R.id.footer_description);
        footerArrow.setImageBitmap(ViewUtils.rotateBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.default_ptr_flip),-180));

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        //放在onAttachedToWindow中添加也可以，在onAttachedToWindow可以获取到ViewGroup的child数量
        addView(header);
        addView(refreshView,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        addView(footer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        maxWidth = 0;
        maxHeight = 0;
        int childCount = getChildCount();
        for(int i=0;i<childCount;i++){
            View child = getChildAt(i);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            maxWidth = Math.max(maxWidth,child.getMeasuredWidth());
            maxHeight += child.getMeasuredHeight();
        }
        setMeasuredDimension(maxWidth+getPaddingLeft()+getPaddingRight(),maxHeight+getPaddingTop()+getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            if (hideHeaderHeight==0) {
                hideHeaderHeight = -header.getMeasuredHeight();
            }
            if (hideFooterHeight==0) {
                hideFooterHeight = footer.getMeasuredHeight();
            }
            int childCount = getChildCount();
            int top = hideHeaderHeight+getPaddingTop();
            for(int i=0;i<childCount;i++){
                View child = getChildAt(i);
                child.layout(getPaddingLeft(),top,maxWidth+getPaddingLeft(),child.getMeasuredHeight()+top);
                top+=child.getMeasuredHeight();
            }
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        MotionEventUtils.println(ev.getAction());
        if(mode == Mode.DISABLED||mScroller.computeScrollOffset()){
            return false;
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                isRelesedFinger = false;
                mLastY = ev.getY();
                isTop = ((ViewOrientation)refreshView).isScrolledTop();
                isBottom = ((ViewOrientation)refreshView).isScrolledBottom();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - mLastY;//大于0是下拉，小于0是上拉
                //可以处理滑动了
                if(Math.abs(deltaY)>touchSlop){
                    //允许下拉显示header或者上拉显示footer，交由onTouch处理
                    if(getScrollY()==0&&((isTop&&deltaY>0)||(isBottom&&deltaY<0))){
                        if (isTop&&(mode == Mode.PULL_FROM_START||mode == Mode.BOTH)) {
                            return true;
                        }else if (isBottom&&(mode == Mode.PULL_FROM_END||mode == Mode.BOTH)) {
                            return true;
                        }
                        //正在刷新的时候下拉
                    }else if(getScrollY()<0&&deltaY>0&&isTop) {
                        if (mode == Mode.PULL_FROM_START||mode == Mode.BOTH) {
                            return true;
                        }
                        //正在加载的时候上拉
                    }else if(getScrollY()>0&&deltaY<0&&isBottom) {
                        if (mode == Mode.PULL_FROM_END||mode == Mode.BOTH) {
                            return true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isRelesedFinger = true;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        MotionEventUtils.println(ev.getAction(),isTop+"",isBottom+"",getScrollY()+"");
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - mLastY;
                mLastY = ev.getY();
                if((getScrollY()==0&&(isTop&&deltaY>0))){
                    handlePullDownAction(deltaY);
                    return true;
                }else if(getScrollY()<0&&isTop) {
                    handlePullDownAction(deltaY);
                    return true;
                }else if((getScrollY()==0&&(isBottom&&deltaY<0))){
                    handlePullUpAction(deltaY);
                    return true;
                }else if(getScrollY()>0&&isBottom) {
                    handlePullUpAction(deltaY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isRelesedFinger = true;
                //考虑到上拉、下拉方向因素，是因为webview中存在isTop、isDown均为true的情况
                if(isTop&&pullDirection==PullDirection.DOWN){
                    handleReleaseUpAction();
                }else if(isBottom&&pullDirection==PullDirection.UP){
                    handleReleaseDownAction();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void handleReleaseUpAction() {
        if((hideHeaderHeight-getScrollY())>=0){
            mScroller.startScroll(0,getScrollY(),0,hideHeaderHeight-getScrollY());
            invalidate();
            //下拉高度未达到headerView的高度情况下，并且不是正在刷新的情况下，回弹隐藏headerview
        }else if(currentStatus != STATUS_REFRESHING){
            mScroller.startScroll(0,getScrollY(),0,-getScrollY());
            invalidate();
        }
    }

    private void handleReleaseDownAction() {
        if((getScrollY()-hideFooterHeight)>=0){
            mScroller.startScroll(0,getScrollY(),0,hideFooterHeight-getScrollY());
            invalidate();
            //上拉高度未达到footerView的高度情况下，并且不是正在加载的情况下，回弹隐藏footerview
        }else if(currentFooterStatus != STATUS_LOADING){
            mScroller.startScroll(0,getScrollY(),0,-getScrollY());
            invalidate();
        }
    }


    /**
     * 处理下拉的动作
     *
     * getScrollY()为负数代表View在往下移动
     * scrollBy 要实现往下移动，y必须为负数
     * @param deltaY
     */
    private void handlePullDownAction(float deltaY){
        pullDirection = PullDirection.DOWN;

        //根据下拉的高度设置阻尼系数
        if(Math.abs(getScrollY())>=-hideHeaderHeight){
            ratio = (1+Math.abs(getScrollY())/(float)screenHeight)*DEFAULT_RATIO;
        }else{
            ratio = DEFAULT_RATIO;
        }
        int dy=(int) (deltaY / ratio);
        scrollBy(0, -dy);

        updateHeaderView();
    }

    /**
     * 处理上拉的动作
     *
     * getScrollY()为正数代表View在往上移动
     * scrollBy 要实现往上移动，y必须为正数
     * @param deltaY
     */
    private void handlePullUpAction(float deltaY){
        pullDirection = PullDirection.UP;

        //根据上拉的高度设置阻尼系数
        if(Math.abs(getScrollY())>=hideFooterHeight){
            ratio = (1+Math.abs(getScrollY())/(float)screenHeight)*DEFAULT_RATIO;
        }else{
            ratio = DEFAULT_RATIO;
        }
        int dy=(int) (deltaY / ratio);
        scrollBy(0, -dy);

        updateFooterView();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }else{
            Log.e(TAG,"omputeScroll()");
            if(isRelesedFinger){
                if(isTop&&pullDirection==PullDirection.DOWN){
                    Log.e(TAG,"computeScroll()--updateHeaderView");
                    updateHeaderView();
                }else if(isBottom&&pullDirection==PullDirection.UP){
                    Log.e(TAG,"computeScroll()--updateFooterView");
                    updateFooterView();
                }
                pullDirection = PullDirection.NONE;
            }
        }
    }


    private void updateHeaderView() {
        if(currentStatus==STATUS_REFRESH_FINISHED){
            currentStatus = STATUS_PULL_TO_REFRESH;
            description.setText(getContext().getString(R.string.pull_to_refresh));
            progressBar.setVisibility(View.GONE);
            arrow.setVisibility(View.VISIBLE);
            
        }else if(currentStatus==STATUS_PULL_TO_REFRESH){
            if(Math.abs(getScrollY())>header.getHeight()){
                description.setText(getContext().getString(R.string.release_to_refresh));
                currentStatus = STATUS_RELEASE_TO_REFRESH;
                rotateArrow();
            }else if(isRelesedFinger&&getScrollY()==hideHeaderHeight){
                description.setText(getContext().getString(R.string.refreshing));
                currentStatus = STATUS_REFRESHING;
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                if(mListener!=null){
                    mListener.onRefresh();
                }
            }
        }else if(currentStatus==STATUS_RELEASE_TO_REFRESH){
            if(Math.abs(getScrollY())<header.getHeight()){
                description.setText(getContext().getString(R.string.pull_to_refresh));
                currentStatus = STATUS_PULL_TO_REFRESH;
                rotateArrow();
                
            }else if(isRelesedFinger&&getScrollY()==hideHeaderHeight){
                description.setText(getContext().getString(R.string.refreshing));
                currentStatus = STATUS_REFRESHING;
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                if(mListener!=null){
                    mListener.onRefresh();
                }
            }
        }else if(currentStatus==STATUS_REFRESHING){
            if(progressBar.getVisibility()==View.GONE
                    ||progressBar.getVisibility()==View.INVISIBLE){
                description.setText(getContext().getString(R.string.refreshing));
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                if(mListener!=null){
                    mListener.onRefresh();
                }
                
            }
        }
    }

    public void updateFooterView(){

        if(currentFooterStatus==STATUS_LOAD_FINISHED){
            currentFooterStatus = STATUS_LOAD_NORMAL;
            footerDescription.setText(getContext().getString(R.string.load_more_normal));
            footerProgressBar.setVisibility(View.GONE);
            footerArrow.setVisibility(View.VISIBLE);
            
        }else if(currentFooterStatus==STATUS_LOAD_NORMAL){
            if(Math.abs(getScrollY())>=footer.getHeight()){
                footerDescription.setText(getContext().getString(R.string.load_more_release));
                currentFooterStatus = STATUS_RELEASE_LOAD;
                rotateFooterArrow();
                
            }
        }else if(currentFooterStatus==STATUS_RELEASE_LOAD){
            if(Math.abs(getScrollY())<header.getHeight()){
                footerDescription.setText(getContext().getString(R.string.load_more_normal));
                currentFooterStatus = STATUS_LOAD_NORMAL;
                rotateFooterArrow();
                
            }else if(isRelesedFinger&&getScrollY()==hideFooterHeight){
                currentFooterStatus = STATUS_LOADING;
                setLoadingView();
            }
        }else if(currentFooterStatus==STATUS_LOADING){
            setLoadingView();
        }
    }

    private void setLoadingView(){
        if(footerProgressBar.getVisibility()==View.GONE
                ||footerProgressBar.getVisibility()==View.INVISIBLE){
            footerDescription.setText(getContext().getString(R.string.load_more_loading));
            footerProgressBar.setVisibility(View.VISIBLE);
            footerArrow.clearAnimation();
            footerArrow.setVisibility(View.GONE);
            if(mListener!=null){
                mListener.onLoadMore();
            }
            
        }
    }


    public void setRefreshCompleted() {
        currentStatus = STATUS_REFRESH_FINISHED;
        mScroller.startScroll(0,getScrollY(),0,-getScrollY());
        invalidate();
    }

    public void setRefreshing(){
        if(mode==Mode.DISABLED){
            return;
        }
        currentStatus = STATUS_REFRESHING;
        mScroller.startScroll(0,0,0,hideHeaderHeight);
        invalidate();
    }

    public void setLoadCompleted(){
        currentFooterStatus = STATUS_LOAD_FINISHED;
        mScroller.startScroll(0,getScrollY(),0,-getScrollY());
        invalidate();
    }

    public void setLoading(){
        if(mode==Mode.DISABLED){
            return;
        }
        currentFooterStatus = STATUS_LOADING;
        mScroller.startScroll(0,0,0,hideFooterHeight);
        invalidate();
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
    private void rotateArrow() {
        arrow.clearAnimation();
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(300);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }

    /**
     * 根据当前的状态来旋转箭头。
     */
    private void rotateFooterArrow() {
        footerArrow.clearAnimation();
        float pivotX = footerArrow.getWidth() / 2f;
        float pivotY = footerArrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentFooterStatus == STATUS_LOAD_NORMAL) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentFooterStatus == STATUS_RELEASE_LOAD) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, pivotX, pivotY);
        animation.setDuration(300);
        animation.setFillAfter(true);
        footerArrow.startAnimation(animation);
    }

    protected abstract T setRefreshView(Context context,AttributeSet attrs);

    public T getRefreshView(){
        return refreshView;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum Mode {

        /**
         * Disable all Pull-to-Refresh gesture and Refreshing handling
         */
        DISABLED,

        /**
         * Only allow the user to Pull from the start of the Refreshable View to
         * refresh. The start is either the Top or Left, depending on the
         * scrolling direction.
         */
        PULL_FROM_START,

        /**
         * Only allow the user to Pull from the end of the Refreshable View to
         * refresh. The start is either the Bottom or Right, depending on the
         * scrolling direction.
         */
        PULL_FROM_END,

        /**
         * Allow the user to both Pull from the start, from the end to refresh.
         */
        BOTH,

        /**
         * Disables Pull-to-Refresh gesture handling, but allows manually
         * setting the Refresh state via
         * {@lik #setRefreshing()}.
         */
        MANUAL_REFRESH_ONLY;
    }

    public interface RefreshLoadListener {
        void onRefresh();
        void onLoadMore();
    }

    public void setOnRefreshLoadListener(RefreshLoadListener pListener) {
        mListener = pListener;
    }
}
