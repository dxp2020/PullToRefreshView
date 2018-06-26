package com.dxp.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dxp.R;
import com.dxp.pulltorefresh.PullToRefreshWebView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;
import com.dxp.swipe.SwipeDirection;
import com.dxp.swipe.SwipeMenu;
import com.dxp.swipe.SwipeMenuItem;
import com.dxp.swipe.SwipeView;

public class MainActivitySwipeView extends AppCompatActivity {
    private LinearLayout ll_container;
    private SwipeView swipeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_swipe_view);
        ll_container = findViewById(R.id.ll_container);
        swipeView = new SwipeView(this, View.inflate(this,R.layout.layout_swipe_view_item,null));
        ll_container.addView(swipeView);

        swipeView.addRightMenu(getSwipeMenu());
        swipeView.addLeftMenu(getSwipeMenu());
        swipeView.setSwipeDirection(SwipeDirection.LEFT);

        swipeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swipeView.getTag()==null){
                    swipeView.setTag("");
                    swipeView.smoothOpenMenu();
                }else{
                    swipeView.setTag(null);
                    swipeView.smoothCloseMenu();
                }
            }
        });
    }

    private SwipeMenu getSwipeMenu() {
        SwipeMenu swipeMenu = new SwipeMenu(this);
        swipeMenu.addMenuItem(getSwipeMenuItem());
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
