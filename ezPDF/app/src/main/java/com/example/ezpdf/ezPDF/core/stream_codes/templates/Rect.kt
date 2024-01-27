package com.example.ezpdf.ezPDF.core.stream_codes.templates

abstract class Rect(
    override var x: Int,
    override var y: Int,
    val width: Int,
    val height: Int
): StreamCode {
    override fun toString(): String {
        return "$x $y $width $height re"
    }
}