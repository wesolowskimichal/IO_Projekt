package com.example.ezpdf.ezPDF.figures

import android.graphics.Canvas
import android.graphics.Paint
import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode

interface Figure {
    var paint: Paint
    val onHover: Boolean
    fun render(canvas: Canvas)
    fun moveTo(newX: Float, newY: Float)
    fun resize(newX: Float, newY: Float)
    fun convertToStreamCode(): StreamCode
    fun checkBounds(posX: Float, posY: Float) : Boolean
}