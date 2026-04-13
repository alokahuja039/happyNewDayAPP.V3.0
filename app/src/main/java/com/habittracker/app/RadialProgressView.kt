package com.habittracker.app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class RadialProgressView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, def: Int = 0
) : View(ctx, attrs, def) {

    private var progress = 0f
    private var showText = false
    private val dp = ctx.resources.displayMetrics.density

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style      = Paint.Style.STROKE
        color      = 0xFFE5E5EA.toInt()
        strokeCap  = Paint.Cap.ROUND
    }
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style      = Paint.Style.STROKE
        strokeCap  = Paint.Cap.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign    = Paint.Align.CENTER
        color        = 0xFF1C1C1E.toInt()
        isFakeBoldText = true
    }
    private val oval = RectF()

    /** Call this to update the ring. [value] is 0.0–1.0. */
    fun setProgress(value: Float, withText: Boolean = false) {
        progress = value.coerceIn(0f, 1f)
        showText = withText
        arcPaint.color = when {
            progress >= 0.75f -> 0xFF27AE60.toInt()   // green
            progress >= 0.40f -> 0xFF2471A3.toInt()   // blue
            progress >  0f    -> 0xFFF39C12.toInt()   // amber
            else              -> 0xFFBDC3C7.toInt()   // grey
        }
        invalidate()
    }

    override fun onMeasure(ws: Int, hs: Int) {
        val s = (if (showText) 72 else 34) * dp
        setMeasuredDimension(resolveSize(s.toInt(), ws), resolveSize(s.toInt(), hs))
    }

    override fun onDraw(canvas: Canvas) {
        val sw = (if (showText) 7f else 5f) * dp
        trackPaint.strokeWidth = sw
        arcPaint.strokeWidth   = sw

        val cx = width  / 2f
        val cy = height / 2f
        val r  = minOf(cx, cy) - sw / 2f - 1f

        oval.set(cx - r, cy - r, cx + r, cy + r)
        canvas.drawArc(oval, -90f, 360f, false, trackPaint)
        if (progress > 0f)
            canvas.drawArc(oval, -90f, progress * 360f, false, arcPaint)

        if (showText) {
            textPaint.textSize = 13f * dp
            canvas.drawText(
                "${(progress * 100).toInt()}%",
                cx, cy + textPaint.textSize * 0.38f, textPaint
            )
        }
    }
}
