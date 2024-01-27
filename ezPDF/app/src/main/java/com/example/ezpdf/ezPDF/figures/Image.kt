package com.example.ezpdf.ezPDF.figures

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.core.graphics.scale
import com.example.ezpdf.ezPDF.CanvasView
import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode
import kotlin.math.abs

class Image(
    var image: Bitmap,
    var position: Offset,
    var scale: Float,
    override var paint: Paint
) : Figure {

    override val onHover: Boolean = false

    override fun render(canvas: Canvas) {
        val scaledImage = image.scale((image.width * scale).toInt(), (image.height * scale).toInt())
        canvas.drawBitmap(scaledImage, position.x, position.y, paint)
    }

    override fun moveTo(newX: Float, newY: Float) {
        position = Offset(newX, newY)
    }

    override fun resize(newX: Float, newY: Float) {
        val deltaX = newX - position.x
        val deltaY = newY - position.y
        scale = abs(deltaX / image.width.coerceAtLeast(1))
    }

    override fun convertToStreamCode(): StreamCode {
        TODO("Not yet implemented")
    }

    override fun checkBounds(posX: Float, posY: Float): Boolean {
        return posX >= position.x && posX <= position.x + image.width * scale &&
                posY >= position.y && posY <= position.y + image.height * scale
    }
}