package com.dxp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ImbeddedListView extends ListView {

    public ImbeddedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImbeddedListView(Context context) {
        super(context);
    }

    public ImbeddedListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置listview不能滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}