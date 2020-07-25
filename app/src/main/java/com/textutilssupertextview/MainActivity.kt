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
        testZero.text = "番点击可烦你"
        testZero.setSpanClickStr("点击")
        testZero.setSpanColorStr(Color.RED,"烦",true)

        testOne.text = "你好你好你好"
        testOne.setSpanScaleValue(6)
            .setAddText("点击此处")
            //.setStringType(StringType.ADD_TEXT)
            .setIsRefreshNow(false)
            .setSpanColorStr(Color.RED,"你好",true, arrayOf(1))
            .setSpanColorStr(Color.GREEN,"你好",true, arrayOf(2))
            .setSpanColorStr(Color.RED,"吗")
            .setSpanBackgroundColor(0,1)
            .setSpanLineStr()
            .setAddText("haha")
            .setSpanScaleValueStr(8)
            .setSpanBold(0,100)
            .refreshNow()

        testTwo.text = "房门口等你发放 都房贷款纠纷方面的可能发飞动漫咖啡呢房门口的烦恼饭店客房内方面的看法拿分开买电脑积分电脑卡烦恼"
        testTwo.setSpanClickStr("房门")
            .setSpanColorStr(Color.RED)
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
            .setSpanItalicStr("点击")
            .setSpanBoldStr()
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
