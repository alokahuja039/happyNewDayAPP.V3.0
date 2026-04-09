package com.happyday.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

/**
 * Draws a GitHub-style contribution grid showing the last [WEEKS] weeks.
 * Columns = weeks (oldest left → newest right).
 * Rows    = days of the week (Mon top → Sun bottom).
 */
class HabitGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val WEEKS = 16
        private const val DAYS_IN_WEEK = 7
    }

    private val density = context.resources.displayMetrics.density
    private val cellSizePx = 13f * density
    private val cellGapPx = 3f * density
    private val cornerPx = 3f * density

    // GitHub colour palette
    private val paintEmpty = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFEBEDF0.toInt() }
    private val paintDone  = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF40C463.toInt() }
    private val paintFuture = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFF6F8FA.toInt() }

    private val rect = RectF()
    private var completedDates: Set<String> = emptySet()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun setCompletedDates(dates: Set<String>) {
        completedDates = dates
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = (WEEKS * (cellSizePx + cellGapPx) - cellGapPx).toInt()
        val h = (DAYS_IN_WEEK * (cellSizePx + cellGapPx) - cellGapPx).toInt()
        setMeasuredDimension(
            resolveSize(w, widthMeasureSpec),
            resolveSize(h, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val todayStr = sdf.format(Date())

        // The start date is WEEKS*7 days ago
        val startCal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -(WEEKS * DAYS_IN_WEEK - 1))
        }

        for (col in 0 until WEEKS) {
            for (row in 0 until DAYS_IN_WEEK) {
                val cal = Calendar.getInstance().apply {
                    timeInMillis = startCal.timeInMillis
                    add(Calendar.DAY_OF_YEAR, col * DAYS_IN_WEEK + row)
                }
                val dateStr = sdf.format(cal.time)

                val x = col * (cellSizePx + cellGapPx)
                val y = row * (cellSizePx + cellGapPx)
                rect.set(x, y, x + cellSizePx, y + cellSizePx)

                val paint = when {
                    dateStr > todayStr -> paintFuture
                    completedDates.contains(dateStr) -> paintDone
                    else -> paintEmpty
                }
                canvas.drawRoundRect(rect, cornerPx, cornerPx, paint)
            }
        }
    }
}
