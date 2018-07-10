package com.changf.pulltorefresh.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.changf.pulltorefresh.R;
import com.changf.pulltorefresh.utils.MotionEventUtils;
import com.changf.pulltorefresh.utils.ViewUtils;


public abstract class PullToRefreshBase<T extends View>  extends LinearLayout {
    public final String TAG = getClass().getSimpleName();

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
    private FrameLayout mRefreshableViewWrapper;

    /**
     * 需要去刷新和加载的View
     */
    private T refreshView;

    /**
     * 本控件内容区域的宽高
     */
    private int maxWidth,maxHeight;

    /**
     * 下拉头
     */
    private LinearLayout innerHeader;

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
    /**
     * 上拉头
     */
    private LinearLayout innerFooter;
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
     * 屏幕高度
     */
    private int screenHeight;

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
     * 需要去刷新和加载View的原始高度
     */
    private int refreshableViewWrapperOriginalHeight;

    /**
     * header的原始高度
     */
    private int headerOriginalHeight;

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
     * 拖动阻力系数
     */
    private static final float DEFAULT_RATIO = 2f;

    /**
     * 下拉刷新上拉加载的回调接口
     */
    private RefreshLoadListener mListener;

    /**
     * 是否释放了手指
     */
    private boolean isRelesedFinger = true;

    /**
     * 是否正在滚动
     */
    private boolean isScrolling = false;

    /**
     * 当前是否在view的顶部，只有View滚动到头的时候才允许下拉
     */
    private boolean isTop;
    /**
     * 当前是否在view的底部，只有View滚动到底的时候才允许上拉
     */
    private boolean isBottom;

    /**
     * 上拉、下拉方向
     */
    private PullDirection pullDirection = PullDirection.NONE;//考虑到上拉、下拉方向因素，是因为webview中存在isTop、isDown均为true的情况

    private Mode mode = Mode.BOTH;

    private Scroller mScroller;


    public PullToRefreshBase(Context context) {
        this(context,null);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RefreshView, 0, 0);
        int header_background = a.getResourceId(R.styleable.RefreshView_header_background,0);
        int footer_background = a.getResourceId(R.styleable.RefreshView_footer_background,0);
        a.recycle();

        mScroller = new Scroller(context);
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        header = LayoutInflater.from(context).inflate(R.layout.refresh_header, null, false);
        header.setBackgroundResource(header_background);
        innerHeader = header.findViewById(R.id.ll_inner_header);
        progressBar = header.findViewById(R.id.progress_bar);
        arrow = header.findViewById(R.id.arrow);
        description = header.findViewById(R.id.description);
        arrow.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.default_ptr_flip ));

        footer = LayoutInflater.from(context).inflate(R.layout.loadmore_footer, null, false);
        footer.setBackgroundResource(footer_background);
        innerFooter = footer.findViewById(R.id.ll_inner_footer);
        footerArrow = footer.findViewById(R.id.iv_footer_arrow);
        footerProgressBar = footer.findViewById(R.id.footer_progress_bar);
        footerDescription = footer.findViewById(R.id.footer_description);
        footerArrow.setImageBitmap(ViewUtils.rotateBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.default_ptr_flip),-180));

        setOrientation(LinearLayout.VERTICAL);

        addView(header,createHeaderParams());
        addView(addRefreshableView(context,attrs));
        addView(footer,createFooterParams());

        hideHeaderHeight = -measureHeight(innerHeader);
        hideFooterHeight = measureHeight(innerFooter);
    }

    private FrameLayout addRefreshableView(Context context,AttributeSet attrs) {
        refreshView = setRefreshView(context,attrs);
        mRefreshableViewWrapper = new FrameLayout(context);
        mRefreshableViewWrapper.addView(refreshView,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        mRefreshableViewWrapper.setLayoutParams(params);
        return mRefreshableViewWrapper;
    }

    private LayoutParams createHeaderParams() {
        LayoutParams params = createFooterParams();
        params.topMargin = -params.height;
        return params;
    }

    private LayoutParams createFooterParams(){
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,Math.round(screenHeight/DEFAULT_RATIO));
    }

    private int measureHeight(View view){
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        return view.getMeasuredHeight();
    }

