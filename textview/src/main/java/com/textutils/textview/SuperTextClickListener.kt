package com.textutils.textview

interface SuperTextClickListener {
    fun onClick(startPosition: Int, endPosition: Int, text: String)
}