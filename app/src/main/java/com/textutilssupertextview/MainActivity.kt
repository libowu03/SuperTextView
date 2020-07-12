package com.textutilssupertextview

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.textutils.textview.SuperTextAddTextClickListener
import com.textutils.textview.SuperTextClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testOne.text = "反对浪费吗方面的浪费饭店对面"
        testOne.setSpanScaleValue(6)
            .setIsRefreshNow(false)
            .setSpanColorStr(Color.RED,"吗")
            .setSpanBackgroundColor(0,1)
            .setSpanLineStr()
            .setSpanScaleValueStr(8)
            .refreshNow()

        testTwo.setSpanClickStr("Hello")
            .setOnStyleFontClickListener(object:SuperTextClickListener{
                override fun onClick(startPosition: Int, endPosition: Int, text: String) {
                    Toast.makeText(baseContext,text,Toast.LENGTH_SHORT).show()
                }
            })
            .setSpanColorStr(Color.RED)
            .setSpanBoldStr()
            .setAddTextClickListener(object:SuperTextAddTextClickListener{
                override fun onAddTextClick(text: String?) {
                    Toast.makeText(baseContext,text,Toast.LENGTH_SHORT).show()
                }

            })
        testTwo.setOnClickListener {
            Toast.makeText(baseContext,"你好",Toast.LENGTH_SHORT).show()
        }



        testOne.text = "反倒可能发放到"
        testOne.setSpanSuperscriptStr("放")
            .setSpanColorStr()
            .setSpanScalePercentStr(0.5f)
        testOne.setFontFace("font.TTF")

        testOne.setSpanColor(0,1,Color.RED)
        testTwo.setSpanColorStr(Color.RED,"Hello")
        testThree.setSpanItalic(0,1)
        testFour.setSpanItalicStr("Hello")
        testFive.setSpanBold(0,1)
        testSix.setSpanBoldStr("Hello")
        testOneSeven.setSpanBackgroundColor(0,1,Color.RED)
        testOneEight.setSpanImage(0,1,R.drawable.ic_launcher,true)
        testNice.setSpanBackgroundColorStr(Color.RED,"Hello")
        testTen.setSpanClick(0,1)
        testTen.setSpanClickStr("World",false)
    }
}
