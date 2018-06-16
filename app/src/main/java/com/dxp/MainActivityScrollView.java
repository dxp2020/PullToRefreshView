package com.dxp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;

import com.dxp.pulltorefresh.PullToRefreshScrollView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.view.ImbeddedListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivityScrollView extends AppCompatActivity {
    private PullToRefreshScrollView prsv_scroll_view;
    private ImbeddedListView lv_listview;
    private List<String> data = new ArrayList<>();
    private ArrayAdapter adapter;

    private void initData(){
        for(int i=0;i<100;i++){
            data.add("张"+(i+1));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_scroll_view);
        prsv_scroll_view = findViewById(R.id.prsv_scroll_view);
        lv_listview = findViewById(R.id.lv_listview);
        initData();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);
        lv_listview.setAdapter(adapter);

        prsv_scroll_view.setOnRefreshLoadListener(new PullToRefreshBase.RefreshLoadListener(){
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        prsv_scroll_view.setRefreshCompleted();
                    }
                },2000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        prsv_scroll_view.setLoadCompleted();
                    }
                },2000);
            }
        });

        prsv_scroll_view.post(new Runnable() {
            @Override
            public void run() {
                prsv_scroll_view.getRefreshView().fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }
}
