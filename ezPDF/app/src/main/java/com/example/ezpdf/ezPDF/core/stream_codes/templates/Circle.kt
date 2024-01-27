package com.example.ezpdf.ezPDF.core.stream_codes.templates

abstract class Circle(
    override var x: Int,
    override var y: Int,
    val radius: Float,
    borderWidth: Int
): StreamCode {
    override val strokeWidth = borderWidth
    override fun toString(): String {
        val bezier = radius*0.552
        return  "$strokeWidth w\n" +
                "${x+radius} $y m\n" +
                "${x+radius} ${y+bezier} ${x+bezier} ${y+radius} $x ${y+radius} c\n" +
                "${x-bezier} ${y+radius} ${x-radius} ${y+bezier} ${x-radius} $y c\n" +
                "${x-radius} ${y-bezier} ${x-bezier} ${y-radius} $x ${y-radius} c\n" +
                "${x+bezier} ${y-radius} ${x+radius} ${y-bezier} ${x+radius} $y c\n"
    }
}