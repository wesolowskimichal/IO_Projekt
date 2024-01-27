package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode

class Line(
    override var x: Int,
    override var y: Int,
    val endX: Int,
    val endY: Int,
    fillColor: LineColor,
    override val strokeWidth: Int,
    val rounded: Boolean = false
): StreamCode {
    override val color = fillColor

    override fun toString(): String {
        val r = if (rounded) "1 J\n" else ""
        return  "$color" +
                "$strokeWidth w\n$r" +
                "$x $y m $endX $endY l S\n"
    }
}