package com.textutils.textview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.*
import android.text.style.*
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.core.text.toSpanned
import com.textutils.textview.R
import com.textutils.textview.SuperTextAddTextClickListener
import com.textutils.textview.SuperTextClickListener
import com.textutils.textview.utils.ModuleUtils
import com.textutils.textview.utils.TextUtils
import kotlin.math.abs


/**
 * 基于安卓textView进行修改,目的是方便各种样式的设置
 * @author libowu
 */
class SuperTextView : androidx.appcompat.widget.AppCompatTextView {
    //追加文本的特殊样式
    private var addTextSpannableString: SpannableStringBuilder? = null
    private var stringBuffer: SpannableStringBuilder? = null

    //临时保存文字大小
    private var tempTextSize = 0f

    //保存竖排文字时需要使用的特殊样式
    private var portraitStyleMap: HashMap<String, Int>? = null

    //点击监听
    private var clickCallback: SuperTextClickListener? = null

    //追加文本的监听器
    private var addTextClickListener: SuperTextAddTextClickListener? = null

    //这个变量是测量文字大小时使用的标准文字，即无论汉字、字母、数字或符号，最终测量出来的字体大小是已这个字符为标准的。这样的目的是避免文字在排版时出现混乱的情况
    private var portraitStr = "你"

    //是否支持竖排文字显示
    private var superTextEnablePortrait = false

    //获取文本内容时不应该获取到的文本内容，比如在文本末端添加的占位符和末尾按钮等这些文本不应该被获取到的
    private var excludeStr = ""

    //竖排文字的列数
    private var rowSize = 0

    //特殊样式的字体颜色
    private var superColor: Int = Color.BLACK

    //需要匹配的文案
    private var matchStr: String? = ""

    //是否对textView的所有文案进行目标匹配,匹配目标为matchStr
    private var matchEverySameStr: Boolean = false

    //需要设置特殊样式的起始位置
    private var startPosition: Int = 0

    //需要匹配特殊样式的结束位置
    private var endPosition: Int = 0

    //设置样式的样式类型
    private var styleType: Int = 0

    //特殊样式文字大小
    private var superTextSize: Int = 0

    //特殊样式文字缩放比例,按原文字比例
    private var superTextScalePrecent: Float = 1f

    //特殊样式文本背景
    private var superTextBackgroundColor: Int = Color.TRANSPARENT

    //是否设置点击时在点击文案下方添加下划线,默认打开划线
    private var enableClickUnderLine: Boolean = true

    //是否打开竖排文字
    private var enableVerticalType: Boolean = false

    //竖排文字的每个字符的间距倍数
    private var wordSpacingMultiplier: Float = 1f

    //竖排文字绘制起始地点,有start和end两种,start表示从左边开始蕙芷,end则表示从右边开始绘制
    private var superTextGravity: Int = 1

    //追加文本,即在行位添加需要追加的文案
    private var addToEndText: String? = ""

    //左上角圆角
    private var superTopLeftCorner: Float = 0f

    //左下角圆角
    private var superTopRightCorner: Float = 0f

    //左下角圆角
    private var superBottomLeftCorner: Float = 0f

    //右上角圆角
    private var superBottomRightCorner: Float = 0f

    //四个角的圆角
    private var superCorner: Float = 0f

    //圆角颜色填充
    private var superSolidColor = Color.TRANSPARENT

    //描边的宽度
    private var superStrokeWidth = 0f

    //描边的颜色
    private var superStrokeColor = Color.TRANSPARENT

    //字体路径，请保存到assets文件夹中
    private var superTextFontFace: String? = ""

    //打印日志的标识
    private val LOG = "superText"
    private var isRefreshNow: Boolean = true

    //保存匹配字符的位置信息集合
    private var matchStrArray: ArrayList<String> = ArrayList()

    //绘制背景的paint
    private var backgroundSrcPaint: Paint = Paint()

    //绘制描边的颜色
    private var strokePaint: Paint = Paint()

    //textView的行数
    private var superTextLineCount: Int = 0

    //文本追加区域的绘制矩阵,由于未知原因，在span中设置点击无效，只能通过点击是否在矩阵中进行判定了
    private var addTextRect: RectF? = null

    //竖排文字每列的高度
    private var rowTextHeight = 0

    //竖排文字的宽度
    private var rowTextWidth = 0

    //将要设置样式的文本类型
    private var stringType: StringType = StringType.NORMAL
    private val mPath = Path()
    private val mPathStroke = Path()

    private var roundValue = Array<Float>(8, { 0f })


    constructor(context: Context) : this(context, null)

    constructor(context: Context?, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context?, attr: AttributeSet?, def: Int) : super(context, attr, def) {
        //设置这个透明是为了在设置点击时避免出现背景色
        highlightColor = Color.TRANSPARENT
        stringBuffer = SpannableStringBuilder(text)
        tempTextSize = textSize
        portraitStyleMap = HashMap()
        getParameter(attr, def)
        initAddText()
        initCornr()
        initData()
        //设置绘制背景的画板
        backgroundSrcPaint.color = superSolidColor
        backgroundSrcPaint.isAntiAlias = true

        strokePaint.color = superStrokeColor
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = superStrokeWidth
        strokePaint.isAntiAlias = true

    }

    /**
     * 初始化文本追加
     */
    private fun initAddText() {
        addToEndText?.let {
            addTextSpannableString = SpannableStringBuilder(it)
        }
    }

    /**
     * 设置圆角
     */
    private fun initCornr() {
        if (superCorner != 0f) {
            roundValue.set(0, superCorner)
            roundValue.set(1, superCorner)
            roundValue.set(2, superCorner)
            roundValue.set(3, superCorner)
            roundValue.set(4, superCorner)
            roundValue.set(5, superCorner)
            roundValue.set(6, superCorner)
            roundValue.set(7, superCorner)
        } else {
            //左上角
            roundValue.set(0, superTopLeftCorner)
            roundValue.set(1, superTopLeftCorner)
            //右上角
            roundValue.set(2, superTopRightCorner)
            roundValue.set(3, superTopRightCorner)
            //右下角
            roundValue.set(4, superBottomRightCorner)
            roundValue.set(5, superBottomRightCorner)
            //左下角
            roundValue.set(6, superBottomLeftCorner)
            roundValue.set(7, superBottomLeftCorner)
        }
    }

