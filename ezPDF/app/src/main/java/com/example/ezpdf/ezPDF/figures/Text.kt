package com.example.ezpdf.ezPDF.figures

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset

class Text(private var content:String, private var start: Offset, override var paint: Paint):Figure{
    private var _onHover = false
    override val onHover: Boolean
        get() = _onHover
    override fun render(canvas: Canvas) {
        canvas.drawText(content, start.x, start.y, paint)
    }

    override fun moveTo(newX: Float, newY: Float) {
        start = Offset(newX, newY)
    }

    override fun resize(newX: Float, newY: Float) {
        paint.textSize = Math.max(newX, newY)
    }

    override fun checkBounds(posX: Float, posY: Float): Boolean {
        val textWidth = paint.measureText(content)
        val textHeight = paint.textSize
        return posX >= start.x && posX <= start.x + textWidth && posY >= start.y - textHeight && posY <= start.y
    }

}
