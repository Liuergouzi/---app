package com.hjq.demo.overall;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

public class GetScreenSize {
    
    public Integer getScreenWidth(Activity activity){
        //手机宽
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public Integer getScreenHeight(Activity activity){
        //手机高
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }
    
}