    /**
     * h获取xml中的属性
     */
    private fun getParameter(attr: AttributeSet?, def: Int) {
        val parameterType = context.theme.obtainStyledAttributes(
            attr,
            R.styleable.SuperTextView, def, 0
        )
        superColor =
            parameterType.getColor(R.styleable.SuperTextView_stTextColor, textColors.defaultColor)
        matchStr = parameterType.getString(R.styleable.SuperTextView_stMatchStr)
        if (matchStr == null) {
            matchStr = ""
        }
        matchEverySameStr =
            parameterType.getBoolean(R.styleable.SuperTextView_stMatchEverySameStr, false)
        startPosition = parameterType.getInteger(R.styleable.SuperTextView_stStartPosition, 0)
        endPosition = parameterType.getInteger(R.styleable.SuperTextView_stEndPosition, 0)
        styleType = parameterType.getInt(R.styleable.SuperTextView_stViewType, 7)
        superTextSize = parameterType.getDimensionPixelSize(
            R.styleable.SuperTextView_stTextSize,
            textSize.toInt()
        )
        superTextEnablePortrait =
            parameterType.getBoolean(R.styleable.SuperTextView_stEnablePortrait, false)
        superTextSize = ModuleUtils.px2dip(
            context,
            superTextSize.toFloat()
        )
        superTextScalePrecent = parameterType.getFloat(R.styleable.SuperTextView_stScale, 1.0f)
        superTextBackgroundColor = parameterType.getColor(
            R.styleable.SuperTextView_stBackgroundColor,
            Color.parseColor("#74E1FF")
        )
        enableClickUnderLine =
            parameterType.getBoolean(R.styleable.SuperTextView_stEnableClickUnderline, true)
        superStrokeColor =
            parameterType.getColor(R.styleable.SuperTextView_stStrokeColor, Color.TRANSPARENT)
        superStrokeWidth = parameterType.getDimension(R.styleable.SuperTextView_stStrokeWidth, 0f)

        //enableVerticalType = parameterType.getBoolean(R.styleable.SuperTextView_superTextEnableSetType, true)
        //wordSpacingMultiplier = parameterType.getFloat(R.styleable.SuperTextView_wordSpacingMultiplier, 1.0f)
        superTextGravity = parameterType.getInt(R.styleable.SuperTextView_stGravity, 1)
        addToEndText = parameterType.getString(R.styleable.SuperTextView_stAddToEndText)
        superTopLeftCorner =
            parameterType.getDimension(R.styleable.SuperTextView_stTopLeftCorner, 0f)
        superTopRightCorner =
            parameterType.getDimension(R.styleable.SuperTextView_stTopRightCorner, 0f)
        superBottomLeftCorner =
            parameterType.getDimension(R.styleable.SuperTextView_stBottomLeftCorner, 0f)
        superBottomRightCorner =
            parameterType.getDimension(R.styleable.SuperTextView_stBottomRightCorner, 0f)
        superCorner = parameterType.getDimension(R.styleable.SuperTextView_stCorner, 0f)
        superSolidColor =
            parameterType.getColor(R.styleable.SuperTextView_stSolidColor, Color.TRANSPARENT)
        superTextFontFace = parameterType.getString(R.styleable.SuperTextView_stFontFace)
        wordSpacingMultiplier =
            parameterType.getFloat(R.styleable.SuperTextView_stWordSpacingMultiplier, 1.0f)
        parameterType.recycle()
    }

    /**
     * 将所有的样式设置为默认值
     */
    fun clearStyle() {
        superColor = textColors.defaultColor
        matchStr = ""
        matchEverySameStr = false
        startPosition = 0
        endPosition = 0
        styleType = SuperTextConfig.Style.COLOR
        superTextSize = ModuleUtils.px2dip(context, superTextSize.toFloat())
        superTextEnablePortrait = false
        superTextScalePrecent = 1f
        superTextBackgroundColor = Color.parseColor("#74E1FF")
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
        if (text.toString().isEmpty()) {
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
        } else if (0 - startPosition > 0) {
            startPosition = 0
        }
        stringBuffer = SpannableStringBuilder(text)
        matchStrArray.addAll(
            TextUtils.getMatchStrArray(
                matchStr,
                matchEverySameStr,
                text.toString(),
                text.toString()
            )
        )
        setStyle()
    }

