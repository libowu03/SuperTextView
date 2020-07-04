package com.textutils.textview.view

import android.content.Context
import android.graphics.*
import android.text.*
import android.text.TextUtils.TruncateAt
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import com.textutils.textview.R
import com.textutils.textview.SuperTextClickListener
import com.textutils.textview.utils.ModuleUtils
import com.textutils.textview.utils.TextUtils
import java.lang.Exception


/**
 * 基于安卓textview进行修改,目的是方便各种样式的设置
 * @author libowu
 */
class SuperTextView : androidx.appcompat.widget.AppCompatTextView {
    private var stringBuffer: SpannableStringBuilder? = null
    //临时保存文字大小
    private var tempTextSize = 0f
    //保存竖排文字时需要使用的特殊样式
    private var portraitStyleMap: HashMap<String, Int>? = null
    //点击监听
    private var clickCallback:SuperTextClickListener?=null
    //这个变量是测量文字大小时使用的标准文字，即无论汉字、字母、数字或符号，最终测量出来的字体大小是已这个字符为标准的。这样的目的是避免文字在排版时出现混乱的情况
    private var portaitStr = "你"
    //是否支持竖排文字显示
    private var superTextEnablePortrait = false
    //获取文本内容时不应该获取到的文本内容，比如在文本末端添加的占位符和末尾按钮等这些文本不应该被获取到的
    private var excludeStr = ""
    //竖排文字的列数
    private var rowSize = 0
    //特殊样式的字体颜色
    var superColor: Int = Color.BLACK
    //需要匹配的文案
    var matchStr:String?= ""
    //是否对textview的所有文案进行目标匹配,匹配目标为matchStr
    var matchEverySameStr:Boolean = false
    //需要设置特殊样式的起始位置
    var startPosition:Int = 0
    //需要匹配特殊样式的结束位置
    var endPosition:Int = 0
    //设置样式的样式类型
    var styleType:Int = 0
    //特殊样式文字大小
    var superTextSize:Int = 0
    //特殊样式文字缩放比例,按原文字比例
    var superTextScalePrecent:Float = 1f
    //特殊样式文本背景
    var superTextBackgroundColor:Int = Color.TRANSPARENT
    //是否设置点击时在点击文案下方添加下划线,默认打开划线
    var enableClickUnderLine:Boolean = true
    //是否打开竖排文字
    var enableVerticalType:Boolean = false
    //竖排文字的每个字符的间距倍数
    var wordSpacingMultiplier : Float = 1f
    //竖排文字绘制起始地点,有start和end两种,start表示从左边开始蕙芷,end则表示从右边开始绘制
    var superTextGravity:Int = 1
    //追加文本,即在行位添加需要追加的文案
    var addToEndText:String?=null
    //左上角圆角
    var superTopLeftCorner:Float = 0f
    //左下角圆角
    var superTopRightCorner:Float = 0f
    //左下角圆角
    var superBottomLeftCorner:Float = 0f
    //右上角圆角
    var superBottomRightCorner:Float = 0f
    //四个角的圆角
    var superCorner:Float = 0f
    //圆角颜色填充
    var superSolidColor = Color.TRANSPARENT
    //描边的宽度
    var superStrokeWidth = 0f
    //描边的颜色
    var superStrokeColor = Color.TRANSPARENT
    //字体路径，请保存到assets文件夹中
    var superTextFontFace:String ?= ""
    //打印日志的标识
    var LOG = "superText"

    //保存匹配字符的位置信息集合
    private var matchStrArray: ArrayList<String> = ArrayList()
    //绘制背景的paint
    private var backgroundSrcPaint:Paint = Paint()
    //绘制描边的颜色
    private var strokePaint:Paint = Paint()
    private var roundValue = Array<Float>(8,{0f})







    constructor(context: Context) : this(context, null)

    constructor(context: Context?, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context?, attr: AttributeSet?, def: Int) : super(context, attr, def) {
        //设置这个透明是为了在设置点击时避免出现背景色
        highlightColor = Color.TRANSPARENT
        stringBuffer = SpannableStringBuilder(text)
        tempTextSize = textSize
        portraitStyleMap = HashMap()
        getParameter(attr, def)
        initData()
        initCornr()
        //设置绘制背景的画板
        backgroundSrcPaint.color = superSolidColor
        backgroundSrcPaint.isAntiAlias = true

        strokePaint.color = superStrokeColor
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = superStrokeWidth
        strokePaint.isAntiAlias = true
    }