//    使用ViewGroup的情况下，ListView默认无数据，请求加载出数据后，无法显示数据，暂时找不到解决的办法
//    因此放弃使用继承ViewGroup，改成LinearLayout
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        maxWidth = 0;
//        maxHeight = 0;
//        int childCount = getChildCount();
//        for(int i=0;i<childCount;i++){
//            View child = getChildAt(i);
//            if(child == header || child == footer){
//                measureChild(child,widthMeasureSpec,MeasureSpec.makeMeasureSpec(Math.round(screenHeight/DEFAULT_RATIO),MeasureSpec.EXACTLY));
//            }else{
//                measureChild(child,widthMeasureSpec,heightMeasureSpec);
//            }
//            maxWidth = Math.max(maxWidth,child.getMeasuredWidth());
//            maxHeight += child.getMeasuredHeight();
//        }
//        setMeasuredDimension(maxWidth+getPaddingLeft()+getPaddingRight(),maxHeight+getPaddingTop()+getPaddingBottom());
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        if(changed){
//            if (hideHeaderHeight==0) {
//                hideHeaderHeight = -innerHeader.getMeasuredHeight();
//            }
//            if (hideFooterHeight==0) {
//                hideFooterHeight = innerFooter.getMeasuredHeight();
//            }
//            int childCount = getChildCount();
//            int top = -header.getMeasuredHeight()+getPaddingTop();
//            for(int i=0;i<childCount;i++){
//                View child = getChildAt(i);
//                child.layout(getPaddingLeft(),top,maxWidth+getPaddingLeft(),child.getMeasuredHeight()+top);
//                top+=child.getMeasuredHeight();
//            }
//        }
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e(TAG,"onInterceptTouchEvent-->"+ MotionEventUtils.getPrintStr(ev.getAction()));
        //正在滚动的情况下，屏蔽手势
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
//        Log.e(TAG,"onTouchEvent-->"+ MotionEventUtils.getPrintStr(ev.getAction()));
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

        int dy=(int) (deltaY / DEFAULT_RATIO);
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

        int dy=(int) (deltaY / DEFAULT_RATIO);
        scrollBy(0, -dy);

        updateFooterView();
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){
            isScrolling = true;
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }else{
            if(isScrolling){
                if(pullDirection==PullDirection.DOWN){
                    updateHeaderView();
                }else if(pullDirection==PullDirection.UP){
                    updateFooterView();
                }
                pullDirection = PullDirection.NONE;
                isScrolling = false;
            }
            Log.e(TAG,"--->"+mRefreshableViewWrapper.getMeasuredHeight());
        }
    }

    private void updateHeaderView() {
        if(currentStatus==STATUS_REFRESH_FINISHED){
            currentStatus = STATUS_PULL_TO_REFRESH;
            description.setText(getContext().getString(R.string.pull_to_refresh));
            progressBar.setVisibility(View.GONE);
            arrow.setVisibility(View.VISIBLE);

        }else if(currentStatus==STATUS_PULL_TO_REFRESH){
            if(Math.abs(getScrollY())>innerHeader.getHeight()){
                description.setText(getContext().getString(R.string.release_to_refresh));
                currentStatus = STATUS_RELEASE_TO_REFRESH;
                rotateArrow();
            }else if(isRelesedFinger&&getScrollY()==hideHeaderHeight){
                description.setText(getContext().getString(R.string.refreshing));
                currentStatus = STATUS_REFRESHING;
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                onRefreshEvent();
            }
        }else if(currentStatus==STATUS_RELEASE_TO_REFRESH){
            if(Math.abs(getScrollY())<innerHeader.getHeight()){
                description.setText(getContext().getString(R.string.pull_to_refresh));
                currentStatus = STATUS_PULL_TO_REFRESH;
                rotateArrow();

            }else if(isRelesedFinger&&getScrollY()==hideHeaderHeight){
                description.setText(getContext().getString(R.string.refreshing));
                currentStatus = STATUS_REFRESHING;
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                onRefreshEvent();
            }
        }else if(currentStatus==STATUS_REFRESHING){
            if(progressBar.getVisibility()==View.GONE
                    ||progressBar.getVisibility()==View.INVISIBLE){
                description.setText(getContext().getString(R.string.refreshing));
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                onRefreshEvent();
            }
        }
    }

    private void updateRefreshableViewWrapperHeight(){
        if(refreshableViewWrapperOriginalHeight==0){
            refreshableViewWrapperOriginalHeight = mRefreshableViewWrapper.getMeasuredHeight();
        }
        if(headerOriginalHeight==0){
            headerOriginalHeight = header.getMeasuredHeight();
        }
        //如果显示了emptyView，则不设置大小
        if(mRefreshableViewWrapper.getChildCount()>0&&
                (mRefreshableViewWrapper.getChildAt(0) instanceof ListView||
                        mRefreshableViewWrapper.getChildAt(0) instanceof GridView)){
           int visibility  = mRefreshableViewWrapper.getChildAt(0).getVisibility();
           if(visibility == View.GONE){
               return;
           }
        }
        if(currentStatus == STATUS_REFRESHING){
            setRefreshableViewWrapperHeight(refreshableViewWrapperOriginalHeight - innerHeader.getMeasuredHeight());
        }
        if(currentFooterStatus == STATUS_LOADING){
            setRefreshableViewWrapperHeight(refreshableViewWrapperOriginalHeight - innerFooter.getMeasuredHeight());
            setHeaderViewHeight(headerOriginalHeight+innerFooter.getHeight());
        }
        if((currentStatus == STATUS_REFRESH_FINISHED||currentStatus == STATUS_PULL_TO_REFRESH) &&
                (currentFooterStatus == STATUS_LOAD_NORMAL||currentFooterStatus == STATUS_LOAD_FINISHED)){
            setRefreshableViewWrapperHeight(refreshableViewWrapperOriginalHeight);
            setHeaderViewHeight(headerOriginalHeight);
        }
    }

    private void setRefreshableViewWrapperHeight(int height){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mRefreshableViewWrapper.getLayoutParams();
        params.height = height;
        requestLayout();
    }

    private void setHeaderViewHeight(int height){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) header.getLayoutParams();
        params.height = height;
        requestLayout();
    }

    public void updateFooterView(){
        if(currentFooterStatus==STATUS_LOAD_FINISHED){
            currentFooterStatus = STATUS_LOAD_NORMAL;
            footerDescription.setText(getContext().getString(R.string.load_more_normal));
            footerProgressBar.setVisibility(View.GONE);
            footerArrow.setVisibility(View.VISIBLE);

        }else if(currentFooterStatus==STATUS_LOAD_NORMAL){
            if(Math.abs(getScrollY())>=innerFooter.getHeight()){
                footerDescription.setText(getContext().getString(R.string.load_more_release));
                currentFooterStatus = STATUS_RELEASE_LOAD;
                rotateFooterArrow();

            }
        }else if(currentFooterStatus==STATUS_RELEASE_LOAD){
            if(Math.abs(getScrollY())<innerFooter.getHeight()){
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
            onLoadEvent();
        }
    }


    public void setRefreshCompleted() {
        //防止加载的时候，调用刷新完成，也隐藏下拉头的问题
        if(currentFooterStatus==STATUS_LOADING){
            return;
        }
        pullDirection = PullDirection.DOWN;
        currentStatus = STATUS_REFRESH_FINISHED;
        mScroller.startScroll(0,getScrollY(),0,-getScrollY());
        invalidate();
        updateRefreshableViewWrapperHeight();
    }

    public void setRefreshing(){
        if(mode== Mode.DISABLED){
            return;
        }
        isTop = true;
        pullDirection=PullDirection.DOWN;
        currentStatus = STATUS_REFRESHING;
        mScroller.startScroll(0,0,0,hideHeaderHeight);
        invalidate();
    }

    public void setLoadCompleted(){
        //防止刷新的时候，调用加载完成，也隐藏上拉头的问题
        if(currentStatus==STATUS_REFRESHING){
            return;
        }
        pullDirection=PullDirection.UP;
        currentFooterStatus = STATUS_LOAD_FINISHED;
        mScroller.startScroll(0,getScrollY(),0,-getScrollY());
        invalidate();
        updateRefreshableViewWrapperHeight();
    }

    public void setLoading(){
        if(mode== Mode.DISABLED){
            return;
        }
        isBottom = true;
        pullDirection=PullDirection.UP;
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

    public FrameLayout getRefreshableViewWrapper() {
        return mRefreshableViewWrapper;
    }

    private void onRefreshEvent(){
        if(mListener!=null){
            mListener.onRefresh();
        }

        updateRefreshableViewWrapperHeight();
    }

    private void onLoadEvent(){
        if(mListener!=null){
            mListener.onLoadMore();
        }
        updateRefreshableViewWrapperHeight();
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum Mode {

        DISABLED,

        PULL_FROM_START,

        PULL_FROM_END,

        BOTH,

        MANUAL_REFRESH_ONLY;
    }

    public interface RefreshLoadListener {
        void onRefresh();
        void onLoadMore();
    }

    public void setOnRefreshLoadListener(RefreshLoadListener pListener) {
        mListener = pListener;
    }

    protected abstract T setRefreshView(Context context, AttributeSet attrs);

    public T getRefreshView(){
        return refreshView;
    }
}
