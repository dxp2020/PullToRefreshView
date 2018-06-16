package com.dxp.view;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.dxp.R;
import com.dxp.utils.MotionEventUtils;
import com.dxp.utils.ViewUtils;


/**
 * 支持ListView、GridView、RecycleView下拉刷新、上拉加载
 *
 * 遇到的问题
 * 1、progressbar 在布局文件中设置为gone 在代码中设置Visibility无效
 * 2、header、footer宽度是内容的宽高，宽度不是屏幕的宽度
 * 3、正在刷新状态下无法上滑到底部，正在加载状态下无法上拉到顶部
 * （因为在上拉、下拉的情况下，listView的高度不变，在屏幕中top和bottom位置发生了变化，有一部分离开了可视范围，这个问题可以解决但也可以忽略）
 * 4、GridView 添加一个元素，未添加一行的情况下，notifyDataSetChanged后没显示出来，触控一下屏幕之后显示出来了
 */
public class PullToRefreshView extends ViewGroup{
    private static final String TAG = "PullToRefreshView";
    private Context mContext;
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
    private PullToRefreshAndPushToLoadMoreListener mListener;

    /**
     * 是否释放了手指
     */
    private boolean isRelesedFinger = true;

    private static final float DEFAULT_RATIO = 2f;
    /**
     * 拖动阻力系数
     */
    private float ratio = DEFAULT_RATIO;

    private int screenHeight;


