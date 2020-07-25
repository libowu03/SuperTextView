# SuperTextView使用教程

**SuperTextView是一款基于原生TextView升级而来的控件,开发的初衷是让文本样式设置变得更简单.该控件的主要功能有:文本追加(在文字末端显示追加文本),竖排文字,文本样式设置(基于原生的SpannableStringBuilder显示,支持开始结束位置设置文本样式,文字全匹配样式,文字筛选匹配样式,文字匹配到的第一处设置样式).使用该控件时最好用的是kotlin,否则在java下使用很多kt下默认参数就需要你手动填写上去了.**

## 使用方法
下面是通过使用位置来设置样式的方法
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

下面是使用文字首字匹配,文字部分匹配和文字全匹配(只要方法名称为setSpanXXXStr形式的方法,都属于文字匹配,不进行每个方法参数详解,基本上参数和上面位置匹配一样,不过多加了过滤数组,是否全匹配和匹配文案三个参数而已)
+ setSpanLineStr( String : 匹配文字, Boolean : 是否属于全匹配,Array<Int> : 需要过滤的数组,如果需要部分匹配,需要打开前面的全匹配开关,在此数组下的索引将被匹配,不在索引内的将被忽略不设置) 
+ setSpanColorStr( Int : 颜色颜色, String : 匹配文字 , Boolean : 是否开启全匹配 , Array<Int> : 功能和上面介绍的一样)

