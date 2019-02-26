package com.example.avjindersinghsekhon.minimaltodo;

import android.content.Context;
import android.content.res.TypedArray;


public class Utils {
        //Function getToolbarHeight
        //IN: Context object
        //OUT: toolbarHeight, integer that is the height of the toolbar to be drawn
        //sets the toolbar height
        public static int getToolbarHeight(Context context) {
            final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                    new int[]{R.attr.actionBarSize});
            int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();
            return toolbarHeight;
        }
}
