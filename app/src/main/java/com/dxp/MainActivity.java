package com.dxp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dxp.activity.MainActivityGridView;
import com.dxp.activity.MainActivityListView;
import com.dxp.activity.MainActivityRecycleView;
import com.dxp.activity.MainActivityScrollView;
import com.dxp.activity.MainActivitySwipeView;
import com.dxp.activity.MainActivityWebView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private ListView lv_listview;
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
        lv_listview.setAdapter(adapter);
        lv_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    }

}
