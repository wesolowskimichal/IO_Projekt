package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.Rect

class FillBorderRect(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    borderWidth: Int,
    val borderColor: FillColor,
    fillColor: FillColor,

    ): Rect(x, y, width, height) {
    override val color = fillColor
    override val strokeWidth = borderWidth

    override fun toString(): String {
        val rect = super.toString()
        val halfBorder = strokeWidth / 2
        val border = FillRect(x-halfBorder, y-halfBorder, width+strokeWidth, height+strokeWidth, borderColor)
        return "$border$color$rect f\n"
    }
}