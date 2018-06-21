package com.dxp.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.pulltorefresh.base.ViewOrientation;

public class PullToRefreshWebView extends PullToRefreshBase<WebView> {
    private String TAG = "PullToRefreshWebView";

    public PullToRefreshWebView(Context context) {
        this(context, null);
    }

    public PullToRefreshWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected WebView setRefreshView(Context context, AttributeSet attrs) {
        return new MyWebView(context, attrs);
    }

    private class MyWebView extends WebView implements ViewOrientation {

        public MyWebView(Context context) {
            super(context);
        }

        public MyWebView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean isScrolledTop() {
            return getScrollY() == 0;
        }

        @Override
        public boolean isScrolledBottom() {
            /*float webcontent = getContentHeight()*getScale();//webview的高度
            float webnow = getHeight()+ getScrollY();//当前webview的高度*/
            if (getContentHeight() * getScale() - (getHeight() + getScrollY()) == 0)
                return true;
            return false;
        }
    }
}