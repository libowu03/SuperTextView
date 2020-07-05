package com.textutils.textview.view

import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import com.textutils.textview.view.SuperTextMovementMethod as SuperTextMovementMethod1

object SuperTextMovementMethod : LinkMovementMethod() {


    override fun onTouchEvent(widget: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
        var b = super.onTouchEvent(widget,buffer,event);
        //解决点击事件冲突问题
        if(!b && event?.action == MotionEvent.ACTION_UP){
            val parent = widget?.parent
            if (parent is ViewGroup) {
                return parent.performClick();
            }
        }
        return b;
    }

}