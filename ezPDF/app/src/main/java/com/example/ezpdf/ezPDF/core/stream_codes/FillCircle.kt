package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.Circle

class FillCircle(
    x: Int,
    y: Int,
    radius: Float,
    borderWidth: Int,
    borderColor: LineColor,
    val fillColor: FillColor
): Circle(x, y, radius, borderWidth) {
    override val color = borderColor

    override fun toString(): String {
        val circle = super.toString()
        return "$color$fillColor" +
                circle +
                "B\n"
    }
}