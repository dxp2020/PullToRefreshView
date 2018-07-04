package com.changf.pulltorefresh.utils;

import android.util.Log;
import android.view.MotionEvent;


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
        Log.e(TAG,getPrintStr(eventCode,additional));
    }

    public static String getPrintStr(int eventCode,String... additional){
            switch (eventCode){
                case MotionEvent.ACTION_DOWN:
                    return "MotionEvent.ACTION_DOWN"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_DOWN:
                    return "MotionEvent.ACTION_POINTER_DOWN"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_2_DOWN:
                    return "MotionEvent.ACTION_POINTER_2_DOWN"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_3_DOWN:
                    return "MotionEvent.ACTION_POINTER_3_DOWN"+showAdditional(additional);
                case MotionEvent.ACTION_MOVE:
                    return "MotionEvent.ACTION_MOVE"+showAdditional(additional);
                case MotionEvent.ACTION_UP:
                    return "MotionEvent.ACTION_UP"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_UP:
                    return "MotionEvent.ACTION_POINTER_UP"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_2_UP:
                    return "MotionEvent.ACTION_POINTER_UP"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_3_UP:
                    return "MotionEvent.ACTION_POINTER_UP"+showAdditional(additional);
                case MotionEvent.ACTION_CANCEL:
                    return "MotionEvent.ACTION_CANCEL"+showAdditional(additional);
                case MotionEvent.ACTION_OUTSIDE:
                    return "MotionEvent.ACTION_OUTSIDE"+showAdditional(additional);
                case MotionEvent.ACTION_HOVER_ENTER:
                    return "MotionEvent.ACTION_HOVER_ENTER"+showAdditional(additional);
                case MotionEvent.ACTION_HOVER_EXIT:
                    return "MotionEvent.ACTION_HOVER_EXIT"+showAdditional(additional);
                case MotionEvent.ACTION_HOVER_MOVE:
                    return "MotionEvent.ACTION_HOVER_MOVE"+showAdditional(additional);
                case MotionEvent.ACTION_MASK:
                    return "MotionEvent.ACTION_MASK"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_INDEX_MASK:
                    return "MotionEvent.ACTION_POINTER_INDEX_MASK"+showAdditional(additional);
                case MotionEvent.ACTION_POINTER_INDEX_SHIFT:
                    return "MotionEvent.ACTION_POINTER_INDEX_SHIFT"+showAdditional(additional);
        }
        return null;
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