    /**
     * 设置圆角
     */
    private fun initCornr() {
        if (superCorner != 0f){
            roundValue.set(0,superCorner)
            roundValue.set(1,superCorner)
            roundValue.set(2,superCorner)
            roundValue.set(3,superCorner)
            roundValue.set(4,superCorner)
            roundValue.set(5,superCorner)
            roundValue.set(6,superCorner)
            roundValue.set(7,superCorner)
        }else{
            //左上角
            roundValue.set(0,superTopLeftCorner)
            roundValue.set(1,superTopLeftCorner)
            //右上角
            roundValue.set(2,superTopRightCorner)
            roundValue.set(3,superTopRightCorner)
            //右下角
            roundValue.set(4,superBottomRightCorner)
            roundValue.set(5,superBottomRightCorner)
            //左下角
            roundValue.set(6,superBottomLeftCorner)
            roundValue.set(7,superBottomLeftCorner)
        }
    }

    /**
     * h获取xml中的属性
     */
    private fun getParameter(attr: AttributeSet?, def: Int) {
        val parameterType = context.theme.obtainStyledAttributes(attr,
            R.styleable.SuperTextView, def, 0)
        superColor = parameterType.getColor(R.styleable.SuperTextView_superTextColor, -1)
        matchStr = parameterType.getString(R.styleable.SuperTextView_superTextMatchStr)
        matchEverySameStr = parameterType.getBoolean(R.styleable.SuperTextView_superTextMatchEverySameStr, false)
        startPosition = parameterType.getInteger(R.styleable.SuperTextView_superTextStartPosition, 0)
        endPosition = parameterType.getInteger(R.styleable.SuperTextView_superTextEndPosition, 0)
        styleType = parameterType.getInt(R.styleable.SuperTextView_superTextViewType, 7)
        superTextSize = parameterType.getDimensionPixelSize(R.styleable.SuperTextView_superTextSize, textSize.toInt())
        superTextEnablePortrait = parameterType.getBoolean(R.styleable.SuperTextView_superTextEnablePortrait, false)
        superTextSize = ModuleUtils.px2dip(
            context,
            superTextSize.toFloat()
        )
        superTextScalePrecent = parameterType.getFloat(R.styleable.SuperTextView_superTextScale, 1.0f)
        superTextBackgroundColor = parameterType.getColor(R.styleable.SuperTextView_superBackgroundColor, Color.parseColor("#74E1FF"))
        enableClickUnderLine = parameterType.getBoolean(R.styleable.SuperTextView_superTextEnableClickUnderline, true)
        superStrokeColor = parameterType.getColor(R.styleable.SuperTextView_superStrokeColor,Color.TRANSPARENT)
        superStrokeWidth = parameterType.getDimension(R.styleable.SuperTextView_superStrokeWidth,0f)

        //enableVerticalType = parameterType.getBoolean(R.styleable.SuperTextView_superTextEnableSetType, true)
        //wordSpacingMultiplier = parameterType.getFloat(R.styleable.SuperTextView_wordSpacingMultiplier, 1.0f)
        superTextGravity = parameterType.getInt(R.styleable.SuperTextView_superTextGravity, 1)
        addToEndText = parameterType.getString(R.styleable.SuperTextView_superTextAddToEndText)
        superTopLeftCorner = parameterType.getDimension(R.styleable.SuperTextView_superTopLeftCorner,0f)
        superTopRightCorner = parameterType.getDimension(R.styleable.SuperTextView_superTopRightCorner,0f)
        superBottomLeftCorner = parameterType.getDimension(R.styleable.SuperTextView_superBottomLeftCorner,0f)
        superBottomRightCorner = parameterType.getDimension(R.styleable.SuperTextView_superBottomRightCorner,0f)
        superCorner = parameterType.getDimension(R.styleable.SuperTextView_superCorner,0f)
        superSolidColor = parameterType.getColor(R.styleable.SuperTextView_superSolidColor,Color.TRANSPARENT)
        superTextFontFace = parameterType.getString(R.styleable.SuperTextView_superTextFontFace)
        parameterType.recycle()
    }

