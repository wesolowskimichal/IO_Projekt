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
import androidx.core.graphics.scale
import com.example.ezpdf.ezPDF.figures.Figure
import com.example.ezpdf.ezPDF.figures.Rectangle
import com.example.ezpdf.ezPDF.figures.Circle
import com.example.ezpdf.ezPDF.figures.Line
import com.example.ezpdf.ezPDF.figures.Text
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    enum class DrawType {
        DRAW, LINE, CIRCLE, RECTANGLE, EDIT, IMAGE, TEXT
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
    lateinit var image: Bitmap
    var strokeSize = 2f
    var myTextSize = 50f
    var drawColor = Color.Black
    var imageScale = 100
    var textToDraw = ""
    private var tmpTexToDraw = ""
    private var _pathType = PathType.FOLLOW

    private val _figures = mutableListOf<Figure>()
    private var _figIdx = -1
    private var _figureEditLevel = false

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
    private var _textPaint: Paint = Paint().apply {
        color = drawColor.toArgb()
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        textSize = myTextSize
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
        _textPaint= Paint().apply {
            color = drawColor.toArgb()
            isAntiAlias = true
            isDither = true
            style = Paint.Style.FILL
            strokeWidth = 3f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            textSize = myTextSize
        }
    }
    private var _path = Path()
    private var _motionX = 0f
    private var _motionY = 0f
    private var _currentX = 0f
    private var _currentY = 0f

    fun clear() {
        _figIdx = -1
        _figures.clear()
        if(::_bitmap.isInitialized) _bitmap.recycle()
        _bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        _canvas = Canvas(_bitmap)
        _canvas.drawColor(_bgcColor.toArgb())
        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(::_bitmap.isInitialized) _bitmap.recycle()
        _bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        _canvas = Canvas(_bitmap)
        _canvas.drawColor(_bgcColor.toArgb())
    }

    private fun render(canvas: Canvas) {
        canvas.drawBitmap(_bitmap, 0f, 0f, null)
        for(figure in _figures) {
            figure.render(_canvas)
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        render(canvas)
        when(drawType) {
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
            DrawType.IMAGE -> {

                var newImg = image.copy(Bitmap.Config.ARGB_8888, true)
                newImg = newImg.scale(imageScale, (imageScale * image.height) / image.width)
                canvas.drawBitmap(newImg, _motionX, _motionY, _paint)

            }
            DrawType.TEXT -> {
//                _textPaint.textSize = textSize
                canvas.drawText(textToDraw, _motionX, _motionY, _textPaint)


            }
            else -> {}
        }

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
        when (drawType) {
            DrawType.LINE -> {
                _figures.add(Line(start = Offset(_currentX, _currentY), end= Offset(_motionX, _motionY), _paint))
            }
            DrawType.CIRCLE -> {
                val r: Double = sqrt(
                    (_currentX - _motionX).toDouble().pow(2.0) + (_currentY - _motionY).toDouble()
                        .pow(2.0)
                )
                _figures.add(Circle(start = Offset(_currentX, _currentY), r.toFloat(), _paint))
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
                _figures.add(Rectangle(RectF(left, top, right, down), _paint))
            }
            DrawType.TEXT->{
                tmpTexToDraw = textToDraw
            }
            DrawType.IMAGE->{

            }
            else -> {}
        }
        _figureEditLevel = false
        _path.reset()
    }
    fun applyText(){

        _figures.add(Text(tmpTexToDraw, Offset(_motionX,_motionY), _textPaint))
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
            if(drawType == DrawType.DRAW) {
                _canvas.drawPath(_path, _paint)
            }
            if(_figIdx != -1) {
                if(_figureEditLevel) {
                    _figures[_figIdx].resize(_motionX, _motionY)
                } else {
                    _figures[_figIdx].moveTo(_motionX, _motionY)
                }
            }
        }
        invalidate()
    }

    private fun touchStart() {
        _path.reset()
        _path.moveTo(_motionX, _motionY)
        _currentX = _motionX
        _currentY = _motionY
        if(drawType == DrawType.EDIT) {
            for (i in _figures.size - 1 downTo 0) {
                val figure = _figures[i]
                if(figure.checkBounds(_motionX, _motionY)) {
                    _figureEditLevel = _figIdx == i
                    _figIdx = i
                    break
                }
            }
        } else {
            _figIdx = -1
            _figureEditLevel = false
        }
    }


}