    public PullToRefreshView(Context context) {
        this(context, null);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context mContext) {
        mScroller = new Scroller(mContext);
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        header = LayoutInflater.from(mContext).inflate(R.layout.refresh_header2, null, false);
        progressBar = header.findViewById(R.id.progress_bar);
        arrow = header.findViewById(R.id.arrow);
        description = header.findViewById(R.id.description);
        arrow.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.default_ptr_flip ));

        footer = LayoutInflater.from(mContext).inflate(R.layout.loadmore_footer2, null, false);
        footerArrow = footer.findViewById(R.id.iv_footer_arrow);
        footerProgressBar = footer.findViewById(R.id.footer_progress_bar);
        footerDescription = footer.findViewById(R.id.footer_description);
        footerArrow.setImageBitmap(ViewUtils.rotateBitmap(BitmapFactory.decodeResource(getResources(),R.mipmap.default_ptr_flip),-180));

        touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mView = getChildAt(0);
        addView(header, 0);
        addView(footer,getChildCount());
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
        if(mode == Mode.DISABLED){
            return false;
        }
        MotionEventUtils.println(ev.getAction());
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                isRelesedFinger = false;
                mLastY = ev.getY();
                judgeIsTop();
                judgeIsBottom();
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
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - mLastY;
                mLastY = ev.getY();
                if((getScrollY()==0&&(isTop&&deltaY>0))){
                    handlePullUpAction(deltaY);
                    return true;
                }else if(getScrollY()<0&&isTop) {
                    handlePullUpAction(deltaY);
                    return true;
                }else if((getScrollY()==0&&(isBottom&&deltaY<0))){
                    handlePullDownAction(deltaY);
                    return true;
                }else if(getScrollY()>0&&isBottom) {
                    handlePullDownAction(deltaY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isRelesedFinger = true;
                if(isTop){
                    handleReleaseUpAction();
                }else if(isBottom){
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
    private void handlePullUpAction(float deltaY){
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
     * 处理下拉的动作
     *
     * getScrollY()为正数代表View在往上移动
     * scrollBy 要实现往上移动，y必须为正数
     * @param deltaY
     */
    private void handlePullDownAction(float deltaY){
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
            if(isRelesedFinger){
                if(isTop){
                    Log.e(TAG,"computeScroll()--updateHeaderView");
                    updateHeaderView();
                }else if(isBottom){
                    Log.e(TAG,"computeScroll()--updateFooterView");
                    updateFooterView();
                }
            }
        }
    }

    private void updateHeaderView() {
        Log.e(TAG,"updateHeaderView()");

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
        Log.e(TAG,"updateHeaderView()");

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


    /**
     * 根据当前View的滚动状态来设定 {@link #isBottom}
     * 的值，每次都需要在触摸事件中第一个执行，这样可以判断出当前应该是滚动View，还是应该进行上拉。
     */
    private void judgeIsBottom() {
        if (mView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) mView;
            //返回的是当前屏幕中的第最后一个子view，非整个列表
            View lastChild = absListView.getChildAt(absListView.getLastVisiblePosition()-absListView.getFirstVisiblePosition());
            if (lastChild != null) {
                int lastVisiblePos = absListView.getLastVisiblePosition();//不必完全可见，当前屏幕中最后一个可见的子view在整个列表的位置
                if (lastVisiblePos == absListView.getAdapter().getCount()-1 && lastChild.getBottom() == absListView.getMeasuredHeight()-mView.getPaddingBottom()) {
                    // 如果最后一个元素的下边缘，距离父布局值为view的高度，就说明View滚动到了最底部，此时应该允许上拉加载
                    isBottom = true;
                } else {
                    isBottom = false;
                }
            } else {
                // 如果View中没有元素，也应该允许下拉刷新，但不允许上拉
                isBottom = false;
            }
        } else if (mView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mView;
            View lastChild = recyclerView.getLayoutManager().findViewByPosition(recyclerView.getAdapter().getItemCount()-1);//lastChild不必须完全可见
            View firstVisibleChild = recyclerView.getChildAt(0);//返回的是当前屏幕中的第一个子view，非整个列表
            if (firstVisibleChild != null) {
                if (lastChild != null &&
                        recyclerView.getLayoutManager().getDecoratedBottom(lastChild) == recyclerView.getMeasuredHeight()-mView.getPaddingBottom()) {
                    isBottom = true;
                } else {
                    isBottom = false;
                }
            } else {
                //没有元素也允许刷新，but不允许上拉
                isBottom = false;
            }
        } else {
            isBottom = true;
        }
    }

    /**
     * 根据当前View的滚动状态来设定 {@link #isTop}
     * 的值，每次都需要在触摸事件中第一个执行，这样可以判断出当前应该是滚动View，还是应该进行下拉。
     */
    private void judgeIsTop() {
        if (mView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) mView;
            View firstChild = absListView.getChildAt(0);//返回的是当前屏幕中的第一个子view，非整个列表
            if (firstChild != null) {
                int firstVisiblePos = absListView.getFirstVisiblePosition();//不必完全可见，当前屏幕中第一个可见的子view在整个列表的位置
                if (firstVisiblePos == 0 && firstChild.getTop()-mView.getPaddingTop() == 0) {
                    // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                    isTop = true;
                } else {
                    isTop = false;
                }
            } else {
                // 如果ListView中没有元素，也应该允许下拉刷新
                isTop = true;
            }
        } else if (mView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mView;
            View firstChild = recyclerView.getLayoutManager().findViewByPosition(0);//firstChild不必须完全可见
            View firstVisibleChild = recyclerView.getChildAt(0);//返回的是当前屏幕中的第一个子view，非整个列表
            if (firstVisibleChild != null) {
                if (firstChild != null && recyclerView.getLayoutManager().getDecoratedTop(firstChild)-mView.getPaddingTop() == 0) {
                    isTop = true;
                } else {
                    isTop = false;
                }
            } else {
                //没有元素也允许刷新
                isTop = true;
            }
        } else {
            isTop = true;
        }
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
         * {@link #setRefreshing()}.
         */
        MANUAL_REFRESH_ONLY;
    }

    public void setOnRefreshAndLoadMoreListener(PullToRefreshAndPushToLoadMoreListener pListener) {
        mListener = pListener;
    }

    /**
     * 监听器，使用刷新和加载的地方应该注册此监听器来获取刷新回调。
     */
    public interface PullToRefreshAndPushToLoadMoreListener {
        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在主线程中调用的， 需要另开线程来进行耗时操作。
         */
        void onRefresh();
        /**
         * 加载更多时会去回调此方法，在方法内编写具体的加载更多逻辑。注意此方法是在主线程中调用的， 需要另开线程来进行耗时操作。
         */
        void onLoadMore();
    }
}