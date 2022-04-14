package com.example.noted.core.Extensions

import android.view.View
import android.view.animation.TranslateAnimation

fun View.animateVisibility(){
    val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
    animate.duration = 500
    animate.fillAfter = true
    startAnimation(animate)
    visibility = View.GONE
}