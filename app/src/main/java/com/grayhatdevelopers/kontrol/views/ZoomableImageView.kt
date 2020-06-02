package com.grayhatdevelopers.kontrol.views

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.min

/**
 * A zoomable image view widget
 * DISCLAIMER: instead of writing down my own pinching functionality,
 * I just borrowed some code from Michael Ortiz Github repo, and rewrote it in Kotlin after some minor modifications.
 * Created by Hamza Maqsood
 */

class ZoomableImageView(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageView(context, attrs) {

    private lateinit var mScaleGestureDetector: ScaleGestureDetector
    private val mMatrix: Matrix = Matrix()

    private var mode: States = States.NONE
    private val start = PointF()
    private val last = PointF()
    private val floatsArray = FloatArray(size = 9)
    private var viewWidth = 0
    private var viewHeight = 0
    private var originalWidth = 0.toFloat()
    private var originalHeight = 0.toFloat()
    private var oldMeasuredWidth = 0
    private var oldMeasuredHeight  = 0
    private var click = 3
    private var saveScale = 1.toFloat()

    init {
        sharedConstructing()
    }

    private fun sharedConstructing() {
        super.setClickable(true)
        mScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        imageMatrix = mMatrix
        scaleType = ScaleType.MATRIX

        setOnTouchListener { _, event ->

            mScaleGestureDetector.onTouchEvent(event)
            val currentPoint = PointF(event.x, event.y)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "MotionEvent: ACTION_DOWN")
                    last.set(currentPoint)
                    start.set(last)
                    mode = States.DRAG
                }

                MotionEvent.ACTION_MOVE -> {
                    Log.d(TAG, "MotionEvent: ACTION_MOVE")
                    if (mode == States.DRAG) {
                        Log.d(TAG, "In Dragging mode")
                        val deltaX = currentPoint.x - last.x
                        val deltaY = currentPoint.y - last.y
                        val fixTransX = getFixDragTrans(deltaX, viewWidth.toFloat(), originalWidth * saveScale)
                        val fixTransY = getFixDragTrans(deltaY, viewHeight.toFloat(), originalHeight * saveScale)
                        mMatrix.postTranslate(fixTransX, fixTransY)
                        fixTrans()
                        last.set(currentPoint.x, currentPoint.y)
                    }
                }

                MotionEvent.ACTION_UP -> {
                    Log.d(TAG, "MotionEvent: ACTION_UP")
                    mode = States.NONE
                    val xDiff: Int = abs(currentPoint.x - start.x).toInt()
                    val yDiff: Int = abs(currentPoint.y - start.y).toInt()

                    if (xDiff < click && yDiff < click)
                        performClick()
                }

                MotionEvent.ACTION_POINTER_UP -> {
                    Log.d(TAG, "MotionEvent: ACTION_POINTER_UP")
                    mode = States.NONE
                }
            }

            imageMatrix = mMatrix

            // will force the image view to redraw itself
            invalidate()

            // TRUE indicates that the event was handled and
            // there's no need to pass the event to the super class
            return@setOnTouchListener true
        }
    }

    private fun fixTrans() {
        mMatrix.getValues(floatsArray)
        val transX = floatsArray[Matrix.MTRANS_X]
        val transY = floatsArray[Matrix.MTRANS_Y]

        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), originalWidth * saveScale)
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), originalHeight * saveScale)

        if (fixTransX != 0.toFloat() || fixTransY != 0.toFloat())
            mMatrix.postTranslate(fixTransX, fixTransY)
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float) : Float {
        val minTrans: Float
        val maxTrans: Float

        if (contentSize <= viewSize) {
            minTrans = 0.toFloat()
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0.toFloat()
        }

        return when {
            trans < minTrans -> -trans + minTrans
            trans > maxTrans -> -trans + maxTrans
            else -> 0.toFloat()
        }
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float) : Float =
        if (contentSize <= viewSize) 0.toFloat() else delta
//        if (contentSize <= viewSize) delta else 0.toFloat()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        // rescale image on rotation
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight || viewWidth == 0 || viewHeight == 0)
            return

        oldMeasuredHeight = viewHeight
        oldMeasuredWidth = viewWidth

        if (saveScale == 1.toFloat()) {
            // Fit to screen
            val scale: Float

            val drawable = drawable
            if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0)
                return

            val bmHeight = drawable.intrinsicHeight
            val bmWidth = drawable.intrinsicWidth

            val scaleX = viewWidth.toFloat() / bmWidth.toFloat()
            val scaleY = viewHeight.toFloat() / bmHeight.toFloat()

            scale = min(scaleX, scaleY)
            mMatrix.setScale(scale, scale)

            // center the image
            val redundantYSpace = (viewHeight - (scale * bmHeight.toFloat())) / 2.toFloat()
            val redundantXSpace = (viewWidth - (scale * bmWidth.toFloat())) / 2.toFloat()

            mMatrix.postTranslate(redundantXSpace, redundantYSpace)
            originalHeight = viewWidth - 2 * redundantXSpace
            originalHeight = viewHeight - 2 * redundantYSpace
            imageMatrix = mMatrix
        }

        fixTrans()
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            mode = States.ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            var scaleFactor = detector?.scaleFactor!!
            val originalScale = saveScale
            saveScale *= scaleFactor

            if (saveScale > MAX_SCALE) {
                saveScale = MAX_SCALE
                scaleFactor = MAX_SCALE / originalScale
            } else if (saveScale < MIN_SCALE) {
                saveScale = MIN_SCALE
                scaleFactor = MIN_SCALE / originalScale
            }

            if (originalWidth * saveScale <= viewWidth || originalHeight * saveScale <= viewHeight) {
                mMatrix.postScale(scaleFactor, scaleFactor, (viewHeight / 2f), (viewWidth / 2f))
            } else {
                mMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            }

            fixTrans()
            return true
        }
    }

    companion object {

        private const val TAG = "ZoomableImageView"

        private const val MIN_SCALE = 1.toFloat()
        private const val MAX_SCALE = 3.toFloat()
    }
}

enum class States {
    NONE, DRAG, ZOOM
}