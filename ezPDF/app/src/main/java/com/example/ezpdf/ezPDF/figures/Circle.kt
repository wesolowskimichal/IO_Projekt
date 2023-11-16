package com.example.ezpdf.ezPDF.figures
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.pow
import kotlin.math.sqrt

class Circle(private var start: Offset, private var radius: Float, override var paint: Paint): Figure {

    private var _onHover = false
    override val onHover: Boolean
        get() = _onHover

    override fun render(canvas: Canvas) {
        canvas.drawCircle(start.x, start.y, radius, paint)
    }

    override fun moveTo(newX: Float, newY: Float) {
        start = Offset(newX, newY)
    }

    override fun resize(newX: Float, newY: Float) {
        radius = sqrt(
            (start.x - newX).toDouble().pow(2.0) + (start.y - newY).toDouble()
                .pow(2.0)
        ).toFloat()
    }

    override fun checkBounds(posX: Float, posY: Float): Boolean {
        val distance = sqrt((posX - start.x).pow(2) + (posY - start.y).pow(2))
        _onHover = distance <= radius
        return _onHover
    }
}
