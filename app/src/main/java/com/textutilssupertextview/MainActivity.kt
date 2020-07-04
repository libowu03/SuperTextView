package com.textutilssupertextview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.textutils.textview.SuperTextClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testOne.text = "反倒可能发放到"
        testOne.setSpanSuperscript("放")
            .setSpanColor("放",Color.RED)
            .setSpanScalePercent("放",0.5f)
        testOne.setFontFace("font.TTF")

        testOne.setSpanColor(0,1,Color.RED)
        testTwo.setSpanColor("Hello",Color.RED)
        testThree.setSpanItalic(0,1)
        testFour.setSpanItalic("Hello")
        testFive.setSpanBold(0,1)
        testSix.setSpanBold("Hello")
        testOneSeven.setSpanBackgroundColor(0,1,Color.RED)
        testOneEight.setSpanImage(0,1,R.drawable.ic_launcher,true)
        testNice.setSpanBackgroundColor("Hello",Color.RED)
        testTen.setSpanClick(0,1)
        testTen.setSpanClick("World",false)
    }
}
