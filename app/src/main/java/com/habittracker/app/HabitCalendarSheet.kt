package com.habittracker.app

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class HabitCalendarSheet : BottomSheetDialogFragment() {

    companion object {
        fun forId(id: String) = HabitCalendarSheet().apply {
            arguments = Bundle().apply { putString("id", id) }
        }
    }

    override fun getTheme() = R.style.BottomSheetTheme

    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.sheet_habit_calendar, container, false)

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        val id    = arguments?.getString("id") ?: return
        val dm    = DataManager(requireContext())
        val habit = dm.loadHabits().find { it.id == id } ?: return

        val sdf   = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val disp  = SimpleDateFormat("MM/dd/yy",   Locale.getDefault())
        val wkFmt = SimpleDateFormat("MMM d",       Locale.getDefault())

        v.findViewById<TextView>(R.id.calName).text = habit.name

        val logged   = habit.completedDates.values.count { it >= habit.goalCount }
        val startStr = if (habit.startDate.isNotEmpty())
            try { disp.format(sdf.parse(habit.startDate)!!) } catch (e: Exception) { "—" }
        else "—"
        v.findViewById<TextView>(R.id.calStats).text =
            "$logged days logged  •  started $startStr"

        v.findViewById<ImageButton>(R.id.calClose).setOnClickListener { dismiss() }

        buildGrid(v.findViewById(R.id.calGrid), habit, sdf, wkFmt)
    }

    private fun buildGrid(
        container: LinearLayout,
        habit:     Habit,
        sdf:       SimpleDateFormat,
        wkFmt:     SimpleDateFormat
    ) {
        val ctx   = requireContext()
        val dp    = ctx.resources.displayMetrics.density.toInt()
        val today = Calendar.getInstance()

        // Start 13 full weeks ago, on Sunday
        val startCal = Calendar.getInstance().apply {
            add(Calendar.WEEK_OF_YEAR, -13)
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        }

        // Header row
        val hdr = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 4 * dp)
        }
        fun hTv(t: String) = TextView(ctx).apply {
            text      = t
            textSize  = 10f
            gravity   = Gravity.CENTER
            setTextColor(0xFF8E8E93.toInt())
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        }
        hdr.addView(hTv("").apply {
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.6f
            )
        })
        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { hdr.addView(hTv(it)) }
        container.addView(hdr)

        // Week rows
        val cal = startCal.clone() as Calendar
        while (!cal.after(today)) {
            val wkStart = cal.clone() as Calendar
            val wkEnd   = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 6) }

            val row = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 2 * dp, 0, 2 * dp)
            }

            // Week label
            row.addView(TextView(ctx).apply {
                text     = "${wkFmt.format(wkStart.time)}-${wkEnd.get(Calendar.DAY_OF_MONTH)}"
                textSize = 9f
                gravity  = Gravity.CENTER_VERTICAL
                setTextColor(0xFF8E8E93.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1.6f
                )
            })

            // 7 day cells
            for (i in 0 until 7) {
                val dayCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
                val future = dayCal.after(today)
                val done   = !future &&
                        (habit.completedDates[sdf.format(dayCal.time)] ?: 0) >= habit.goalCount
                val size   = 34 * dp

                row.addView(TextView(ctx).apply {
                    textSize = 14f
                    gravity  = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, size, 1f).apply {
                        setMargins(2 * dp, 2 * dp, 2 * dp, 2 * dp)
                    }
                    when {
                        future -> { /* blank */ }
                        done   -> {
                            text = "✓"
                            setTextColor(0xFF27AE60.toInt())
                            setBackgroundResource(R.drawable.cal_done)
                        }
                        else -> setBackgroundResource(R.drawable.cal_empty)
                    }
                })
            }

            container.addView(row)
            cal.add(Calendar.WEEK_OF_YEAR, 1)
        }
    }
}