    /**
     * 将所有的样式设置为默认值
     */
    fun clearStyle(){
        superColor = textColors.defaultColor
        matchStr = ""
        matchEverySameStr = false
        startPosition = 0
        endPosition = 0
        styleType = SuperTextConfig.Style.COLOR
        superTextSize = ModuleUtils.px2dip(context, superTextSize.toFloat())
        superTextEnablePortrait = false
        superTextScalePrecent = 1f
        superTextBackgroundColor =  Color.parseColor("#74E1FF")
        enableClickUnderLine = true
        enableVerticalType = true
        wordSpacingMultiplier = 1f
        superTextGravity = 1
        addToEndText = ""
        superTopLeftCorner = 0f
        superTopRightCorner = 0f
        superBottomLeftCorner = 0f
        superBottomRightCorner = 0f
        superCorner = 0f
        superStrokeWidth = 0f
        superStrokeColor = Color.TRANSPARENT
        superSolidColor = Color.TRANSPARENT
        stringBuffer?.clear()
        stringBuffer = SpannableStringBuilder(text.toString())
        superTextFontFace = ""
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        if (text.toString().isNullOrEmpty()){
            return
        }

        setFontFace()

        //如果存在匹配的文案,先进行清除
        matchStrArray.clear()
        //大于文本长度时，设置为文本长度
        if (endPosition > text.length || endPosition == -1) {
            endPosition = text.length
        }
        //小于0时，设置开始位置为文本其实位置
        if (startPosition == -2) {
            startPosition = 0
        }else if (0-startPosition > 0){
            startPosition = 0
        }
/*        if (startPosition < endPosition){
            startPosition = 0
            endPosition = text.length
        }*/
        stringBuffer = SpannableStringBuilder(text)
        matchStrArray.addAll(TextUtils.getMatchStrArray(matchStr,matchEverySameStr,text.toString(),text.toString()))
        setStyle()
    }

    /**
     * 设置文字字体
     */
    fun setFontFace(fonTFace:String?=superTextFontFace){
        this.superTextFontFace = fonTFace
        //设置字体
        if (!superTextFontFace.isNullOrEmpty()){
            try{
                if (superTextFontFace!!.startsWith("/")){
                    superTextFontFace = superTextFontFace!!.replaceFirst("/","")
                }
                val end = if (superTextFontFace!!.lastIndexOf("/") == -1) 0 else superTextFontFace!!.lastIndexOf("/")
                val start = if (superTextFontFace!!.lastIndexOf("/")+1 == 0) 0 else superTextFontFace!!.lastIndexOf("/")+1
                val list = context.assets.list(superTextFontFace!!.substring(0,end))
                val isExist = list?.contains(superTextFontFace!!.substring(start,superTextFontFace!!.length))
                var font:Typeface
                if (isExist!!){
                    font = Typeface.createFromAsset(context.assets,superTextFontFace)
                }else{
                    font = Typeface.createFromFile(superTextFontFace)
                }
                if (font != null){
                    typeface = font
                }
            }catch (e:Exception){
                Log.e(LOG,String.format(context.resources.getString(R.string.readFontFaceFail),e.localizedMessage))
            }
        }
        invalidate()
    }

    /**
     * 通过xml属性设置不同的样式
     */
    private fun setStyle() {
        when (styleType) {
            SuperTextConfig.Style.LINE -> {
                setSpanLine()
            }
            SuperTextConfig.Style.UNDER_LINE -> {
                setSpanUnderline()
            }
            SuperTextConfig.Style.BOLD -> {
                setSpanBold()
            }
            SuperTextConfig.Style.ITALIC -> {
                setSpanItalic()
            }
            SuperTextConfig.Style.SCALE_PERCENT -> {
                setSpanScalePercent()
            }
            SuperTextConfig.Style.SCALE_VALUE -> {
                setSpanScaleValue()
            }
            SuperTextConfig.Style.BACKGROUND_COLOR -> {
                setSpanBackgroundColor()
            }
            SuperTextConfig.Style.COLOR -> {
                setSpanColor()
            }
            SuperTextConfig.Style.SUBSCRIPT -> {
                setSpanSubscript()
            }
            SuperTextConfig.Style.SUPERSCRIPT -> {
                setSpanSuperscript()
            }
        }
    }

    /**
     * 这个放啊是为了使调用者可以直接使用text的setText()方法
     * @return stringBuffer是否被重置了
     */
    private fun compareText():Boolean{
        if (stringBuffer != null && !text.toString().equals(stringBuffer.toString())){
            stringBuffer = SpannableStringBuilder(text)
            matchStrArray.clear()
            return true
        }
        return false
    }

    /**
     * 获取开始和结束位置,使用这个方法目的是为了避免外部调用者传入开始和结束位置有问题导致软件崩溃的问题
     */
    private fun getStartAndEndPosition(startPosition: Int,endPosition: Int):ArrayList<Int>{
        var tempStartPosition = startPosition
        var tempEndPosition = endPosition
        if (tempEndPosition < 0) {
            tempEndPosition = 0
        }
        if (tempStartPosition < 0){
            tempStartPosition = 0
        }
        if (tempEndPosition > text.length) {
            tempEndPosition = text.length
        }
        if (tempStartPosition > tempEndPosition) {
            tempEndPosition = 0
            tempStartPosition = 0
        }
        val startAndEndPosttion = ArrayList<Int>()
        startAndEndPosttion.add(tempStartPosition)
        startAndEndPosttion.add(tempEndPosition)
        return startAndEndPosttion
    }


