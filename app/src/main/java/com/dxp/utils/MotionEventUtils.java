package com.dxp.utils;

import android.util.Log;
import android.view.MotionEvent;

import com.dxp.pulltorefresh.PullToRefreshView;

public class MotionEventUtils {
    public final static String TAG = "MotionEventUtils";

    private static boolean isShowAdditionalData(String... additional){
        if(additional!=null&&additional.length>0){
            return true;
        }
        return false;
    }

    private static String showAdditionalData(String... additional){
        StringBuffer sb = new StringBuffer();
        for(String content:additional){
            sb.append("--").append(content);
        }
        return sb.toString();
    }

    private static String showAdditional(String... additional){
        return (isShowAdditionalData(additional)?showAdditionalData(additional):"");
    }

    public static void println(int eventCode,String... additional){
        switch (eventCode){
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG,"MotionEvent.ACTION_DOWN"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_DOWN"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_2_DOWN"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_3_DOWN:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_3_DOWN"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG,"MotionEvent.ACTION_MOVE"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG,"MotionEvent.ACTION_UP"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_UP"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_2_UP:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_UP"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_3_UP:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_UP"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG,"MotionEvent.ACTION_CANCEL"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_OUTSIDE:
                Log.e(TAG,"MotionEvent.ACTION_OUTSIDE"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_HOVER_ENTER:
                Log.e(TAG,"MotionEvent.ACTION_HOVER_ENTER"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
                Log.e(TAG,"MotionEvent.ACTION_HOVER_EXIT"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                Log.e(TAG,"MotionEvent.ACTION_HOVER_MOVE"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_MASK:
                Log.e(TAG,"MotionEvent.ACTION_MASK"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_INDEX_MASK:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_INDEX_MASK"+showAdditional(additional));
                break;
            case MotionEvent.ACTION_POINTER_INDEX_SHIFT:
                Log.e(TAG,"MotionEvent.ACTION_POINTER_INDEX_SHIFT"+showAdditional(additional));
                break;
        }
    }

    public static void printlnFooterStatus(int status){
        switch (status){
            case PullToRefreshView.STATUS_LOAD_FINISHED:
                Log.e(TAG,"printlnFooterStatus-->STATUS_LOAD_FINISHED");
                break;
            case PullToRefreshView.STATUS_LOADING:
                Log.e(TAG,"printlnFooterStatus-->STATUS_LOADING");
                break;
            case PullToRefreshView.STATUS_RELEASE_LOAD:
                Log.e(TAG,"printlnFooterStatus-->STATUS_RELEASE_LOAD");
                break;
            case PullToRefreshView.STATUS_LOAD_NORMAL:
                Log.e(TAG,"printlnFooterStatus-->STATUS_LOAD_NORMAL");
                break;
        }
    }

    public static void printlnParams(boolean isTop, boolean isBottom, int touchSlop, float deltaY) {
        Log.e(TAG,"isTop->"+isTop+" isBottom->"+isBottom+" touchSlop->"+touchSlop+" deltaY-->"+deltaY);
    }

    public static void printParams(int... params) {
        StringBuffer sb = new StringBuffer();
        for(int value:params){
            sb.append("  ").append(value);
        }
        Log.e(TAG,sb.toString());
    }
}
