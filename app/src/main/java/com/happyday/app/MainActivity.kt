package com.happyday.app

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dataManager: DataManager
    private lateinit var habits: MutableList<Habit>
    private lateinit var todos: MutableList<TodoItem>
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataManager = DataManager(this)
        habits = dataManager.loadHabits()
        todos  = dataManager.loadTodos()

        setupHeader()
        setupHabits()
        setupTodos()
    }

    // ─── Header ──────────────────────────────────────────────────────────────

    private fun setupHeader() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good Morning ☀️"
            hour < 17 -> "Good Afternoon 🌤️"
            else      -> "Good Evening 🌙"
        }
        findViewById<TextView>(R.id.greetingText).text = greeting
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        findViewById<TextView>(R.id.dateText).text = sdf.format(Date())
    }

    // ─── Habits ──────────────────────────────────────────────────────────────

    private fun setupHabits() {
        habitAdapter = HabitAdapter(
            habits,
            onToggleToday = { habit, isChecked ->
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (isChecked) habit.completedDates.add(today)
                else           habit.completedDates.remove(today)
                dataManager.saveHabits(habits)
                habitAdapter.notifyDataSetChanged()
            },
            onDelete = { habit ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Habit")
                    .setMessage("Delete \"${habit.name}\"? All history will be lost.")
                    .setPositiveButton("Delete") { _, _ ->
                        val idx = habits.indexOf(habit)
                        habits.remove(habit)
                        dataManager.saveHabits(habits)
                        habitAdapter.notifyItemRemoved(idx)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        val rv = findViewById<RecyclerView>(R.id.rvHabits)
        rv.adapter = habitAdapter
        rv.layoutManager = LinearLayoutManager(this)
        rv.isNestedScrollingEnabled = false

        findViewById<Button>(R.id.addHabitBtn).setOnClickListener { showAddHabitDialog() }
    }

    private fun showAddHabitDialog() {
        val editText = EditText(this).apply {
            hint = "e.g. Morning Run, Read 20 pages…"
            setPadding(52, 32, 52, 32)
        }
        AlertDialog.Builder(this)
            .setTitle("✨ New Habit")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val name = editText.text.toString().trim()
                if (name.isNotEmpty()) {
                    val habit = Habit(id = UUID.randomUUID().toString(), name = name)
                    habits.add(habit)
                    dataManager.saveHabits(habits)
                    habitAdapter.notifyItemInserted(habits.size - 1)
                } else {
                    Toast.makeText(this, "Please enter a habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ─── To-Do ───────────────────────────────────────────────────────────────

    private fun setupTodos() {
        todoAdapter = TodoAdapter(
            todos,
            onToggle = { todo ->
                todo.isDone = !todo.isDone
                dataManager.saveTodos(todos)
                todoAdapter.notifyDataSetChanged()
            },
            onDelete = { todo ->
                val idx = todos.indexOf(todo)
                todos.remove(todo)
                dataManager.saveTodos(todos)
                todoAdapter.notifyItemRemoved(idx)
            }
        )

        val rv = findViewById<RecyclerView>(R.id.rvTodos)
        rv.adapter = todoAdapter
        rv.layoutManager = LinearLayoutManager(this)
        rv.isNestedScrollingEnabled = false

        findViewById<Button>(R.id.addTodoBtn).setOnClickListener { showAddTodoDialog() }
    }

    private fun showAddTodoDialog() {
        val editText = EditText(this).apply {
            hint = "What do you need to do?"
            setPadding(52, 32, 52, 32)
        }
        AlertDialog.Builder(this)
            .setTitle("📝 New To-Do")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    val todo = TodoItem(id = UUID.randomUUID().toString(), text = text)
                    todos.add(todo)
                    dataManager.saveTodos(todos)
                    todoAdapter.notifyItemInserted(todos.size - 1)
                } else {
                    Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
