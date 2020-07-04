package com.textutils.textview.utils

import android.util.Log

object TextUtils {
    private var matchStrArray: ArrayList<String> = ArrayList()
    private var tempText:String = ""
    //上次调用匹配的原文本
    private var oldText:String?=null
    //上次匹配的文本
    private var oldMathStr:String?=null
    //上次匹配的类型
    private var oldMathType = -1

    fun getMatchStrArray(matchStr:String?,matchEverySameStr:Boolean,tempText:String,text:String):ArrayList<String>{
        if (oldText != null && oldText.equals(text) && matchStrArray.size != 0 && oldMathStr != null && oldMathStr.equals(matchStr) && oldMathType == MathType.MATH_WORD){
            return matchStrArray
        }
        oldMathType = MathType.MATH_WORD
        oldText = text
        oldMathStr = matchStr
        matchStrArray.clear()
        getMatchStr(matchStr,matchEverySameStr,tempText,text)
        this.tempText = ""
        return matchStrArray
    }

    /**
     * 匹配字符
     */
    private fun getMatchStr(matchStr:String?,matchEverySameStr:Boolean,tempText:String,text:String){
        //保存匹配字符的位置信息集合
        matchStr?.let {
            //如果需要将所有相同字符设置为同一个样式，则需要递归获取每个相同字符的开始位置和结束位置。
            if (matchEverySameStr) {
                if (tempText.indexOf(it) != -1) {
                    var startIndex = tempText.indexOf(it)
                    if (matchStrArray?.size != 0) {
                        startIndex += matchStrArray!!.get(matchStrArray!!.size - 1).split(",")[1].toInt()
                    }
                    val endIndex: Int = startIndex + it.length
                    //Log.e("日志","输出结果前："+startIndex+","+text.length)
                    if (endIndex > text.length) {
                        this.tempText = ""
                    } else {
                        this.tempText = text.substring(endIndex, text.length)
                    }
                    ///Log.e("日志","输出结果："+tempText+",endIndex:"+endIndex+",length:"+tempText.length)
                    matchStrArray.add("${startIndex},${endIndex}")
                    getMatchStr(matchStr, matchEverySameStr, this.tempText, text)
                } else{
                    //Log.e("日志","未匹配到")
                }
            } else {
                val startIndex = text.indexOf(it)
                val endIndex: Int = startIndex + it.length
                matchStrArray.add("${startIndex},${endIndex}")
            }
        }
    }

    /**
     * 获取网页链接的开始和借宿位置
     */
    fun getUrlArray(text:String) : ArrayList<String>{
        if (oldText != null && oldText.equals(text) && matchStrArray.size != 0 && oldMathType == MathType.MATH_URL){
            return matchStrArray
        }
        oldMathType = MathType.MATH_URL
        oldText = text
        Log.e("日志","执行匹配")
        matchStrArray.clear()
        runMatch(text)
        return matchStrArray
    }

    /**
     * 获取文本中所有超链接起始地点和结束地点
     */
    private fun runMatch(str: String?) {
        //不允许传入内容为空
        if (str == null || str.isEmpty()) {
            return
        }

        //不存在url直接退出
        val havaUrl = str.indexOf("https://") != -1 || str.indexOf("http://") != -1
        if (!havaUrl) {
            return
        }
        var index = str.indexOf("https://")
        //不存在http://,则再匹配http://
        if (index == -1) {
            index = str.indexOf("http://")
        }
        var lastIndex = -1
        for (i in 0..str.length-1) {
            if (index +i == str.length){
                lastIndex = str.length
                break
            }
            //存在中文字符直接退出
            if (str[index + i].toString().matches(Regex("[\\u4E00-\\u9FA5]+"))) {
                lastIndex = index + i
                break
            }
            if (index + i + 1 < str.length && str[index + i + 1].toString() == " ") {
                lastIndex = index + i
                break
            }
        }
        //Log.e("日志","temp的值为："+tempText)
        tempText = str.substring(lastIndex)
        if (matchStrArray!!.size != 0){
            val preStartIndex = matchStrArray!!.get(matchStrArray!!.size-1).split(",")[0].toInt()
            val preEndIndex = matchStrArray!!.get(matchStrArray!!.size-1).split(",")[1].toInt()
            matchStrArray.add("${index+preEndIndex},${lastIndex+preEndIndex}")
        }else{
            matchStrArray.add("$index,$lastIndex")
        }
        if (tempText.indexOf("https://") != -1 || tempText.indexOf("http://") != -1) {
            runMatch(tempText)
        }
    }

    object MathType{
        //匹配文字
        const val MATH_WORD = 0
        //匹配链接
        const val MATH_URL = 1
    }
}