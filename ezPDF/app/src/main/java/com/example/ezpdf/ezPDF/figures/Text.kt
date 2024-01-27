package com.example.ezpdf.ezPDF.figures

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.ezpdf.ezPDF.core.stream_codes.FillColor
import com.example.ezpdf.ezPDF.core.stream_codes.Text
import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode

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

    override fun convertToStreamCode(): StreamCode {
        return Text(
            start.x.toInt(),
            start.y.toInt(),
            paint.textSize.toInt(),
            content,
            FillColor(paint.color.red, paint.color.green, paint.color.blue)
        )
    }

    override fun checkBounds(posX: Float, posY: Float): Boolean {
        val textWidth = paint.measureText(content)
        val textHeight = paint.textSize
        return posX >= start.x && posX <= start.x + textWidth && posY >= start.y - textHeight && posY <= start.y
    }

}
