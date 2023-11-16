package com.example.ezpdf.ezPDF.figures

import android.graphics.Canvas
import android.graphics.Paint

interface Figure {
    var paint: Paint
    val onHover: Boolean
    fun render(canvas: Canvas)
    fun moveTo(newX: Float, newY: Float)
    fun resize(newX: Float, newY: Float)
    fun checkBounds(posX: Float, posY: Float) : Boolean
}