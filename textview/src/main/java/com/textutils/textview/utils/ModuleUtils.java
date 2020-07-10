package com.textutils.textview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Element;

import androidx.annotation.RequiresApi;

public class ModuleUtils {
    //将px转换成dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //dp转px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Bitmap bitmap, float radius, Context context) {
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        android.renderscript.RenderScript rs = android.renderscript.RenderScript.create(context);

        //Create an Intrinsic Blur Script using the Renderscript
        android.renderscript.ScriptIntrinsicBlur blurScript = android.renderscript.ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        android.renderscript.Allocation allIn = android.renderscript.Allocation.createFromBitmap(rs, bitmap);
        android.renderscript.Allocation allOut = android.renderscript.Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(radius);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
        //        bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
//        view.setBackground(new BitmapDrawable(context.getResources(), outBitmap));
        rs.destroy();

        return outBitmap;
    }

}
