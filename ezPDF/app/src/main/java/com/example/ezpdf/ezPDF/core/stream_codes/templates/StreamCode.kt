package com.example.ezpdf.ezPDF.core.stream_codes.templates

import android.util.Log


interface StreamCode {
    var x: Int
    var y: Int
    val color: Color
    val strokeWidth: Int?
    fun transpose(width: Int, height: Int) {
        Log.d("fig_transpose", y.toString())
        y = height - y
        Log.d("fig_transpose", y.toString() + "\n")
    }
}