package com.habittracker.app

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class HabitDetailSheet : BottomSheetDialogFragment() {

    var onDismiss: (() -> Unit)? = null
    private val sdf     = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dispSdf = SimpleDateFormat("MM/dd/yy", Locale.getDefault())

    private val quotes = listOf(
        "Consistency is the key to unlocking your potential.",
        "Every day you show up is a victory worth celebrating.",
        "Small steps lead to big changes. Keep going!",
        "Your future self will thank you for starting today.",
        "Progress is not always visible, but it is always happening.",
        "The only bad habit is the one you never started tracking.",
        "Discipline is choosing what you want most over what you want now."
    )

    companion object {
        fun forId(id: String) = HabitDetailSheet().apply {
            arguments = Bundle().apply { putString("id", id) }
        }
    }

    override fun getTheme() = R.style.BottomSheetTheme
    override fun onStart() {
        super.onStart()
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED; skipCollapsed = true
        }
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.sheet_habit_detail, c, false)

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        val id   = arguments?.getString("id") ?: return
        val dm   = DataManager(requireContext())
        val habit= dm.loadHabits().find { it.id == id } ?: return
        val today= sdf.format(Date())

        v.findViewById<TextView>(R.id.detailName).text  = habit.name
        v.findViewById<TextView>(R.id.detailQuote).text = quotes.random()

        val statusTv = v.findViewById<TextView>(R.id.detailStatus)
        fun refresh(h: Habit) {
            val done = (h.completedDates[today] ?: 0) >= h.goalCount
            statusTv.text = if (done) "✓  Done today" else "Not yet done today"
            statusTv.setTextColor(if (done) 0xFF27AE60.toInt() else 0xFF8E8E93.toInt())
        }
        refresh(habit)

        val cur = currentStreak(habit); val lng = longestStreak(habit)
        v.findViewById<TextView>(R.id.detailStreak).text =
            "⚡ Current streak: $cur days   •   Longest: $lng days"

        val logged = habit.completedDates.values.count { it >= habit.goalCount }
        val startStr = if (habit.startDate.isNotEmpty())
            try { dispSdf.format(sdf.parse(habit.startDate)!!) } catch (e: Exception) { "—" } else "—"
        v.findViewById<TextView>(R.id.detailLogged).text = "📅 $logged days logged  •  started $startStr"

        v.findViewById<RadialProgressView>(R.id.d7d).setProgress(rate(habit, 7), true)
        v.findViewById<RadialProgressView>(R.id.d4w).setProgress(rate(habit, 28), true)
        v.findViewById<RadialProgressView>(R.id.d3m).setProgress(rate(habit, 90), true)
        v.findViewById<RadialProgressView>(R.id.d1y).setProgress(rate(habit, 365), true)

        // 1. Close
        v.findViewById<ImageButton>(R.id.btnClose).setOnClickListener { dismiss() }

        // 2. Calendar / log
        v.findViewById<ImageButton>(R.id.btnCalendar).setOnClickListener {
            HabitCalendarSheet.forId(id).show(parentFragmentManager, "cal")
        }

        // 3. Mark done today
        v.findViewById<ImageButton>(R.id.btnMarkDone).setOnClickListener {
            val list = dm.loadHabits().toMutableList()
            val idx  = list.indexOfFirst { it.id == id }
            if (idx >= 0) {
                val h = list[idx]
                val cur2 = h.completedDates[today] ?: 0
                val next = if (cur2 < h.goalCount) cur2 + 1 else 0
                if (next == 0) h.completedDates.remove(today) else h.completedDates[today] = next
                dm.saveHabits(list); refresh(h)
            }
        }

        // 4. Delete
        v.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Delete \"${habit.name}\"? All history will be lost.")
                .setPositiveButton("Delete") { _, _ ->
                    val list = dm.loadHabits().toMutableList()
                    list.removeAll { it.id == id }
                    dm.saveHabits(list); dismiss()
                }.setNegativeButton("Cancel", null).show()
        }

        // 5. Edit
        v.findViewById<ImageButton>(R.id.btnEdit).setOnClickListener {
            val current = dm.loadHabits().find { it.id == id } ?: return@setOnClickListener
            val sheet = AddHabitSheet.forHabit(current)
            sheet.onSave = { updated ->
                val list = dm.loadHabits().toMutableList()
                val i = list.indexOfFirst { it.id == id }
                if (i >= 0) list[i] = updated
                dm.saveHabits(list); dismiss()
            }
            sheet.show(parentFragmentManager, "edit")
        }
    }

    override fun onDismiss(d: DialogInterface) { super.onDismiss(d); onDismiss?.invoke() }

    private fun rate(h: Habit, days: Int): Float {
        val cal = Calendar.getInstance(); var met = 0
        repeat(days) {
            if (it > 0) cal.add(Calendar.DAY_OF_YEAR, -1)
            if ((h.completedDates[sdf.format(cal.time)] ?: 0) >= h.goalCount) met++
        }
        return met.toFloat() / days
    }

    private fun currentStreak(h: Habit): Int {
        val cal = Calendar.getInstance()
        if ((h.completedDates[sdf.format(cal.time)] ?: 0) < h.goalCount)
            cal.add(Calendar.DAY_OF_YEAR, -1)
        var s = 0
        while ((h.completedDates[sdf.format(cal.time)] ?: 0) >= h.goalCount) { s++; cal.add(Calendar.DAY_OF_YEAR, -1) }
        return s
    }

    private fun longestStreak(h: Habit): Int {
        if (h.completedDates.isEmpty()) return 0
        val sorted = h.completedDates.entries.filter { it.value >= h.goalCount }
            .mapNotNull { try { sdf.parse(it.key) } catch (e: Exception) { null } }.sortedBy { it.time }
        if (sorted.isEmpty()) return 0
        var lng = 1; var cur = 1; val cal = Calendar.getInstance()
        for (i in 1 until sorted.size) {
            cal.time = sorted[i - 1]; cal.add(Calendar.DAY_OF_YEAR, 1)
            if (sdf.format(cal.time) == sdf.format(sorted[i])) { cur++; if (cur > lng) lng = cur } else cur = 1
        }
        return lng
    }
}
