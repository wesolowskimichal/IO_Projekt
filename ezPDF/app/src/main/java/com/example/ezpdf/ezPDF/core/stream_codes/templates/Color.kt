package com.example.ezpdf.ezPDF.core.stream_codes.templates

abstract class Color(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    override fun toString(): String {
        return "${red.toDouble()/255.0} ${green.toDouble()/255.0} ${blue.toDouble()/255.0} "
    }
}