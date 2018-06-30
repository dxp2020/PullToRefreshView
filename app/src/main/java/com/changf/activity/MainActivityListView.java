package com.changf.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.changf.R;
import com.changf.pulltorefresh.PullToRefreshListView;
import com.changf.pulltorefresh.base.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

public class MainActivityListView extends Activity {
    private ListView lv_listview;
    private PullToRefreshListView ptrv_pull_refresh;
    private List<String> data = new ArrayList<>();
    private ArrayAdapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                data.clear();
                initData();
                adapter.notifyDataSetChanged();
                ptrv_pull_refresh.setRefreshCompleted();
            }else{
                data.add("(╰_╯)#");
                adapter.notifyDataSetChanged();
                ptrv_pull_refresh.setLoadCompleted();
            }
        }
    };

    private void initData(){
        for(int i=0;i<100;i++){
            data.add("张"+(i+1));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_view);
        initData();
        ptrv_pull_refresh = findViewById(R.id.ptrv_pull_refresh);
        lv_listview = ptrv_pull_refresh.getRefreshView();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);
        lv_listview.setAdapter(adapter);
        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivityListView.this,"click-->"+position,Toast.LENGTH_SHORT).show();
            }
        });
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
