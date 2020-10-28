package com.example.drawapp.uimodel

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import com.example.drawapp.DrawViewHolder
import com.example.drawapp.model.COLOR
import com.example.drawapp.model.PaintState
import kotlin.math.abs

class DrawView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val STROKE_WIDTH = 12f
    }

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var path = Path()
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    // Path representing
    private val drawing = Path() // the drawing
    private val curPath = Path() // what's currently being drawn

    // Painting Settings
    private val paint = Paint().apply {
        color = ResourcesCompat.getColor(resources, COLOR.BLACK.value, null)
        isAntiAlias = true // Smooths out edges of what is drawn without affecting shape.
        isDither = true // Dithering affects how colors with higher-precision than the device are down-sampled.
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    fun render(state: PaintState) {
        paint.color = ResourcesCompat.getColor(resources, state.color.value, null)
        paint.strokeWidth = state.size.value.toFloat()
        paint.strokeCap = state.shape.value
    }

    fun clear(){
        curPath.reset()
        drawing.reset()
        path.reset()
        extraCanvas.drawColor(Color.WHITE)
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        (context as DrawViewHolder).onClicked()
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun restartCurrentXY() {
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        restartCurrentXY()
    }

    private fun touchMove() {
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            restartCurrentXY()
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    private fun touchUp() {
        drawing.addPath(curPath)
        curPath.reset()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap).apply { drawColor(Color.WHITE) }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        canvas.drawPath(drawing, paint)
        canvas.drawPath(curPath, paint)
    }

    fun getBitmap(): Bitmap {
        return extraBitmap
        //return extraCanvas
    }
}