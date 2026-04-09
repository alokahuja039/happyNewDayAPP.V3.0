package com.happyday.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onToggleToday: (Habit, Boolean) -> Unit,
    private val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.ViewHolder>() {

    private val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitName: TextView = view.findViewById(R.id.habitName)
        val todayCheck: CheckBox = view.findViewById(R.id.todayCheck)
        val streakText: TextView = view.findViewById(R.id.streakText)
        val gridView: HabitGridView = view.findViewById(R.id.habitGrid)
        val deleteBtn: ImageButton = view.findViewById(R.id.deleteHabitBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitName.text = habit.name

        // Bind checkbox without triggering listener during bind
        holder.todayCheck.setOnCheckedChangeListener(null)
        holder.todayCheck.isChecked = habit.completedDates.contains(todayStr)
        holder.todayCheck.setOnCheckedChangeListener { _, isChecked ->
            onToggleToday(habit, isChecked)
        }

        val streak = calculateStreak(habit.completedDates)
        holder.streakText.text = if (streak > 0) "🔥 $streak day streak" else "Start your streak today!"

        holder.gridView.setCompletedDates(habit.completedDates)

        holder.deleteBtn.setOnClickListener { onDelete(habit) }
    }

    override fun getItemCount() = habits.size

    private fun calculateStreak(dates: Set<String>): Int {
        if (dates.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        // Allow streak if today OR yesterday is done (grace period)
        val today = sdf.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(cal.time)
        if (!dates.contains(today) && !dates.contains(yesterday)) return 0

        // Walk backwards from the most recent completed day
        cal.time = Date()
        if (!dates.contains(sdf.format(cal.time))) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        var streak = 0
        while (dates.contains(sdf.format(cal.time))) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        return streak
    }
}
