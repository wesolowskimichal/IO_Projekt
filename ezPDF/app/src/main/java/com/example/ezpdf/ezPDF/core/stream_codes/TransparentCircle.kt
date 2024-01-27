package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.Circle

class TransparentCircle(
    x: Int,
    y: Int,
    radius: Float,
    borderWidth: Int,
    borderColor: LineColor
): Circle(x, y, radius, borderWidth) {
    override val color = borderColor

    override fun toString(): String {
        val circle = super.toString()
        return color.toString() +
                circle +
                "S\n"
    }
}