    /**
     * 设置删除线
     */
    fun setSpanLine(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition) :SuperTextView{
      setPositionStyle(startPosition,endPosition, SuperTextConfig.Style.LINE,false)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanLine(matchStr:String,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.LINE,0,0,0,0f,0)
        return this
    }

    /**
     * 设置删下划线
     */
    fun setSpanUnderline(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition):SuperTextView {
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.UNDER_LINE)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanUnderline(matchStr:String,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.UNDER_LINE,0,0,0,0f,0)
        return this
    }



    /**
     * 设置粗体
     */
    fun setSpanBold(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.BOLD)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanBold(matchStr:String,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.BOLD,0,0,0,0f,0)
        return this
    }

    /**
     * 设置斜体
     */
    fun setSpanItalic(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.ITALIC)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanItalic(matchStr:String,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.ITALIC,0,0,0,0f,0)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanSubscript(matchStr:String,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.SUBSCRIPT,0,0,0,0f,0)
        return this
    }

    /**
     * 设置斜体
     */
    fun setSpanSubscript(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.SUBSCRIPT)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanSuperscript(matchStr:String,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.SUPERSCRIPT,0,0,0,0f,0)
        return this
    }

    /**
     * 设置斜体
     */
    fun setSpanSuperscript(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.SUPERSCRIPT)
        return this
    }


    /**
     * 设置缩放比例
     */
    fun setSpanScalePercent(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition,scalePercent:Float = superTextScalePrecent) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.SCALE_PERCENT,false,0,0,0,scalePercent,0)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanScalePercent(matchStr:String,scalePercent: Float = this.superTextScalePrecent,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.SCALE_PERCENT,0,0,0,scalePercent,0)
        return this
    }

    /**
     * 设置缩放大小
     */
    fun setSpanScaleValue(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition,superTextSize:Int = this.superTextSize) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.SCALE_VALUE,false,0,0,0,0f,superTextSize)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanScaleValue(matchStr:String,scaleValue: Int = this.superTextSize,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.SCALE_VALUE,0,0,0,0f,scaleValue)
        return this
    }


    /**
     * 设置特殊样式背景色
     */
    fun setSpanBackgroundColor(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition,backgroundColor:Int = this.superTextBackgroundColor) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.BACKGROUND_COLOR,false,0,backgroundColor,0,0f,0)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanBackgroundColor(matchStr:String,backgroundColor: Int = this.superTextBackgroundColor,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.BACKGROUND_COLOR,0,backgroundColor,0,0f,0)
        return this
    }

    /**
     * 设置特殊样式字体颜色
     */
    fun setSpanColor(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition,superTextColor :Int = this.superColor) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.COLOR,false,superTextColor,0,0,0f,0)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanColor(matchStr:String,textColor: Int = this.superColor,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.COLOR,textColor,0,0,0f,0)
        return this
    }

    /**
     * 设置特殊样式字体点击
     */
    fun setSpanClick(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition,enableUnderLine :Boolean = this.enableClickUnderLine) :SuperTextView{
        setPositionStyle(startPosition,endPosition,
            SuperTextConfig.Style.CLICK,false,0,0,0,0f,0,enableUnderLine)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanClick(matchStr:String,enableUnderLine: Boolean = this.enableClickUnderLine,matchAll:Boolean = false,indexArray:Array<Int> ?= null):SuperTextView{
        setMatchStrStyle(matchStr,matchAll,indexArray,
            SuperTextConfig.Style.CLICK,0,0,0,0f,0,enableUnderLine)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlClick(enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.CLICK,0,0,0f,0,enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlColor(superTextColor: Int=this.superColor,enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.COLOR,superTextColor,0,0f,0,enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlBold(enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.BOLD,0,0,0f,0,enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlBackgroundColor(backgroundColor: Int = this.superTextBackgroundColor,enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.BOLD,0,backgroundColor,0f,0,enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlItalic(enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.ITALIC,0,0,0f,0,enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlLine(enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.LINE,0,0,0f,0,enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlScalePrecent(scalePrecent:Float = this.superTextScalePrecent,enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.SCALE_PERCENT,0,0,scalePrecent,0,enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlScaleValue(scaleValue:Int = this.superTextSize,enableUnderlink: Boolean=true,matchArray:Array<Int>?=null):SuperTextView{
        setUrlStrStyle(matchArray,SuperTextConfig.Style.SCALE_PERCENT,0,0,0f,scaleValue,enableUnderlink)
        return this
    }

    /**
     * 设置图片
     * @param startPosition 特殊样样式起始位置
     * @param endPosition 特殊样式结束位置
     * @param img 图片
     * @param isRefreshNow 是否设置后立即属性
     */
    fun setSpanImage(startPosition: Int = this.startPosition, endPosition: Int = this.endPosition, img: Int,isCenter:Boolean = false) :SuperTextView{
        setPositionStyle(startPosition,endPosition,SuperTextConfig.Style.IMG,false,0,0,img,0f,0,false,isCenter)
        return this
    }

    fun setSpanImage(matchStr: String,img: Int,matchAll: Boolean=false,isCenter:Boolean = true,indexArray: Array<Int>?=null) {
        setMatchStrStyle(matchStr,matchAll,indexArray,SuperTextConfig.Style.IMG,0,0,img,0f,0,false,isCenter)
    }

    /**
     * 获取开始和结束位置,同时使用compareText()方法做一些赋值操作,位真正开始设置样式做准备
     * @param startPosition 特殊样式开始位置
     * @param endPosition 特殊样式结束位置
     * @param includeClick 是否启用点击
     * @param superTextColor 特殊样式字体样色
     * @param img 需要插入的图片
     * @param scalePercent 字体缩放比例
     * @param scaleValue 字体缩放的具体大小
     */
    private fun setPositionStyle(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition,type:Int,includeClick:Boolean = false,superTextColor:Int = 0,backgroundColor:Int=this.superTextBackgroundColor,img:Int=0,scalePercent: Float = this.superTextScalePrecent,scaleValue:Int=this.superTextSize,enableUnderLine: Boolean = this.enableClickUnderLine,isCenter: Boolean = false){
        this.startPosition = startPosition
        this.endPosition = endPosition
        compareText()
        //获取开始和结束位置
        if (!matchStr.isNullOrEmpty()){
            for (item in matchStrArray){
                val startIndex = item.split(",")[0]
                val endIndex = item.split(",")[1]
                setSuperStyle(startIndex.toInt(),endIndex.toInt(),type,includeClick,superTextColor,backgroundColor,img,scalePercent,scaleValue,false,enableUnderLine,isCenter)
            }
            text = stringBuffer
        }else{
            val tempStartPosition = getStartAndEndPosition(startPosition,endPosition).get(0)
            val tempEndPosition = getStartAndEndPosition(startPosition,endPosition).get(1)
            setSuperStyle(tempStartPosition,tempEndPosition,type,includeClick,superTextColor,backgroundColor,img,scalePercent,scaleValue,true,enableUnderLine,isCenter)
        }
    }

    /**
     * 通過字符串匹配獲取开始位置和解锁位置
     */
    private fun setMatchStrStyle(matchStr:String,matchAll:Boolean,indexArray:Array<Int>?,type:Int,superTextColor:Int = 0,backgroundColor:Int=0,img:Int=0,scalePercent: Float = this.superTextScalePrecent,scaleValue:Int=this.superTextSize,enableUnderLine: Boolean = this.enableClickUnderLine,isCenter: Boolean = false){
        val isReset = compareText()
        matchEverySameStr = matchAll
        if (matchAll){
            //如果匹配所有字符，则清除之前的数据，通过递归重新获取
            if (!matchStr.equals(this.matchStr) || isReset){
                this.matchStr = matchStr
                this.matchStrArray.clear()
                matchStrArray.addAll(TextUtils.getMatchStrArray(matchStr,matchAll,text.toString(),text.toString()))
            }
            var tempIndex = 0
            for (item in matchStrArray!!.withIndex()) {
                //通过调用者传入的数组判断哪些目标是需要匹配的
                if (indexArray != null){
                    if (indexArray.contains(item.index)){
                        //如果已经匹配完了，没必要浪费时间据需往下匹配了
                        tempIndex++
                        if (tempIndex > indexArray.size){
                            break
                        }
                        val startIndex = item.value.split(",")[0].toInt()
                        val endIndex = item.value.split(",")[1].toInt()
                        if (endIndex > text.length) {
                            break
                        }
                        //Log.e("日志","循环结果："+item)
                        setSuperStyle(startIndex,endIndex,type,false,superTextColor,backgroundColor,img,scalePercent,scaleValue,false,enableUnderLine,isCenter)
                    }
                }else{
                    //获取匹配目标的开始和结束位置
                    val startIndex = item.value.split(",")[0].toInt()
                    val endIndex = item.value.split(",")[1].toInt()
                    if (endIndex > text.length) {
                        break
                    }
                    setSuperStyle(startIndex,endIndex,type,false,superTextColor,backgroundColor,img,scalePercent,scaleValue,false,enableUnderLine,isCenter)
                }
            }
            text = stringBuffer
        }else{
            //不需要匹配全部目标时,只需要匹配到文本的第一个目标即可
            var startIndex = text.toString().indexOf(matchStr)
            if (startIndex == -1){
                return
            }
            var endIndex = startIndex + matchStr.length
            if (startIndex < 0){
                startIndex = 0
            }
            if (endIndex > text.toString().length){
                endIndex = text.toString().length
            }
            setSuperStyle(startIndex,endIndex,type,false,superTextColor,backgroundColor,img,scalePercent,scaleValue,true,enableUnderLine,isCenter)
        }
    }


    /**
     * 通过url的开始和结束位置设置样式
     */
    private fun setUrlStrStyle(indexArray:Array<Int>?,type:Int,superTextColor:Int = 0,backgroundColor:Int=0,scalePercent: Float = this.superTextScalePrecent,scaleValue:Int=this.superTextSize,enableUnderLine: Boolean = this.enableClickUnderLine){
        val isReset = compareText()
        //避免设置时每次都需要生成一遍数据,如果存在可用数据,就使用可用数据
        if (isReset || matchStrArray.size == 0){
            Log.e("日志","我被执行了")
            matchStrArray = TextUtils.getUrlArray(text.toString())
        }
        var tempIndex = 0
        for (item in matchStrArray.withIndex()){
            if (indexArray != null){
                if (indexArray.contains(item.index)) {
                    //如果已经匹配完了，没必要浪费时间据需往下匹配了
                    tempIndex++
                    if (tempIndex > indexArray.size) {
                        break
                    }
                    val startIndex = item.value.split(",")[0].toInt()
                    val endIndex = item.value.split(",")[1].toInt()
                    if (endIndex > text.length) {
                        break
                    }
                    setSuperStyle(startIndex,endIndex,type,true,superTextColor,backgroundColor,0,scalePercent,scaleValue,false,enableUnderLine)
                }
            }else{
                val startIndex = item.value.split(",")[0].toInt()
                val endIndex = item.value.split(",")[1].toInt()
                setSuperStyle(startIndex,endIndex,type,true,superTextColor,backgroundColor,0,scalePercent,scaleValue,false,enableUnderLine)
            }
        }
        text = stringBuffer
    }


    /**
     * 真正开始设置样式
     * @param startPosition 特殊样式开始位置
     * @param endPosition 特殊样式结束位置
     * @param includeClick 是否启用点击
     * @param superTextColor 特殊样式字体样色
     * @param img 需要插入的图片
     * @param scalePercent 字体缩放比例
     * @param scaleValue 字体缩放的具体大小
     * @param refreshNow 是否马上刷新,当出现for循环时,此属性会改为false,避免textview多次重复绘制浪费不必要的资源
     */
    private fun setSuperStyle(startPosition:Int = this.startPosition,endPosition:Int = this.endPosition,type:Int,includeClick:Boolean,superTextColor:Int = 0,backgroundColor:Int=this.superTextBackgroundColor,img:Int=0,scalePercent: Float = this.superTextScalePrecent,scaleValue:Int=this.superTextSize,refreshNow:Boolean = true,enableUnderLine: Boolean = this.enableClickUnderLine,isCenter: Boolean = false){
        when (type) {
            SuperTextConfig.Style.LINE -> {
                val lineStype = StrikethroughSpan()
                stringBuffer?.setSpan(lineStype, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.UNDER_LINE -> {
                val underlineStype = UnderlineSpan()
                stringBuffer?.setSpan(underlineStype, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.BOLD -> {
                val styleSpa = StyleSpan(Typeface.BOLD)
                stringBuffer?.setSpan(styleSpa, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.ITALIC -> {
                val styleSpan = StyleSpan(Typeface.ITALIC)
                stringBuffer?.setSpan(styleSpan, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.SCALE_PERCENT -> {
                val styleSpan = RelativeSizeSpan(scalePercent)
                stringBuffer?.setSpan(styleSpan, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.SCALE_VALUE -> {
                val lineStype = AbsoluteSizeSpan(scaleValue.toInt(), true)
                stringBuffer?.setSpan(lineStype, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.BACKGROUND_COLOR -> {
                val backgroundColorSpan = BackgroundColorSpan(backgroundColor)
                stringBuffer?.setSpan(backgroundColorSpan, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.COLOR -> {
                val lineStype = ForegroundColorSpan(superTextColor)
                stringBuffer?.setSpan(lineStype, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.IMG -> {
                if (isCenter){
                    val imgspan = CenterAlignImageSpan(context!!, img)
                    stringBuffer?.setSpan(imgspan, startPosition, endPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                }else{
                    val imgspan = ImageSpan(context!!, img)
                    stringBuffer?.setSpan(imgspan, startPosition, endPosition, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                }
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.CLICK -> {
                val clickSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        Log.e("日志","点击监听器是否为空:${clickCallback == null}")
                        clickCallback?.onClick(startPosition, endPosition, text.substring(startPosition, endPosition))
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = enableUnderLine
                    }
                }
                stringBuffer?.setSpan(clickSpan, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                movementMethod = LinkMovementMethod.getInstance();
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.SUBSCRIPT -> {
                val lineStype = SubscriptSpan()
                stringBuffer?.setSpan(lineStype, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
            SuperTextConfig.Style.SUPERSCRIPT -> {
                val lineStype = SuperscriptSpan()
                stringBuffer?.setSpan(lineStype, startPosition, endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                if (refreshNow){
                    text = stringBuffer
                }
            }
        }
    }

    /**
     * 设置样式点击监听器
     */
    fun setOnStyleFontClickListener(clickClickListener:SuperTextClickListener){
        this.clickCallback = clickClickListener
    }

    override fun onDraw(canvas: Canvas?) {
        if(compareText()){
            initData()
            return
        }
        if (superTextEnablePortrait) {
            //竖排文字
            drawPortraintText(canvas)
        } else {
            //绘制填充的背景色
            val path = Path()
            path.addRoundRect(RectF(0f+strokePaint.strokeWidth/2,0f+strokePaint.strokeWidth/2,width.toFloat()-strokePaint.strokeWidth/2,height.toFloat()-strokePaint.strokeWidth/2),roundValue.toFloatArray(),Path.Direction.CW)
            canvas?.drawPath(path,backgroundSrcPaint)

            if (strokePaint.strokeWidth >= height/2){
                strokePaint.strokeWidth = (height/2.0f)
            }
            //绘制描边颜色
            val pathStroke = Path()
            pathStroke.addRoundRect(RectF(0f+strokePaint.strokeWidth/2,0f+strokePaint.strokeWidth/2,width.toFloat()-strokePaint.strokeWidth/2,height.toFloat()-strokePaint.strokeWidth/2),roundValue.toFloatArray(),Path.Direction.CW)
            canvas?.drawPath(pathStroke,strokePaint)

            //普通排版
            drawAddTo(canvas)
            super.onDraw(canvas)

        }
    }

    /**
     * 绘制竖排文字
     */
    private fun drawPortraintText(canvas: Canvas?) {
        canvas?.let {
            val startX = paddingLeft
            val endX = it.width - paddingRight
            val startY = paddingTop
            val stopY = it.height - paddingBottom
            var row = 0
            var col = 0
            for (item in text.withIndex()){
                //行距最小距离为1，不允许小于1
                var lineScale = lineSpacingMultiplier
                if (lineScale < 1){
                    lineScale = 1f
                }
                //计算行距
                val rowSpace = Math.abs(lineScale - 1.0f)*paint.measureText(portaitStr)
                //计算文字y坐标
                var y = startY + (paint.measureText(portaitStr) + rowSpace )*col+paint.measureText(portaitStr)
                //不允许y坐标超出画布
                if (y > stopY){
                    row++
                    col = 0
                    y = startY + (paint.measureText(portaitStr) + rowSpace )*col+paint.measureText(portaitStr)
                    col = 1
                }else{
                    col++
                }
                //计算文字x坐标
                var x = startX.toFloat() + (paint.measureText(portaitStr))*row
                if (superTextGravity == SuperTextConfig.Gravity.LEFT){
                    x = startX.toFloat() + (paint.measureText(portaitStr))*row
                }else if (superTextGravity == SuperTextConfig.Gravity.RIGHT){
                    x = endX - (paint.measureText(portaitStr))*row - paint.measureText(portaitStr)
                }
                //绘制文本
                canvas.drawText(item.value.toString(), x,y,paint)
            }
        }

    }


    /**
     * 获取值追加文字
     */
    private fun drawAddTo(canvas: Canvas?) {
        addToEndText?.let {
            stringBuffer?.let {
                //如果追加过文本，需要将追加的文本去除
                var temp = it.toString()
                var tempWidth = 0f
                for (item in 0..temp.length-1){
                    if (tempWidth > (width) - paddingLeft - paddingRight){
                        tempWidth = width - tempWidth
                    }
                    tempWidth += paint.measureText("${temp[item]}")
                }
                val test = addToEndText
                var tempTestLenght =paint.measureText(test)
                //如果最终的剩余宽度足够添加末尾文字，则添加末尾文字
                if (tempWidth + tempTestLenght < width-paddingLeft-paddingRight){
                    //计算末尾需要补充的占位符个数
                    val placeholderCount = (width - tempWidth - tempTestLenght - paddingLeft - paddingRight)/paint.measureText(" ")
                    //插入占位符
                    for (item in 0..placeholderCount.toInt()-1){
                        stringBuffer?.append(" ")
                        excludeStr += " "
                    }
                    excludeStr+=test
                    stringBuffer?.append(test)
                }else if ( tempWidth + tempTestLenght == width.toFloat()-paddingLeft-paddingRight ){
                    //此时因为之前的原有字符加末尾的字符等于textview的宽度，这时可以直接插入
                    stringBuffer?.append(test)
                    excludeStr+=test
                }else{
                    //原本的字符长度太长，无法进行插入，则直接开启下一行进行插入
                    stringBuffer?.append("\n")
                    excludeStr+="\n"
                    val placeholderCount = (width - tempTestLenght - paddingLeft - paddingRight)/paint.measureText(" ")
                    for (item in 0..((placeholderCount.toInt())-1)){
                        stringBuffer?.append(" ")
                        excludeStr += " "
                    }
                    excludeStr+=test
                    stringBuffer?.append(test)
                }
                text =stringBuffer
                addToEndText = null
            }
        }
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (superTextEnablePortrait) {
            val selfHeight = reSize(0, heightMeasureSpec, true)
            val selfWidth = reSize(0, widthMeasureSpec, false)
            setMeasuredDimension(selfWidth, selfHeight)
        } else {
            if (text.toString().isEmpty()){
                super.onMeasure(0, 0)
            }else{
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
    }

    /**
     * 设置宽和高
     */
    fun reSize(size: Int, measureSpec: Int, isHeight: Boolean): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        var lineScale = lineSpacingMultiplier
        var wordScale = wordSpacingMultiplier
        if (lineScale <= 1){
            lineScale = 1f
        }
        if (wordScale <= 1){
            wordScale = 1f
        }
        if (maxLines <=1 ){
            maxLines = 1
        }
        val rowSpace = Math.abs(lineScale - 1.0f)*paint.measureText(portaitStr)*maxLines
        val colSpace = Math.abs(wordScale - 1.0f)*paint.measureText(portaitStr)*(text.toString().length / maxLines + 0.99)
        var rowNum = 0
        if (text.toString().length % maxLines == 0){
            rowNum = text.toString().length / maxLines
        }else{
            rowNum = text.toString().length / maxLines + 1
        }
        return when (specMode) {
            MeasureSpec.UNSPECIFIED -> {
                if (isHeight) {
                    if (maxLines < text.toString().length) {
                        (paint.measureText(portaitStr) * maxLines + rowSpace).toInt() + paddingTop + paddingBottom
                    } else {
                        (paint.measureText(portaitStr) * text.toString().length + rowSpace).toInt() + paddingTop + paddingBottom
                    }
                } else {
                    (rowNum * (paint.measureText(portaitStr)) + colSpace).toInt() + paddingLeft + paddingRight
                }
            }
            MeasureSpec.AT_MOST -> {
                //设定宽高原则是，总列数宽度或总行数高度与可用宽度或高度比较，哪个值小使用那个，这样可以避免文字内容溢出可用宽度或高度的情况
                if (isHeight) {
                    if (maxLines < text.toString().length) {
                        Math.min((paint.measureText(portaitStr) * maxLines + rowSpace).toInt() + paddingTop + paddingBottom, specSize)
                    } else {
                        Math.min((paint.measureText(portaitStr) * text.toString().length + rowSpace).toInt() + paddingTop + paddingBottom, specSize)
                    }
                } else {
                    Math.min((rowNum * (paint.measureText(portaitStr)) + colSpace).toInt() + paddingLeft + paddingRight, specSize)
                }
            }
            MeasureSpec.EXACTLY -> {
                specSize
            }
            else -> {
                size
            }
        }
    }



    object SuperTextConfig{
        //竖排文字的起始、结束位置，仅对竖排文字有效
        object Gravity{
            const val LEFT = 0
            const val RIGHT = 1
            const val CENTER_START = 2
            const val CENTER_END = 3
        }

        /**
         * 特殊样式类型
         */
        object Style{
            //删除线
            const val LINE = 0
            //下划线
            const val UNDER_LINE = 1
            //加粗
            const val BOLD = 2
            //斜体
            const val ITALIC = 3
            //按比例缩放
            const val SCALE_PERCENT = 4
            //缩放到具体的值
            const val SCALE_VALUE = 5
            //设置背景色
            const val BACKGROUND_COLOR = 6
            //设置字体颜色
            const val COLOR = 7
            //点击
            const val CLICK = 8
            //图片
            const val IMG = 10
            //角标上
            const val SUBSCRIPT = 11
            //角标下
            const val SUPERSCRIPT = 12
        }
    }
}