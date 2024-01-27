package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.Rect

class TransparentRect(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    borderWidth: Int,
    borderColor: LineColor
): Rect(x, y, width, height) {
    override val color = borderColor
    override val strokeWidth = borderWidth

    override fun toString(): String {
        val rect = super.toString()
        return "$color$rect S\n"
    }
}