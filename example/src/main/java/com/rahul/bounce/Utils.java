package com.rahul.bounce;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;

/**
 * Created by workhard on 9/25/15.
 */
public class Utils {

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

    public static void setElevation(View view, float elevation) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            view.setElevation(elevation);
    }
}
