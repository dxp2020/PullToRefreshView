package com.dxp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.dxp.R;
import com.dxp.pulltorefresh.PullToRefreshWebView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;

public class MainActivitySwipeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_swipe_view);

    }
}
