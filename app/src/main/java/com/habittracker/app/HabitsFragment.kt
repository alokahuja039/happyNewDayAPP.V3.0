package com.habittracker.app

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HabitsFragment : Fragment() {

    private lateinit var dm:      DataManager
    private lateinit var habits:  MutableList<Habit>
    private lateinit var adapter: HabitAdapter
    private lateinit var empty:   LinearLayout
    private lateinit var rv:      RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_habits, container, false)

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        dm     = DataManager(requireContext())
        habits = dm.loadHabits()
        empty  = v.findViewById(R.id.emptyHabits)
        rv     = v.findViewById(R.id.rvHabits)

        adapter = HabitAdapter(
            habits,
            onClick = { habit ->
                val sheet = HabitDetailSheet.forId(habit.id)
                sheet.onClose = { refresh() }
                sheet.show(parentFragmentManager, "detail")
            },
            onSave = { dm.saveHabits(habits) }
        )
        rv.adapter       = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        v.findViewById<ImageButton>(R.id.addHabitBtn).setOnClickListener {
            val sheet = AddHabitSheet.forHabit(null)
            sheet.onSave = { h ->
                habits.add(h)
                dm.saveHabits(habits)
                refresh()
            }
            sheet.show(parentFragmentManager, "add")
        }

        updateEmpty()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val fresh = dm.loadHabits()
        habits.clear()
        habits.addAll(fresh)
        adapter.notifyDataSetChanged()
        updateEmpty()
    }

    private fun updateEmpty() {
        empty.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
        rv.visibility    = if (habits.isEmpty()) View.GONE    else View.VISIBLE
    }
}
