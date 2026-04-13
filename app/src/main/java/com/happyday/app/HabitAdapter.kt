package com.habittracker.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onClick: (Habit) -> Unit,
    private val onSave: () -> Unit
) : RecyclerView.Adapter<HabitAdapter.VH>() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val box: TextView           = v.findViewById(R.id.habitCheckBox)
        val name: TextView          = v.findViewById(R.id.habitName)
        val stats: TextView         = v.findViewById(R.id.habitStats)
        val p7d: RadialProgressView = v.findViewById(R.id.p7d)
        val p4w: RadialProgressView = v.findViewById(R.id.p4w)
        val p3m: RadialProgressView = v.findViewById(R.id.p3m)
        val p1y: RadialProgressView = v.findViewById(R.id.p1y)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val habit   = habits[pos]
        val today   = sdf.format(Date())
        val todayCnt= habit.completedDates[today] ?: 0
        val done    = todayCnt >= habit.goalCount

        h.name.text = habit.name

        if (habit.goalDirection == "do_less") {
            h.box.text = todayCnt.toString()
            h.box.setBackgroundResource(if (todayCnt >= habit.goalCount) R.drawable.cb_red else R.drawable.cb_outline)
            h.box.setTextColor(if (todayCnt >= habit.goalCount) 0xFFE74C3C.toInt() else 0xFF8E8E93.toInt())
        } else {
            h.box.text = if (done) "✓" else ""
            h.box.setBackgroundResource(if (done) R.drawable.cb_done else R.drawable.cb_outline)
            h.box.setTextColor(0xFFFFFFFF.toInt())
        }

        val streak = streak(habit)
        val logged = habit.completedDates.values.count { it >= habit.goalCount }
        val parts  = mutableListOf<String>()
        if (streak > 0) parts.add("⚡ $streak days")
        parts.add("📅 $logged logged")
        h.stats.text = parts.joinToString("   ")

        h.p7d.setProgress(rate(habit, 7))
        h.p4w.setProgress(rate(habit, 28))
        h.p3m.setProgress(rate(habit, 90))
        h.p1y.setProgress(rate(habit, 365))

        h.itemView.setOnClickListener { onClick(habit) }
        h.box.setOnClickListener {
            val cur = habit.completedDates[today] ?: 0
            val next = if (cur < habit.goalCount) cur + 1 else 0
            if (next == 0) habit.completedDates.remove(today) else habit.completedDates[today] = next
            onSave(); notifyItemChanged(pos)
        }
    }

    override fun getItemCount() = habits.size

    private fun rate(habit: Habit, days: Int): Float {
        val cal = Calendar.getInstance(); var met = 0
        repeat(days) {
            if (it > 0) cal.add(Calendar.DAY_OF_YEAR, -1)
            if ((habit.completedDates[sdf.format(cal.time)] ?: 0) >= habit.goalCount) met++
        }
        return met.toFloat() / days
    }

    private fun streak(habit: Habit): Int {
        val cal = Calendar.getInstance()
        if ((habit.completedDates[sdf.format(cal.time)] ?: 0) < habit.goalCount)
            cal.add(Calendar.DAY_OF_YEAR, -1)
        var s = 0
        while ((habit.completedDates[sdf.format(cal.time)] ?: 0) >= habit.goalCount) {
            s++; cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return s
    }
}
