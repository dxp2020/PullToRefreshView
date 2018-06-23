package com.dxp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dxp.R;
import com.dxp.pulltorefresh.PullToRefreshListView;
import com.dxp.pulltorefresh.PullToRefreshWebView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

public class MainActivityWebView extends AppCompatActivity {
    private WebView lv_refreshview;
    private PullToRefreshWebView ptrv_pull_refresh;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                ptrv_pull_refresh.setRefreshCompleted();
            }else{
                ptrv_pull_refresh.setLoadCompleted();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web_view);
        ptrv_pull_refresh = findViewById(R.id.ptrv_pull_refresh);
        lv_refreshview = ptrv_pull_refresh.getRefreshView();
        lv_refreshview.loadUrl("http://www.qq.com");

        ptrv_pull_refresh.setOnRefreshLoadListener(new PullToRefreshBase.RefreshLoadListener(){
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0,2000);
            }
            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(1,2000);
            }
        });
    }
}
