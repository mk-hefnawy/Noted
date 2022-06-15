package com.example.noted.core.Extensions

import android.view.View
import android.view.animation.TranslateAnimation

fun View.animateVisibility(){
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f,
        0f,
        height.toFloat() - (2 * height.toFloat()),
        0f)
    animate.duration = 500
    //animate.fillAfter = true

    startAnimation(animate)

    //clearAnimation()
}

fun View.animateVisibilityFromOffsetVertically(endOfWindow: Float){
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f,
        0f,
        endOfWindow,
        endOfWindow - height.toFloat())
    animate.duration = 500
    animate.fillAfter = true

    startAnimation(animate)

    //clearAnimation()
}

fun View.animateInvisibility(){
    visibility = View.INVISIBLE
    val animate = TranslateAnimation(0f,
        0f,
        0f,
        height.toFloat() - (2 * height.toFloat()))
    animate.duration = 500
    startAnimation(animate)
}