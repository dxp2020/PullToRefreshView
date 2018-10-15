package com.dxp.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.changf.pulltorefresh.PullToRefreshSwipeListView;
import com.changf.pulltorefresh.base.PullToRefreshBase;
import com.changf.pulltorefresh.swipe.SwipeDirection;
import com.changf.pulltorefresh.swipe.SwipeListView;
import com.changf.pulltorefresh.swipe.SwipeMenu;
import com.changf.pulltorefresh.swipe.SwipeMenuCreator;
import com.changf.pulltorefresh.swipe.SwipeMenuItem;
import com.dxp.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivitySwipeView extends Activity {
    private PullToRefreshSwipeListView lv_swipe_list;
    private SwipeListView lv_listview;
    private ArrayAdapter adapter;
    private List<String> data = new ArrayList<>();

    private void initData(){
        for(int i=0;i<10;i++){
            data.add("张"+(i+1));
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                data.clear();
                initData();
                adapter.notifyDataSetChanged();
                lv_swipe_list.setRefreshCompleted();
            }else{
                data.add("(╰_╯)#");
                adapter.notifyDataSetChanged();
                lv_swipe_list.setLoadCompleted();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_swipe_view);
        lv_swipe_list = findViewById(R.id.lv_swipe_list);
        initData();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);
        lv_listview = lv_swipe_list.getRefreshView();
        lv_listview.setAdapter(adapter);
        lv_listview.setSwipeDirection(SwipeDirection.LEFT);
        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivitySwipeView.this,"click-->"+position,Toast.LENGTH_SHORT).show();
            }
        });
        lv_swipe_list.setOnRefreshLoadListener(new PullToRefreshBase.RefreshLoadListener(){
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0,2000);
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(1,2000);
            }
        });

        lv_swipe_list.getRefreshView().setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public SwipeMenu createLeftMenu() {
                return getLeftSwipeMenu();
            }

            @Override
            public SwipeMenu createRightMenu() {
                return getRightSwipeMenu();
            }
        });
    }

    private SwipeMenu getLeftSwipeMenu() {
        SwipeMenu swipeMenu = new SwipeMenu(this);
        swipeMenu.addMenuItem(getSwipeMenuItem("收藏",Color.parseColor("#09BFEA")));
        swipeMenu.setOnSwipeItemClickListener(new SwipeMenu.OnSwipeItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Toast.makeText(MainActivitySwipeView.this,"收藏"+index,Toast.LENGTH_SHORT).show();
            }
        });
        return swipeMenu;
    }

    private SwipeMenu getRightSwipeMenu() {
        SwipeMenu swipeMenu = new SwipeMenu(this);
        swipeMenu.addMenuItem(getSwipeMenuItem("删除",Color.parseColor("#e83f22")));
        swipeMenu.setOnSwipeItemClickListener(new SwipeMenu.OnSwipeItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Toast.makeText(MainActivitySwipeView.this,"删除"+index,Toast.LENGTH_SHORT).show();
            }
        });
        return swipeMenu;
    }

    private SwipeMenuItem getSwipeMenuItem(String title, int color) {
        SwipeMenuItem deleteItem = new SwipeMenuItem(this);
        deleteItem.setBackground(new ColorDrawable(color));
        deleteItem.setWidth(dip2px(90));
        deleteItem.setTitle(title);
        deleteItem.setTitleSize(16);
        deleteItem.setTitleColor(Color.WHITE);
        return deleteItem;
    }

    public  int dip2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }


}
