# SuperTextView使用教程

**SuperTextView是一款基于原生TextView升级而来的控件,开发的初衷是让文本样式设置变得更简单.该控件的主要功能有:文本追加(在文字末端显示追加文本),竖排文字,文本样式设置(基于原生的SpannableStringBuilder显示,支持开始结束位置设置文本样式,文字全匹配样式,文字筛选匹配样式,文字匹配到的第一处设置样式).使用该控件时最好用的是kotlin,否则在java下使用很多kt下默认参数就需要你手动填写上去了.**

## 使用方法
**下面是通过使用位置来设置样式的方法**
+ setSpanLine( Int : 开始位置, Int  : 结束位置) : 设置删除线
+ setSpanColor( Int  :开始位置, Int : 结束位置 ) : 设置字体颜色
+ setSpanUnderline( Int : 开始位置 , Int : 结束位置 ) : 设置下划线
+ setSpanBold( Int : 开始位置 , Int : 结束位置 ) : 设置字体加粗
+ setSpanItalic( Int : 开始位置 , Int : 结束位置) : 设置斜体
+ setSpanScalePercent( Float : 缩放百分比, Int : 开始位置 , Int : 结束位置 ) : 设置文字按百分比放大缩小
+ setSpanScaleValue( Int : 字体大小 , Int : 开始位置 , Int : 结束位置 ) : 设置文字大小
+ setSpanBackgroundColor( Int : 文字颜色 , Int : 开始位置 , Int : 结束位置 ) : 设置文字背景色
+ setSpanSubscript( Int  : 开始位置, Int : 结束位置 ) : 设置上角标
+ setSpanSuperscript( Int  : 开始位置, Int : 结束位置 ) : 设置下角标
+ setSpanClickStr( Int : 开始位置, Int : 结束位置 , Boolean : 是否显示下划线 ) : 设置点击
+ setSpanImage( Int : 开始位置, Int : 结束位置 , Int : 图片 , Booealn  : 是否居中于文字) : 设置图片

**下面是使用文字首字匹配,文字部分匹配和文字全匹配(只要方法名称为<font color = "#ff0000">setSpanXXXStr</font>形式的方法,都属于文字匹配,不进行每个方法参数详解,基本上参数和上面位置匹配一样,不过多加了过滤数组,是否全匹配和匹配文案三个参数而已)**
+ setSpanLineStr( String : 匹配文字, Boolean : 是否属于全匹配,Array<Int> : 需要过滤的数组,如果需要部分匹配,需要打开前面的全匹配开关,在此数组下的索引将被匹配,不在索引内的将被忽略不设置) 
+ setSpanColorStr( Int : 颜色颜色, String : 匹配文字 , Boolean : 是否开启全匹配 , Array<Int> : 功能和上面介绍的一样)


**xml属性介绍(所有该组件的xml属性开通都为st)**
+ stViewType : 设置文字样式类型，有：line（删除线）、underline （下划线）、bold（加粗）、italic（斜体）、scaleParent（文字相对大小）、scaleValue（文字绝对大小）、backgroundColor（文字背景颜色）、color（文字颜色）、subscript（上角标）、superscript（文字下角标）
+ stTextColor：特殊样式文字颜色，stViewType选color生效
+ stStartPosition：特殊样式开始位置
+ stEndPosition：特殊样式结束位置
+ stMatchStr：需要匹配的文字
+ stMatchEverySameStr：是否全匹配
+ stTextSize：特殊样式文字大小，stViewType选scaleValue生效
+ stScale：特殊样式文字缩放比例，stViewType选scaleParent生效
+ stBackgroundColor：特殊样式文字背景，stViewType选backgroundColor生效
+ stWordSpacingMultiplier：字符水平距离，仅对竖排文字生效
+ stEnablePortrait：是否支持竖排文字
+ stGravity：竖排文字显示开始位置，默认文案是从右边开始绘制，有：start（左边开始绘制）、end（右边开始绘制）、centerStart（居中并且文字从左边开始绘制）、centerEnd（居中且文字从右边开始绘制）
+ stAddToEndText：文本追加，即在文字行尾添加一段文字(比如 “你好呀，我是XXX&emsp;&emsp;&emsp;&emsp;&emsp;点击了解更多”，此处的“点击了解更多”就是追加的内容，原文本过长时，行尾部分内容会改为“......”的形式腾出空间来显示追加文本，如果需要显示完整文本，请不要使用该功能，除非确保有足够的空间显示要追加的文本)
+ stTopLeftCorner：左上角圆角
+ stTopRightCorner：右上角圆角
+ stBottomRightCorner：右下角圆角
+ stBottomLeftCorner：左下角圆角
+ stCorner：四个角的圆角
+ stSolidColor：填充背景颜色
+ stStrokeColor：描边颜色
+ stStrokeWidth：描边的粗细
+ stFontFace：文本字体，允许放到assets下或其他文件夹下，不过设置后没发在xml中直观的看出设置后的效果


**其他方法介绍**
+ clearStyle()：清除所有的样式
+ setFontFace（String：字体存放地址）：设置字体
+ setIsRefreshNow（Boolea：是否设置完马上属性）：使用链式调用时建议把这个设置成false，避免每次没有必要的控件刷新，在调用到最后时手动调用一次refreshNow（）就可以了。
+ setAddText（CharSequence：追加内容）：文字末尾显示的追加文本
+ setAddTextClickListener（SuperTextAddTextClickListener：追加文本点击监听）：设置追加文本的点击监听
+ setMathStr（String：匹配文案）：匹配文字

**xml使用示例**
```

    <com.textutils.textview.view.SuperTextView
        android:id="@+id/testTwo"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:stSolidColor="#FF69B4"
        app:stViewType="scaleValue"
        android:gravity="center_vertical"
        android:layout_marginRight="10dp"
        android:paddingStart="10dp"
        android:paddingEnd="10dp"
        android:text="白日依山尽黄河入海流欲穷千里目更上一层楼"
        app:stMatchStr="黄河入海流"
        android:textColor="#ffffff"
        app:stCorner="20dp"
        app:stTextSize="22dp"
        app:stStrokeColor="	#FF1493"
        app:stStrokeWidth="1dp"
        app:stAddToEndText="了解更多" />
```

**代码使用示例**
```
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
```

## 更新历史
+ 1.0.0
基础功能完成
