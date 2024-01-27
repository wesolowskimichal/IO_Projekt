package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.Rect

class FillRect(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    fillColor: FillColor,
): Rect(x, y, width, height) {
    override val color = fillColor
    override val strokeWidth = null

    override fun toString(): String {
        val rect = super.toString()
        return "$color$rect f\n"
    }
}