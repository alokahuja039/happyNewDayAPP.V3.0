package com.habittracker.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TodoFragment : Fragment() {

    private lateinit var dm: DataManager
    private lateinit var todos: MutableList<TodoItem>
    private lateinit var adapter: TodoAdapter
    private lateinit var emptyView: LinearLayout
    private lateinit var rv: RecyclerView

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_todo, c, false)

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        dm    = DataManager(requireContext())
        todos = dm.loadTodos()
        emptyView = v.findViewById(R.id.emptyTodos)
        rv        = v.findViewById(R.id.rvTodos)

        adapter = TodoAdapter(todos,
            onToggle = { todo ->
                todo.isDone = !todo.isDone
                dm.saveTodos(todos)
                adapter.notifyDataSetChanged()
                updateEmpty()
            },
            onDelete = { todo ->
                val idx = todos.indexOf(todo)
                todos.remove(todo)
                dm.saveTodos(todos)
                adapter.notifyItemRemoved(idx)
                updateEmpty()
            }
        )
        rv.adapter       = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        v.findViewById<ImageButton>(R.id.addTodoBtn).setOnClickListener { showAdd() }
        updateEmpty()
    }

    private fun showAdd() {
        val input = EditText(requireContext()).apply {
            hint = "What do you need to do?"; setPadding(60, 40, 60, 40)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("New Task").setView(input)
            .setPositiveButton("Add") { _, _ ->
                val t = input.text.toString().trim()
                if (t.isNotEmpty()) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    todos.add(0, TodoItem(UUID.randomUUID().toString(), t, createdDate = today))
                    dm.saveTodos(todos)
                    adapter.notifyItemInserted(0)
                    rv.scrollToPosition(0)
                    updateEmpty()
                }
            }.setNegativeButton("Cancel", null).show()
    }

    private fun updateEmpty() {
        emptyView.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
        rv.visibility        = if (todos.isEmpty()) View.GONE    else View.VISIBLE
    }
}
