package com.example.ezpdf.ezPDF

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.lang.Math.pow
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    enum class DrawType {
        DRAW, LINE, CIRCLE, RECTANGLE
    }

    private enum class PathType {
        FOLLOW, ABORT
    }

    private val _bgcColor: Color = Color.White
    private lateinit var _canvas: Canvas
    private lateinit var _bitmap: Bitmap

    var drawType = DrawType.DRAW
        set(value) {
            _pathType = if(value == DrawType.DRAW) {
                    PathType.FOLLOW
                } else {
                    PathType.ABORT
                }
            field = value
        }
    var strokeSize = 2f
    var drawColor = Color.Black
    private var _pathType = PathType.FOLLOW

    private val _lines = mutableListOf<Line>()
    private val _circles = mutableListOf<Circle>()

    private val _touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    private var _paint: Paint = Paint().apply {
        color = drawColor.toArgb()
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = strokeSize
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

//  !!! Obstawiam że tak się nie powinno robić, ale nie wiem jak to poprawnie zrobić :P
    fun updatePaint(){
        _paint = Paint().apply {
            color = drawColor.toArgb()
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = strokeSize
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
    }
    private var _path = Path()
    private var _motionX = 0f
    private var _motionY = 0f
    private var _currentX = 0f
    private var _currentY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(::_bitmap.isInitialized) _bitmap.recycle()
        _bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        _canvas = Canvas(_bitmap)
        _canvas.drawColor(_bgcColor.toArgb())
    }

    private fun render(canvas: Canvas) {
        // maybe paint is also needed
        for(line in _lines) {
            canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, line.paint)
        }

        for(circle in _circles) {
            canvas.drawCircle(circle.start.x, circle.start.y, circle.radius, circle.paint)
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when(drawType) {
            DrawType.DRAW -> {
                canvas.drawBitmap(_bitmap, 0f, 0f, null)
            }
            DrawType.LINE -> {
                canvas.drawLine(_currentX, _currentY, _motionX, _motionY, _paint)
            }
            DrawType.CIRCLE -> {
                val r: Double = sqrt(
                    (_currentX - _motionX).toDouble().pow(2.0) + (_currentY - _motionY).toDouble()
                    .pow(2.0)
                )
                canvas.drawCircle(_currentX, _currentY, r.toFloat(), _paint)
            }
            DrawType.RECTANGLE -> {
                val diffX = abs(_currentX - _motionX)
                val diffY = abs(_motionY - _currentY)
                var left = _currentX
                var top = _currentY
                var right = _currentX + diffX
                var down = _currentY + diffY

                if(_motionY < _currentY) {
                    top = _motionY
                    down = _motionY + diffY
                }

                if(_motionX < _currentX) {
                    left = _motionX
                    right = _motionX + diffX
                }


                canvas.drawRect(
                    RectF(
                        left,
                        top,
                        right,
                        down
                    ),
                    _paint)
            }
        }
        render(canvas)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        _motionX = event!!.x
        _motionY = event!!.y
        when(event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchEnd()
        }
        return true
    }

    private fun touchEnd() {
        if(drawType == DrawType.LINE) {
            _lines.add(Line(start = Offset(_currentX, _currentY), end= Offset(_motionX, _motionY), _paint))
        }
        else if(drawType == DrawType.CIRCLE) {
            val r: Double = sqrt(
                (_currentX - _motionX).toDouble().pow(2.0) + (_currentY - _motionY).toDouble()
                    .pow(2.0)
            )
            _circles.add(Circle(start = Offset(_currentX, _currentY), r.toFloat(), _paint))
        }
         _path.reset()
    }

    private fun touchMove() {
        val dx = abs(_motionX - _currentX)
        val dy = abs(_motionY - _currentY)
        if(dx >= _touchTolerance || dy >= _touchTolerance) {
            _path.quadTo(
                _currentX,
                _currentY,
                (_motionX + _currentX) / 2,
                (_motionY + _currentY) / 2
            )
            if(_pathType == PathType.FOLLOW) {

                _currentX = _motionX
                _currentY = _motionY
            }
            _canvas.drawPath(_path, _paint)
        }
        invalidate()
    }

    private fun touchStart() {
        _path.reset()
        _path.moveTo(_motionX, _motionY)
        _currentX = _motionX
        _currentY = _motionY
    }
}