package com.textutilssupertextview

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.textutils.textview.SuperTextAddTextClickListener
import com.textutils.textview.SuperTextClickListener
import com.textutils.textview.view.StringType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testZero.setSpanColorStr(Color.RED,"更上",true)

        testOne.text = "反倒可能发凡开放呢饭店开房你https://www.bilibili.com/bangumi/media/md427/?from=search"
        testOne.setSpanScaleValue(6)
            .setAddText("点击此处")
            //.setStringType(StringType.ADD_TEXT)
            .setIsRefreshNow(false)
            .setSpanColorStr(Color.RED,"你好",true, arrayOf(1))
            .setSpanColorStr(Color.GREEN,"你好",true, arrayOf(2))
            .setSpanColorStr(Color.RED,"吗")
            .setSpanBackgroundColor(0,  1)
            .setSpanLineStr()
            .setOnStyleFontClickListener(object:SuperTextClickListener{
                override fun onClick(startPosition: Int, endPosition: Int, text: String) {
                    Toast.makeText(this@MainActivity,text,Toast.LENGTH_SHORT).show()
                }

            })
            .setSpanUrlColor(Color.RED)
            .setSpanUrlScalePrecent(1.5f)
            .setSpanScaleValueStr(2,"你")
            .setAddText("haha")
            .setSpanUrlItalic()
            .setSpanBold(0,100)
            .refreshNow()

        testTwo.text = "你指尖跃动的电光，是我此生不灭的信仰，唯我超电磁炮永世长存"
        testTwo.setSpanClickStr("超电磁炮",false)
            .setSpanColorStr(Color.RED)
            .setSpanBackgroundColorStr(Color.BLUE)
            .setSpanBoldStr()
            .setOnStyleFontClickListener(object:SuperTextClickListener{
                override fun onClick(startPosition: Int, endPosition: Int, text: String) {
                    Toast.makeText(baseContext,text,Toast.LENGTH_SHORT).show()
                }

            })
            .setAddTextClickListener(object:SuperTextAddTextClickListener{
                override fun onAddTextClick(text: String?) {
                    Toast.makeText(baseContext,text,Toast.LENGTH_SHORT).show()
                }

            })
            .setStringType(StringType.ADD_TEXT)
            .setAddText("充电")
            .setSpanBoldStr("充电")
            .setSpanColorStr(Color.parseColor("#DEB887"))
            .refreshNow()



        testOne.setSpanSuperscriptStr("放")
            .setSpanColorStr()
            .setSpanScalePercentStr(0.5f)

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
