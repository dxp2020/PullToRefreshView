package com.dxp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dxp.pulltorefresh.PullToRefreshListView;
import com.dxp.pulltorefresh.PullToRefreshRecycleView;
import com.dxp.pulltorefresh.base.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

public class MainActivityRecycleView extends AppCompatActivity {
    private RecyclerView lv_listview;
    private PullToRefreshRecycleView ptrv_pull_refresh;
    private List<String> data = new ArrayList<>();
    private DemoAdapter adapter;

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
        setContentView(R.layout.activity_main_recycle_view);
        initData();
        ptrv_pull_refresh = findViewById(R.id.ptrv_pull_refresh);
        lv_listview = ptrv_pull_refresh.getRefreshView();
        lv_listview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DemoAdapter(this);
        lv_listview.setAdapter(adapter);

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

    public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.VH> {

        private Context context;

        public DemoAdapter(Context context) {
            this.context = context;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(View.inflate(context, android.R.layout.simple_list_item_1, null));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.mTextView.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public  class VH extends RecyclerView.ViewHolder {
            TextView mTextView;

            public VH(View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
