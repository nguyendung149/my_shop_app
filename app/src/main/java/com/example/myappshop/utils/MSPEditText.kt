package com.example.myappshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MSPEditText(
    context:Context,
    attrs:AttributeSet
):AppCompatEditText(context,attrs) {
    init {

    }
    private fun applyFont(){
        val typeface: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        setTypeface(typeface)
    }
}