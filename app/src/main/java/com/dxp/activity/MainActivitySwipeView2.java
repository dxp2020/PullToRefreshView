package com.dxp.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.changf.pulltorefresh.swipe.SwipeMenu;
import com.changf.pulltorefresh.swipe.SwipeMenuCreator;
import com.changf.pulltorefresh.swipe.SwipeMenuItem;
import com.changf.pulltorefresh.swipe.SwipeViews;
import com.dxp.R;

public class MainActivitySwipeView2 extends Activity {
    private SwipeViews mSwipeItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_swipe_view2);
        mSwipeItemView = findViewById(R.id.siv_item);

        mSwipeItemView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public SwipeMenu createLeftMenu() {
                return getLeftSwipeMenu();
            }
            @Override
            public SwipeMenu createRightMenu() {
                return getRightSwipeMenu();
            }
        });
        mSwipeItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeItemView.closeMenu();
            }
        });
    }

    private SwipeMenu getLeftSwipeMenu() {
        SwipeMenu swipeMenu = new SwipeMenu(this);
        swipeMenu.addMenuItem(getSwipeMenuItem("收藏",Color.parseColor("#09BFEA")));
        swipeMenu.setOnSwipeItemClickListener(new SwipeMenu.OnSwipeItemClickListener() {
            @Override
            public void onItemClick(int index) {
                Toast.makeText(MainActivitySwipeView2.this,"收藏"+index,Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivitySwipeView2.this,"删除"+index,Toast.LENGTH_SHORT).show();
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