    /**
     * 设置文字字体
     * @param fontFace 字体存放路径，可以是assets路径，也可以是手机文件里面的路径
     */
    fun setFontFace(fontFace: String? = superTextFontFace): SuperTextView {
        this.superTextFontFace = fontFace
        //设置字体
        if (!superTextFontFace.isNullOrEmpty()) {
            try {
                if (superTextFontFace!!.startsWith("/")) {
                    superTextFontFace = superTextFontFace!!.replaceFirst("/", "")
                }
                val end =
                    if (superTextFontFace!!.lastIndexOf("/") == -1) 0 else superTextFontFace!!.lastIndexOf(
                        "/"
                    )
                val start =
                    if (superTextFontFace!!.lastIndexOf("/") + 1 == 0) 0 else superTextFontFace!!.lastIndexOf(
                        "/"
                    ) + 1
                val list = context.assets.list(superTextFontFace!!.substring(0, end))
                val isExist =
                    list?.contains(superTextFontFace!!.substring(start, superTextFontFace!!.length))
                val font: Typeface
                font = if (isExist!!) {
                    Typeface.createFromAsset(context.assets, superTextFontFace)
                } else {
                    Typeface.createFromFile(superTextFontFace)
                }
                if (font != null) {
                    typeface = font
                }
            } catch (e: Exception) {
                Log.e(
                    LOG,
                    String.format(
                        context.resources.getString(R.string.readFontFaceFail),
                        e.localizedMessage
                    )
                )
            }
        }
        invalidate()
        return this
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                addTextRect?.let {
                    if (it.contains(event.x, event.y)) {
                        addTextClickListener?.onAddTextClick(addToEndText)
                        return true
                    }/*else{

                    }*/
                }
            }
        }
        return super.onTouchEvent(event)
    }


    /**
     * 通过xml属性设置不同的样式
     */
    private fun setStyle() {
        when (styleType) {
            SuperTextConfig.Style.LINE -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanLine()
                    setSpanColor()
                }else{
                    setSpanLineStr()
                    setSpanColorStr()
                }
            }
            SuperTextConfig.Style.UNDER_LINE -> {
                if (matchStr.isNullOrEmpty()) {
                    setSpanUnderline()
                    setSpanColor()
                }else{
                    setSpanUnderlineStr()
                    setSpanColorStr()
                }
            }
            SuperTextConfig.Style.BOLD -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanBold()
                    setSpanColor()
                }else{
                    setSpanBoldStr()
                    setSpanColorStr()
                }
            }
            SuperTextConfig.Style.ITALIC -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanItalic()
                    setSpanColor()
                }else{
                    setSpanItalicStr()
                    setSpanColorStr()
                }

            }
            SuperTextConfig.Style.SCALE_PERCENT -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanScalePercent()
                    setSpanColor()
                }else{
                    setSpanScalePercentStr()
                    setSpanColorStr()
                }

            }
            SuperTextConfig.Style.SCALE_VALUE -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanScaleValue()
                    setSpanColor()
                }else{
                    setSpanScaleValueStr()
                    setSpanColorStr()
                }

            }
            SuperTextConfig.Style.BACKGROUND_COLOR -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanBackgroundColor()
                    setSpanColor()
                }else{
                    setSpanBackgroundColorStr()
                    setSpanColorStr()
                }

            }
            SuperTextConfig.Style.COLOR -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanColor()
                }else{
                    setSpanColorStr()
                }
            }
            SuperTextConfig.Style.SUBSCRIPT -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanSubscript()
                    setSpanColor()
                }else{
                    setSpanSubscriptStr()
                    setSpanColorStr()
                }

            }
            SuperTextConfig.Style.SUPERSCRIPT -> {
                if (matchStr.isNullOrEmpty()){
                    setSpanSuperscript()
                    setSpanColor()
                }else{
                    setSpanSuperscriptStr()
                    setSpanColorStr()
                }

            }
        }
    }

    /**
     * 这个放啊是为了使调用者可以直接使用text的setText()方法
     * @return stringBuffer是否被重置了
     */
    private fun compareText(): Boolean {
        if (stringBuffer != null && !text.toString().equals(stringBuffer.toString())) {
            setFontFace(superTextFontFace)
            stringBuffer = SpannableStringBuilder(text)
            matchStrArray.clear()
            return true
        }
        return false
    }

    /**
     * 获取开始和结束位置,使用这个方法目的是为了避免外部调用者传入开始和结束位置有问题导致软件崩溃的问题
     */
    private fun getStartAndEndPosition(startPosition: Int, endPosition: Int): ArrayList<Int> {
        var tempStartPosition = startPosition
        var tempEndPosition = endPosition
        var stringText = this.text
        when(stringType){
            StringType.ADD_TEXT -> stringText = addTextSpannableString.toString()
        }
        if (tempEndPosition < 0) {
            tempEndPosition = 0
        }
        if (tempStartPosition < 0) {
            tempStartPosition = 0
        }
        if (tempEndPosition > stringText.length) {
            tempEndPosition = stringText.length
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
    fun setSpanLine(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(startPosition, endPosition, SuperTextConfig.Style.LINE, false)
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanLineStr(
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.LINE, 0, 0, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置删下划线
     */
    fun setSpanUnderline(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.UNDER_LINE
        )
        return this
    }

    /**
     * 设置删下划线
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanUnderlineStr(
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.UNDER_LINE, 0, 0, 0, 0f, 0
        )
        return this
    }


    /**
     * 设置粗体
     */
    fun setSpanBold(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.BOLD
        )
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanBoldStr(
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.BOLD, 0, 0, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置斜体
     */
    fun setSpanItalic(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.ITALIC
        )
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanItalicStr(
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.ITALIC, 0, 0, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置删除线，设置依据为字符的匹配
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanSubscriptStr(
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.SUBSCRIPT, 0, 0, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置角标上
     */
    fun setSpanSubscript(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.SUBSCRIPT
        )
        return this
    }

    /**
     * 设置角标上
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanSuperscriptStr(
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.SUPERSCRIPT, 0, 0, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置角标下
     */
    fun setSpanSuperscript(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.SUPERSCRIPT
        )
        return this
    }


    /**
     * 设置缩放比例
     */
    fun setSpanScalePercent(
        scalePercent: Float = superTextScalePrecent,
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.SCALE_PERCENT, false, 0, 0, 0, scalePercent, 0
        )
        return this
    }

    /**
     * 设置缩放比例
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanScalePercentStr(
        scalePercent: Float = this.superTextScalePrecent,
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.SCALE_PERCENT, 0, 0, 0, scalePercent, 0
        )
        return this
    }

    /**
     * 设置缩放具体字体大小
     */
    fun setSpanScaleValue(
        superTextSize: Int = this.superTextSize,
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.SCALE_VALUE, false, 0, 0, 0, 0f, superTextSize
        )
        return this
    }

    /**
     * 设置缩放具体字体大小
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanScaleValueStr(
        scaleValue: Int = this.superTextSize,
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.SCALE_VALUE, 0, 0, 0, 0f, scaleValue
        )
        return this
    }


    /**
     * 设置特殊样式背景色
     */
    fun setSpanBackgroundColor(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition,
        backgroundColor: Int = this.superTextBackgroundColor
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.BACKGROUND_COLOR, false, 0, backgroundColor, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置特殊样式背景色
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanBackgroundColorStr(
        backgroundColor: Int = this.superTextBackgroundColor,
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.BACKGROUND_COLOR, 0, backgroundColor, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置特殊样式字体颜色
     */
    fun setSpanColor(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition,
        superTextColor: Int = this.superColor
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.COLOR, false, superTextColor, 0, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置特殊样式字体颜色
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanColorStr(
        textColor: Int = this.superColor,
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.COLOR, textColor, 0, 0, 0f, 0
        )
        return this
    }

    /**
     * 设置特殊样式字体点击
     */
    fun setSpanClick(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition,
        enableUnderLine: Boolean = this.enableClickUnderLine
    ): SuperTextView {
        setPositionStyle(
            startPosition, endPosition,
            SuperTextConfig.Style.CLICK, false, 0, 0, 0, 0f, 0, enableUnderLine
        )
        return this
    }

    /**
     * 设置特殊样式字体点击
     * @param matchStr 需要匹配的字符
     * @param matchAll 是否在文中匹配所有该字符
     * @param indexArray 需要匹配的index，比如如果只需要第二被改变样式，则此indexArray中应该放入2
     */
    fun setSpanClickStr(
        matchStr: String = this.matchStr!!,
        enableUnderLine: Boolean = this.enableClickUnderLine,
        matchAll: Boolean = false,
        indexArray: Array<Int>? = null
    ): SuperTextView {
        setMatchStrStyle(
            matchStr, matchAll, indexArray,
            SuperTextConfig.Style.CLICK, 0, 0, 0, 0f, 0, enableUnderLine
        )
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlClick(
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(matchArray, SuperTextConfig.Style.CLICK, 0, 0, 0f, 0, enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlColor(
        superTextColor: Int = this.superColor,
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(
            matchArray,
            SuperTextConfig.Style.COLOR,
            superTextColor,
            0,
            0f,
            0,
            enableUnderlink
        )
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlBold(
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(matchArray, SuperTextConfig.Style.BOLD, 0, 0, 0f, 0, enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlBackgroundColor(
        backgroundColor: Int = this.superTextBackgroundColor,
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(
            matchArray,
            SuperTextConfig.Style.BOLD,
            0,
            backgroundColor,
            0f,
            0,
            enableUnderlink
        )
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlItalic(
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(matchArray, SuperTextConfig.Style.ITALIC, 0, 0, 0f, 0, enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlLine(
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(matchArray, SuperTextConfig.Style.LINE, 0, 0, 0f, 0, enableUnderlink)
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlScalePrecent(
        scalePrecent: Float = this.superTextScalePrecent,
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(
            matchArray,
            SuperTextConfig.Style.SCALE_PERCENT,
            0,
            0,
            scalePrecent,
            0,
            enableUnderlink
        )
        return this
    }

    /**
     * 将文本中所有链接改为可点击状态
     * @param enableUnderlink 是否打开下划线的显示,默认显示下划线
     */
    fun setSpanUrlScaleValue(
        scaleValue: Int = this.superTextSize,
        enableUnderlink: Boolean = true,
        matchArray: Array<Int>? = null
    ): SuperTextView {
        setUrlStrStyle(
            matchArray,
            SuperTextConfig.Style.SCALE_PERCENT,
            0,
            0,
            0f,
            scaleValue,
            enableUnderlink
        )
        return this
    }

    /**
     * 设置图片
     * @param startPosition 特殊样样式起始位置
     * @param endPosition 特殊样式结束位置
     * @param img 图片
     * @param isRefreshNow 是否设置后立即属性
     */
    fun setSpanImage(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition,
        img: Int,
        isCenter: Boolean = false
    ): SuperTextView {
        setPositionStyle(
            startPosition,
            endPosition,
            SuperTextConfig.Style.IMG,
            false,
            0,
            0,
            img,
            0f,
            0,
            false,
            isCenter
        )
        return this
    }

    fun setSpanImageStr(
        img: Int,
        matchStr: String = this.matchStr!!,
        matchAll: Boolean = false,
        isCenter: Boolean = true,
        indexArray: Array<Int>? = null
    ) {
        setMatchStrStyle(
            matchStr,
            matchAll,
            indexArray,
            SuperTextConfig.Style.IMG,
            0,
            0,
            img,
            0f,
            0,
            false,
            isCenter
        )
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
    private fun setPositionStyle(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition,
        type: Int,
        includeClick: Boolean = false,
        superTextColor: Int = 0,
        backgroundColor: Int = this.superTextBackgroundColor,
        img: Int = 0,
        scalePercent: Float = this.superTextScalePrecent,
        scaleValue: Int = this.superTextSize,
        enableUnderLine: Boolean = this.enableClickUnderLine,
        isCenter: Boolean = false
    ) {
        this.startPosition = startPosition
        this.endPosition = endPosition
        compareText()
        //获取开始和结束位置
        val tempStartPosition = getStartAndEndPosition(startPosition, endPosition).get(0)
        val tempEndPosition = getStartAndEndPosition(startPosition, endPosition).get(1)
        setSuperStyle(
            tempStartPosition,
            tempEndPosition,
            type,
            includeClick,
            superTextColor,
            backgroundColor,
            img,
            scalePercent,
            scaleValue,
            isRefreshNow,
            enableUnderLine,
            isCenter
        )
    }

    /**
     * 通過字符串匹配獲取开始位置和解锁位置
     */
    private fun setMatchStrStyle(
        matchStr: String,
        matchAll: Boolean,
        indexArray: Array<Int>?,
        type: Int,
        superTextColor: Int = 0,
        backgroundColor: Int = 0,
        img: Int = 0,
        scalePercent: Float = this.superTextScalePrecent,
        scaleValue: Int = this.superTextSize,
        enableUnderLine: Boolean = this.enableClickUnderLine,
        isCenter: Boolean = false
    ) {
        val isReset = compareText()
        matchEverySameStr = matchAll
        var text = this.text.toString()
        when(stringType){
            StringType.ADD_TEXT -> text = this.addTextSpannableString.toString()
        }
        if (matchAll) {
            //如果匹配所有字符，则清除之前的数据，通过递归重新获取
            if (matchStr != this.matchStr || isReset) {
                Log.e(LOG,"进入文字赛选")
                this.matchStrArray.clear()
                matchStrArray.addAll(
                    TextUtils.getMatchStrArray(
                        matchStr,
                        matchAll,
                        text,
                        text
                    )
                )
            }
            var tempIndex = 0
            for (item in matchStrArray.withIndex()) {
                //通过调用者传入的数组判断哪些目标是需要匹配的
                if (indexArray != null) {
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
                        //Log.e("日志","循环结果："+item)
                        setSuperStyle(
                            startIndex,
                            endIndex,
                            type,
                            false,
                            superTextColor,
                            backgroundColor,
                            img,
                            scalePercent,
                            scaleValue,
                            false,
                            enableUnderLine,
                            isCenter
                        )
                    }
                } else {
                    //获取匹配目标的开始和结束位置
                    val startIndex = item.value.split(",")[0].toInt()
                    val endIndex = item.value.split(",")[1].toInt()
                    if (endIndex > text.length) {
                        break
                    }
                    setSuperStyle(
                        startIndex,
                        endIndex,
                        type,
                        false,
                        superTextColor,
                        backgroundColor,
                        img,
                        scalePercent,
                        scaleValue,
                        false,
                        enableUnderLine,
                        isCenter
                    )
                }
            }
            if (isRefreshNow && stringType == StringType.NORMAL) {
                this.text = stringBuffer
            } else if (isRefreshNow && stringType == StringType.ADD_TEXT) {
                invalidate()
            }
        } else {
            var text = this.text.toString()
            when(stringType){
                StringType.ADD_TEXT -> text = this.addTextSpannableString.toString()
            }
            //不需要匹配全部目标时,只需要匹配到文本的第一个目标即可
            var startIndex = text.indexOf(matchStr)
            if (startIndex == -1) {
                return
            }
            var endIndex = startIndex + matchStr.length
            if (startIndex < 0) {
                startIndex = 0
            }
            if (endIndex > text.length) {
                endIndex = text.length
            }
            setSuperStyle(
                startIndex,
                endIndex,
                type,
                false,
                superTextColor,
                backgroundColor,
                img,
                scalePercent,
                scaleValue,
                isRefreshNow,
                enableUnderLine,
                isCenter
            )
        }
        this.matchStr = matchStr

    }


    /**
     * 通过url的开始和结束位置设置样式
     */
    private fun setUrlStrStyle(
        indexArray: Array<Int>?,
        type: Int,
        superTextColor: Int = 0,
        backgroundColor: Int = 0,
        scalePercent: Float = this.superTextScalePrecent,
        scaleValue: Int = this.superTextSize,
        enableUnderLine: Boolean = this.enableClickUnderLine
    ) {
        val isReset = compareText()
        //避免设置时每次都需要生成一遍数据,如果存在可用数据,就使用可用数据
        if (isReset || matchStrArray.size == 0) {
            matchStrArray = TextUtils.getUrlArray(text.toString())
        }
        var tempIndex = 0
        for (item in matchStrArray.withIndex()) {
            if (indexArray != null) {
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
                    setSuperStyle(
                        startIndex,
                        endIndex,
                        type,
                        true,
                        superTextColor,
                        backgroundColor,
                        0,
                        scalePercent,
                        scaleValue,
                        false,
                        enableUnderLine
                    )
                }
            } else {
                val startIndex = item.value.split(",")[0].toInt()
                val endIndex = item.value.split(",")[1].toInt()
                setSuperStyle(
                    startIndex,
                    endIndex,
                    type,
                    true,
                    superTextColor,
                    backgroundColor,
                    0,
                    scalePercent,
                    scaleValue,
                    false,
                    enableUnderLine
                )
            }
        }
        text = stringBuffer
    }


    /**
     * 设置是否立马刷新
     */
    fun setIsRefreshNow(isRefreshNow: Boolean): SuperTextView {
        this.isRefreshNow = isRefreshNow
        return this
    }

    /**
     * 立即刷新数据
     */
    fun refreshNow() {
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
     * @param refreshNow 是否马上刷新,当出现for循环时,此属性会改为false,避免textView多次重复绘制浪费不必要的资源
     */
    private fun setSuperStyle(
        startPosition: Int = this.startPosition,
        endPosition: Int = this.endPosition,
        type: Int,
        includeClick: Boolean,
        superTextColor: Int = 0,
        backgroundColor: Int = this.superTextBackgroundColor,
        img: Int = 0,
        scalePercent: Float = this.superTextScalePrecent,
        scaleValue: Int = this.superTextSize,
        refreshNow: Boolean = true,
        enableUnderLine: Boolean = this.enableClickUnderLine,
        isCenter: Boolean = false
    ) {
        try {
            //根据不同类别设置不同类型的文字
            var stringBuffer = this.stringBuffer
            stringBuffer = when (stringType) {
                StringType.NORMAL -> this.stringBuffer
                StringType.ADD_TEXT -> addTextSpannableString
            }

            when (type) {
                SuperTextConfig.Style.LINE -> {
                    val lineStyle = StrikethroughSpan()
                    stringBuffer?.setSpan(
                        lineStyle,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.UNDER_LINE -> {
                    val underlineStyle = UnderlineSpan()
                    stringBuffer?.setSpan(
                        underlineStyle,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.BOLD -> {
                    val styleSpa = StyleSpan(Typeface.BOLD)
                    stringBuffer?.setSpan(
                        styleSpa,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.ITALIC -> {
                    val styleSpan = StyleSpan(Typeface.ITALIC)
                    stringBuffer?.setSpan(
                        styleSpan,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.SCALE_PERCENT -> {
                    val styleSpan = RelativeSizeSpan(scalePercent)
                    stringBuffer?.setSpan(
                        styleSpan,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.SCALE_VALUE -> {
                    val lineStyle = AbsoluteSizeSpan(scaleValue.toInt(), true)
                    stringBuffer?.setSpan(
                        lineStyle,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.BACKGROUND_COLOR -> {
                    val backgroundColorSpan = BackgroundColorSpan(backgroundColor)
                    stringBuffer?.setSpan(
                        backgroundColorSpan,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.COLOR -> {
                    val lineStyle = ForegroundColorSpan(superTextColor)
                    stringBuffer?.setSpan(
                        lineStyle,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.IMG -> {
                    if (isCenter) {
                        val imgspan = CenterAlignImageSpan(context!!, img)
                        stringBuffer?.setSpan(
                            imgspan,
                            startPosition,
                            endPosition,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        val imgspan = ImageSpan(context!!, img)
                        stringBuffer?.setSpan(
                            imgspan,
                            startPosition,
                            endPosition,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.CLICK -> {
                    val clickSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            clickCallback?.onClick(
                                startPosition,
                                endPosition,
                                text.substring(startPosition, endPosition)
                            )
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = enableUnderLine
                        }
                    }
                    stringBuffer?.setSpan(
                        clickSpan,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    movementMethod = /*LinkMovementMethod.getInstance();*/SuperTextMovementMethod
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.SUBSCRIPT -> {
                    val lineStyle = SubscriptSpan()
                    stringBuffer?.setSpan(
                        lineStyle,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
                SuperTextConfig.Style.SUPERSCRIPT -> {
                    val lineStyle = SuperscriptSpan()
                    stringBuffer?.setSpan(
                        lineStyle,
                        startPosition,
                        endPosition,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (refreshNow && stringType == StringType.NORMAL) {
                        text = stringBuffer
                    } else if (refreshNow && stringType == StringType.ADD_TEXT) {
                        invalidate()
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(LOG, "设置样式错误:${e.localizedMessage}")
        }
    }

    /**
     * 设置追加文本
     * @param addText 追文本内容
     */
    fun setAddText(addText: CharSequence): SuperTextView {
        addToEndText = addText.toString()
        addTextSpannableString = SpannableStringBuilder(addText)
        invalidate()
        return this
    }

    /**
     * 设置将要设置样式的所属文案类型
     * 设置类型觉得了后面设置文案样式时设置的目标文案.比如调用此方法设置了NORMAL,也就是在调用这个方法后,后面所设置的文案样式只针对正常文本和追加文本有效
     * @param type 文案类型,有NORMAL,ADD_TEXT
     * NORMAL:正常文本和竖排文本
     * ADD_TEXT:追加文本
     */
    fun setStringType(type: StringType): SuperTextView {
        this.stringType = type
        return this
    }

    /**
     * 设置样式点击监听器
     */
    fun setOnStyleFontClickListener(clickClickListener: SuperTextClickListener): SuperTextView {
        this.clickCallback = clickClickListener
        return this
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        if (compareText()) {
            initData()
            return
        }

        //绘制填充的背景色
        if (mPath.isEmpty){
            mPath.addRoundRect(
                RectF(
                    0f + strokePaint.strokeWidth / 2,
                    0f + strokePaint.strokeWidth / 2,
                    width.toFloat() - strokePaint.strokeWidth / 2,
                    height.toFloat() - strokePaint.strokeWidth / 2
                ), roundValue.toFloatArray(), Path.Direction.CW
            )
            canvas?.drawPath(mPath, backgroundSrcPaint)
        }

        if (strokePaint.strokeWidth >= height / 2) {
            strokePaint.strokeWidth = (height / 2.0f)
        }
        //绘制描边颜色
        if (mPathStroke.isEmpty){
            mPathStroke.addRoundRect(
                RectF(
                    0f + strokePaint.strokeWidth / 2,
                    0f + strokePaint.strokeWidth / 2,
                    width.toFloat() - strokePaint.strokeWidth / 2,
                    height.toFloat() - strokePaint.strokeWidth / 2
                ), roundValue.toFloatArray(), Path.Direction.CW
            )
            canvas?.drawPath(mPathStroke, strokePaint)
        }

        if (superTextEnablePortrait) {
            //竖排文字
            drawPortraintText(canvas)
        } else {
            //存在文本追加时，如果文本到达或超出追加文本的显示区域，超出部分被截断并用“...”.如果存在需要截断的内容，则结束此次绘制，进入下一次绘制
            if (checkLastLineWidth()) {
                return
            }
            //普通排版
            super.onDraw(canvas)
            superTextLineCount = lineCount
            drawAddTo(canvas)
        }
    }

    /**
     * 获取值追加文字
     */
    private fun drawAddTo(canvas: Canvas?) {
        //存在水平居中的没必要添加追加文字，顾直接结束掉
        if (gravity == Gravity.CENTER || gravity == Gravity.CENTER_HORIZONTAL) {
            return
        }
        if (addToEndText == null && addTextSpannableString == null) {
            return
        }
        if (!addTextSpannableString.isNullOrEmpty()) {
            //防止linespace为0时计算出错
            var lineSpace = lineSpacingMultiplier
            if (lineSpace == 0f){
                lineSpace = 1f
            }

            canvas?.save()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val staticLaout = StaticLayout.Builder.obtain(
                    addTextSpannableString!!,
                    0,
                    addTextSpannableString!!.length,
                    paint,
                    paint.measureText(addTextSpannableString.toString()).toInt()
                )
                layout.height
                //正式開始绘制追加的文本
                when(gravity){
                    (Gravity.TOP or Gravity.LEFT),(Gravity.TOP or Gravity.START) ->{
                        addTextRect = RectF(
                            width - paint.measureText(addTextSpannableString.toString()) - paddingRight,
                            paddingTop + layout.height  - paint.measureText(portraitStr) - ModuleUtils.dip2px(context,6f),
                            width.toFloat() - paddingRight,
                            layout.height.toFloat()+ ModuleUtils.dip2px(context,3f)
                        )
                        canvas?.translate(
                            width - paint.measureText(addTextSpannableString.toString()) - paddingRight,
                            paddingTop + layout.height  - paint.measureText(portraitStr) - ModuleUtils.dip2px(context,6f)
                        )
                        staticLaout.build().draw(canvas)
                    }
                    (Gravity.CENTER_VERTICAL or Gravity.START) -> {
                        addTextRect = RectF(
                            width - paint.measureText(addTextSpannableString.toString()) - paddingRight,
                            (height/2).toFloat() + layout.height/2 - paint.measureText(portraitStr) - ModuleUtils.dip2px(context,6f),
                            width.toFloat() - paddingRight,
                            (height/2).toFloat() + layout.height/2
                        )
                        canvas?.translate(
                            width - paint.measureText(addTextSpannableString.toString()) - paddingRight,
                            (height/2).toFloat() + layout.height/2 - paint.measureText(portraitStr) - ModuleUtils.dip2px(context,6f)
                        )
                        staticLaout.build().draw(canvas)
                    }
                }
            } else {
                val staticLayout = StaticLayout(
                    addTextSpannableString,
                    paint,
                    paint.measureText(addTextSpannableString.toString()).toInt(),
                    Layout.Alignment.ALIGN_NORMAL,
                    lineSpacingMultiplier,
                    lineSpacingExtra,
                    includeFontPadding
                )
                canvas?.translate(
                    width - paint.measureText(addTextSpannableString.toString()) - paddingRight,
                    height / 2 - (-paint.ascent() + paint.descent()) / 2
                )
                staticLayout.draw(canvas)
            }
            canvas?.restore()
        }

    }


    /**
     * 这里对追加文本做一些判断，如果原文本最后一行剩余空白空间不足以显示追加文本，则原文本做出让步，原文不末尾部分数据变成。"..."的形式
     * 如果需要保持原始文本的完整度，请不要使用追加文本，或者在设置追加文本前明确原始文本的追后一行空间可以容纳的下追加文本的长度
     * @return 是否设置"..."，当文本为空时也会返回true
     */
    private fun checkLastLineWidth(): Boolean {
        if (addToEndText == null) {
            return false
        }
        //如果文本为空，没必要测量，onDraw方法也没必要执行了。直接返回true结束onDraw方法f
        if (stringBuffer.isNullOrEmpty()) {
            return true
        }
        if (!addToEndText.isNullOrEmpty()) {
            var tempWidth = 0f
            val sp = stringBuffer?.toSpanned()
            val spans = sp!!.getSpans(0, stringBuffer!!.length, Any::class.java)

            for (item in stringBuffer!!.indices) {
                if (tempWidth > (width) - paddingLeft - paddingRight) {
                    tempWidth = width - tempWidth
                }
                if (item < spans.size) {
                    //这两个span会改变字体大小，这里做一下特殊处理
                    when {
                        spans[item] is RelativeSizeSpan -> {
                            tempWidth += paint.measureText(stringBuffer!![item].toString()) * (spans[item] as RelativeSizeSpan).sizeChange
                        }
                        spans[item] is AbsoluteSizeSpan -> {
                            tempWidth += ModuleUtils.dip2px(
                                context, (spans[item] as AbsoluteSizeSpan).size.toFloat()
                            )
                        }
                        else -> {
                            tempWidth += paint.measureText(stringBuffer!![item].toString())
                        }
                    }
                } else {
                    tempWidth += paint.measureText(stringBuffer!![item].toString())
                }
            }
            val tempTestLength = paint.measureText(addToEndText)
            //如果最终的剩余宽度足够添加末尾文字，则添加末尾文字
            when {
                tempWidth + tempTestLength < width - paddingLeft - paddingRight -> {
                    return false
                }
                tempWidth + tempTestLength == width.toFloat() - paddingLeft - paddingRight -> {
                    return false
                }
                else -> {
                    //原本的字符长度太长，无法进行插入，则直接开启下一行进行插入
                    stringBuffer = SpannableStringBuilder(
                        stringBuffer!!.subSequence(
                            0,
                            (stringBuffer!!.length - addToEndText!!.length * 1.5).toInt()
                        )
                    )
                    stringBuffer?.append("...")
                    text = stringBuffer
                    return true
                }
            }
        }
        return false
    }

    /**
     * 绘制竖排文字
     * 如果不设置最大行数,则最大行数就是int的最大值
     */
    private fun drawPortraintText(canvas: Canvas?) {
        canvas?.let {
            var startX = paddingLeft
            var endX = it.width - paddingRight
            var startY = paddingTop
            val stopY = it.height - paddingBottom
            //行距最小距离为1，不允许小于1
            var lineScale = lineSpacingMultiplier
            var wordScale = wordSpacingMultiplier
            if (lineScale < 1) {
                lineScale = 1f
            }
            if (wordScale < 1) {
                wordScale = 1f
            }
            //文字宽度
            val charWidth = paint.measureText(portraitStr)
            //文字高度
            val charHeight = (-paint.ascent() + paint.descent())
            //计算行距
            val rowSpace = abs(lineScale - 1.0f) * charHeight
            val colSpace = abs(wordScale - 1.0f) * charWidth
            //如果不设置最大值,通过计算画布给出一个最大列数
            if (maxLines == Int.MAX_VALUE) {
                maxLines = (height / (charHeight + rowSpace)).toInt()
            }
            //可现实最大列数
            val maxRow =
                (((width - paddingLeft - paddingRight) / (charWidth + colSpace)) + 0.99).toInt()
            //当前文字的列数
            val currentTextRow = maxRow.coerceAtMost((text.toString().length / (maxLines)))
            //计算文字总体绘制的起始点
            if ((superTextGravity == SuperTextConfig.Gravity.CENTER_START || superTextGravity == SuperTextConfig.Gravity.CENTER_END)) {
                val tempStartY =
                    height / 2.0f - text.length.coerceAtMost(maxLines) / 2.0f * (charHeight + rowSpace) + rowSpace / 3
                if (tempStartY > startY) {
                    startY = tempStartY.toInt()
                }

                val tempStartX =
                    width / 2.0f - (currentTextRow / 2) * (charWidth + colSpace) - charWidth / 1.3
                if (tempStartX > startX) {
                    startX = tempStartX.toInt()
                }
                val tempEndX = width - (maxRow / 2 - currentTextRow / 2) * (charWidth + colSpace)
                if (tempEndX < endX) {
                    endX = tempEndX.toInt()
                }
            }

            drawSepcialText(
                canvas,
                charWidth,
                charHeight,
                maxRow,
                currentTextRow,
                rowSpace,
                colSpace,
                startX,
                endX,
                startY
            )
            if (true) {
                return
            }
        }

    }

    private fun drawSepcialText(
        canvas: Canvas,
        charWidth: Float,
        charHeight: Float,
        maxRow: Int,
        currentTextRow: Int,
        rowSpace: Float,
        colSpace: Float,
        startX: Int,
        endX: Int,
        startY: Int
    ) {
        val strArray = text.split("\n")
        when (superTextGravity) {
            SuperTextConfig.Gravity.LEFT -> {
                for (j in strArray.withIndex()) {
                    if (j.index > maxRow) {
                        break
                    }
                    canvas.save()
                    canvas.translate(j.index * (charWidth + colSpace), 0f)
                    drawPorText(canvas,j.value)
                    canvas.restore()
                }
            }
            SuperTextConfig.Gravity.RIGHT -> {
                for (j in strArray.withIndex()) {
                    if (j.index > maxRow) {
                        break
                    }
                    canvas.save()
                    canvas.translate(
                        width - j.index * (charWidth + colSpace) - (charWidth + colSpace),
                        0f
                    )
                    drawPorText(canvas,j.value)
                    canvas.restore()
                }
            }
            SuperTextConfig.Gravity.CENTER_START -> {
                //如果是最大列数大于或等于当前列数,只需要和普通的start方法一样绘制就行
                for (j in strArray.withIndex()) {
                    if (j.index > maxRow) {
                        break
                    }
                    canvas.save()
                    canvas.translate(j.index * (charWidth + colSpace) + startX, startY.toFloat())
                    drawPorText(canvas,j.value)
                    canvas.restore()
                }
            }
            SuperTextConfig.Gravity.CENTER_END -> {
                //如果是最大列数大于或等于当前列数,只需要和普通的start方法一样绘制就行
                for (j in strArray.withIndex()) {
                    if (j.index > maxRow) {
                        break
                    }
                    canvas.save()
                    canvas.translate(endX - j.index * (charWidth + colSpace), startY.toFloat())
                    drawPorText(canvas,j.value)
                    canvas.restore()
                }
            }
        }
    }

    private fun drawPorText(canvas: Canvas?,value:CharSequence){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val build = DynamicLayout.Builder.obtain(value,paint, paint.measureText(portraitStr).toInt())
                .setIncludePad(false)
                .setDisplayText(value)
                .setLineSpacing(1f,1f)
                .build()
            build.draw(canvas)
            Log.i("日志","大于p")
        } else {
            val d = DynamicLayout(
                value!!,
                paint,
                value!!.length,
                Layout.Alignment.ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                false
            )
            d.draw(canvas)
            Log.i("日志","小于p")
        }
    }


    /**
     * 设设置文本追加的点击事件
     */
    fun setAddTextClickListener(addTextClickListener: SuperTextAddTextClickListener): SuperTextView {
        this.addTextClickListener = addTextClickListener
        return this
    }


    /**
     * 设置匹配字符
     */
    fun setMathStr(matchStr: String): SuperTextView {
        this.matchStr = matchStr
        return this
    }

    /**
     * 渐变色
     * @param orientation
     * @param colors
     * @return
     */
    private fun addGradientBgDrawable(
        orientation: GradientDrawable.Orientation?,
        colors: IntArray?
    ): GradientDrawable? {
        val drawable = GradientDrawable()
        drawable.orientation = orientation //定义渐变的方向
        drawable.colors = colors //colors为int[]，支持2个以上的颜色
        return drawable
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (superTextEnablePortrait) {
            val selfHeight = reSize(0, heightMeasureSpec, true)
            val selfWidth = reSize(0, widthMeasureSpec, false)
            setMeasuredDimension(selfWidth, selfHeight)
        } else {
            if (text.toString().isEmpty()) {
                super.onMeasure(0, 0)
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
        }
    }



    /**
     * 设置宽和高
     */
    private fun reSize(size: Int, measureSpec: Int, isHeight: Boolean): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        var lineScale = lineSpacingMultiplier
        var wordScale = wordSpacingMultiplier
        if (lineScale <= 1) {
            lineScale = 1f
        }
        if (wordScale <= 1) {
            wordScale = 1f
        }
        if (maxLines <= 1) {
            maxLines = 1
        }
        val rowSpace = Math.abs(lineScale - 1.0f) * paint.measureText(portraitStr) * maxLines
        val colSpace =
            abs(wordScale - 1.0f) * paint.measureText(portraitStr) * (text.toString().length / maxLines)
        var rowNum = 0
        rowNum = if (text.toString().length % maxLines == 0) {
            text.toString().length / maxLines
        } else {
            text.toString().length / maxLines + 1
        }
        //文字宽度
        val charWidth = paint.measureText(portraitStr)
        //文字高度
        val charHeight = (-paint.ascent() + paint.descent())
        return when (specMode) {
            MeasureSpec.UNSPECIFIED -> {
                if (isHeight) {
                    val array = text.toString().split("\n")
                    var max = 0
                    for (ite in array){
                        if (ite.length > max){
                            max = ite.length
                        }
                    }
                    Log.e("日志", "AT_MOST${max}")
                    return  (paint.measureText(portraitStr)*max).toInt() + (paint.measureText(portraitStr)/4*(max-1)).toInt()
                } else {
                    (rowNum * (charWidth) + colSpace).toInt() + paddingLeft + paddingRight
                }
            }
            MeasureSpec.AT_MOST -> {
                //设定宽高原则是，总列数宽度或总行数高度与可用宽度或高度比较，哪个值小使用那个，这样可以避免文字内容溢出可用宽度或高度的情况
                if (isHeight) {
                    val array = text.toString().split("\n")
                    var max = 0
                    for (ite in array){
                        if (ite.length > max){
                            max = ite.length
                        }
                    }
                    Log.e("日志", "AT_MOST${max}")
                    return (paint.measureText(portraitStr)*max).toInt() + (paint.measureText(portraitStr)/4*(max-1)).toInt()
                } else {
                    Math.min(
                        (rowNum * (charWidth) + colSpace).toInt() + paddingLeft + paddingRight - (abs(
                            wordScale - 1.0f
                        ) * charWidth).toInt() / 2, specSize
                    )
                }

            }
            MeasureSpec.EXACTLY -> {
                /*specSize*/
                //设定宽高原则是，总列数宽度或总行数高度与可用宽度或高度比较，哪个值小使用那个，这样可以避免文字内容溢出可用宽度或高度的情况
                if (isHeight) {
                    Log.e("日志", "EXACTLY")
                    specSize
                } else {
                    rowTextWidth = specSize
                    specSize
                }
            }
            else -> {
                size
            }
        }
    }


    object SuperTextConfig {
        //竖排文字的起始、结束位置，仅对竖排文字有效
        object Gravity {
            const val LEFT = 0
            const val RIGHT = 1
            const val CENTER_START = 2
            const val CENTER_END = 3
        }

        /**
         * 特殊样式类型
         */
        object Style {
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