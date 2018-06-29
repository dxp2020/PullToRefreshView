package com.dxp.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.dxp.R;
import com.dxp.pulltorefresh.PullToRefreshSwipeListView;
import com.dxp.pulltorefresh.PullToRefreshWebView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.swipe.SwipeDirection;
import com.dxp.swipe.SwipeListView;
import com.dxp.swipe.SwipeMenu;
import com.dxp.swipe.SwipeMenuCreator;
import com.dxp.swipe.SwipeMenuItem;
import com.dxp.swipe.SwipeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivitySwipeView extends AppCompatActivity {
    private PullToRefreshSwipeListView lv_swipe_list;
    private SwipeListView lv_listview;
    private SwipeView swipeView;
    private ArrayAdapter adapter;
    private List<String> data = new ArrayList<>();

    private void initData(){
        for(int i=0;i<100;i++){
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
        lv_listview.setSwipeDirection(SwipeDirection.BOTH);
        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                return getSwipeMenu();
            }

            @Override
            public SwipeMenu createRightMenu() {
                return getSwipeMenu();
            }
        });


//        ll_container = findViewById(R.id.ll_container);
//        swipeView = new SwipeView(this, View.inflate(this,R.layout.layout_swipe_view_item,null));
//        ll_container.addView(swipeView);
//
//        swipeView.addRightMenu(getSwipeMenu());
//        swipeView.addLeftMenu(getSwipeMenu());
//        swipeView.setSwipeDirection(SwipeDirection.BOTH);
//
//        swipeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(swipeView.getTag()==null){
//                    swipeView.setTag("");
//                    swipeView.smoothOpenMenu();
//                }else{
//                    swipeView.setTag(null);
//                    swipeView.smoothCloseMenu();
//                }
//            }
//        });
    }

    private SwipeMenu getSwipeMenu() {
        SwipeMenu swipeMenu = new SwipeMenu(this);
        swipeMenu.addMenuItem(getSwipeMenuItem());
        return swipeMenu;
    }

    private SwipeMenuItem getSwipeMenuItem() {
        SwipeMenuItem deleteItem = new SwipeMenuItem(this);
        deleteItem.setBackground(new ColorDrawable(Color.parseColor("#e83f22")));
        deleteItem.setWidth(dip2px(90));
        deleteItem.setTitle("删除");
        deleteItem.setTitleSize(16);
        deleteItem.setTitleColor(Color.WHITE);
        return deleteItem;
    }

    public  int dip2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }


}
