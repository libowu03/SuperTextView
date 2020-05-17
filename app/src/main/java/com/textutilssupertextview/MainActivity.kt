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
        testOne.text = "李博武你好https://www.baidu.com饭店开房接口放到付款李博武你好https://www.baidu.com饭店开房接口放到付款李博武你好https://www.baidu.com饭店开房接口放到付款李博武你好https://www.baidu.com饭店开房接口放到付款"
        
    }
}
