package com.example.ezpdf.ezPDF

import android.graphics.Paint

interface Figure {
    var Color: Int
    var IsAntiAlias: Boolean
    var IsDither: Boolean
    var Style: Paint.Style
    var StrokeWidth: Float
    var StrokeCap: Paint.Cap
    var StrokeJoin: Paint.Join

    fun CreatePaint(): Paint;
}