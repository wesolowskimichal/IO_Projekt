package com.example.ezpdf.ezPDF.figures

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class Line(private var start: Offset, private var end: Offset, override var paint: Paint): Figure {
    private var _onHover = false
    override val onHover: Boolean
        get() = _onHover
    override fun render(canvas: Canvas) {
        canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }

    override fun moveTo(newX: Float, newY: Float) {
        val diffX =  end.x - start.x
        val diffY = end.y - start.y
        start = Offset(newX, newY)
        end = Offset(newX + diffX, newY + diffY)
    }

    override fun resize(newX: Float, newY: Float) {
        end = Offset(newX, newY)
    }

    override fun checkBounds(posX: Float, posY: Float): Boolean {
        val a = start.x
        val b = start.y
        val c = end.x
        val d = end.y
        if (c - a != 0.0f) {
            val slope = (d - b) / (c - a)
            val yOnLine = slope * (posX - a) + b
            if (posY == yOnLine && posX in minOf(a, c)..maxOf(a, c) && posY in minOf(b, d)..maxOf(b, d)) {
                _onHover = true
                return true
            }
        } else if (posX == a && posY in minOf(b, d)..maxOf(b, d)) {
            _onHover = true
            return true
        }
        _onHover = false
        return false

        _onHover = start.x <= posX && posX <= end.x && start.y <= posY && posY <= end.y
        return _onHover
    }
}