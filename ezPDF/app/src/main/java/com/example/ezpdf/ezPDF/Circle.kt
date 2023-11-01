package com.example.ezpdf.ezPDF
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset

/*
class Circle(val start: Offset, val radius: Float) : Figure {
    override var Color: Int = android.graphics.Color.BLACK
    override var IsAntiAlias: Boolean = true
    override var IsDither: Boolean = true
    override var Style: Paint.Style = Paint.Style.STROKE
    override var StrokeWidth: Float = 2f
    override var StrokeCap: Paint.Cap = Paint.Cap.ROUND
    override var StrokeJoin: Paint.Join = Paint.Join.ROUND

    override fun CreatePaint(): Paint {
        return Paint().apply {
            color = Color
            isAntiAlias = IsAntiAlias
            isDither = IsDither
            style = Style
            strokeWidth = StrokeWidth
            strokeCap = StrokeCap
            strokeJoin = StrokeJoin
        }
    }
}*/

class Circle(val start: Offset, val radius: Float, var paint: Paint) {}
