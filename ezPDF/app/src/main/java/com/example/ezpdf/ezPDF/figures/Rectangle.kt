package com.example.ezpdf.ezPDF.figures

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs

class Rectangle(private var _coordinates: RectF, override var paint: Paint): Figure {
    private var _onHover = false
    override val onHover: Boolean
        get() = _onHover

    override fun render(canvas: Canvas) {
        canvas.drawRect(_coordinates, paint)
    }

    override fun moveTo(newX: Float, newY: Float) {
        _coordinates.offsetTo(newX, newY)
    }

    override fun resize(newX: Float, newY: Float) {
        val diffX = abs(_coordinates.left - newX)
        val diffY = abs(newY - _coordinates.top)
        var left = _coordinates.left
        var top = _coordinates.top
        var right = newX
        var down = newY
        if(down < top) {
            down = top.also { top = down }
        }
        if(right < left) {
            right = left.also { left = right}
        }
        _coordinates = RectF(left, top, right, down)
    }

    override fun checkBounds(posX: Float, posY: Float): Boolean {
        _onHover = _coordinates.contains(posX, posY)
        return _onHover
    }
}