package com.dxp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dxp.activity.MainActivityGridView;
import com.dxp.activity.MainActivityListView;
import com.dxp.activity.MainActivityRecycleView;
import com.dxp.activity.MainActivityScrollView;
import com.dxp.activity.MainActivitySwipeView;
import com.dxp.activity.MainActivityWebView;
import com.dxp.pulltorefresh.PullToRefreshSwipeListView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.swipe.SwipeDirection;
import com.dxp.swipe.SwipeMenu;
import com.dxp.swipe.SwipeMenuCreator;
import com.dxp.swipe.SwipeMenuItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private PullToRefreshSwipeListView lv_listview;
    private List<String> data = new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data.add("ListView");
        data.add("GridView");
        data.add("RecycleView");
        data.add("ScrollView");
        data.add("WebView");
        data.add("SwipeListView");

        lv_listview = findViewById(R.id.lv_listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,data);
        lv_listview.getRefreshView().setAdapter(adapter);
        lv_listview.getRefreshView().setSwipeDirection(SwipeDirection.BOTH);
        lv_listview.getRefreshView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this,MainActivityListView.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this,MainActivityGridView.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this,MainActivityRecycleView.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this,MainActivityScrollView.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this,MainActivityWebView.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity.this,MainActivitySwipeView.class));
                        break;
                }
            }
        });

        lv_listview.setOnRefreshLoadListener(new PullToRefreshBase.RefreshLoadListener(){
            @Override
            public void onRefresh() {
                handler.sendEmptyMessageDelayed(0,2000);
            }

            @Override
            public void onLoadMore() {
                handler.sendEmptyMessageDelayed(1,2000);
            }
        });

        lv_listview.getRefreshView().setSwipeMenuCreator(new SwipeMenuCreator() {
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
                Toast.makeText(MainActivity.this,"收藏"+index,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this,"删除"+index,Toast.LENGTH_SHORT).show();
            }
        });
        return swipeMenu;
    }

    private SwipeMenuItem getSwipeMenuItem(String title,int color) {
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                lv_listview.setRefreshCompleted();
            }else{
                lv_listview.setLoadCompleted();
            }
        }
    };

}
