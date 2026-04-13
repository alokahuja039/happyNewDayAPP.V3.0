package com.habittracker.app

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TodoFragment : Fragment() {

    private lateinit var dm:      DataManager
    private lateinit var todos:   MutableList<TodoItem>
    private lateinit var adapter: TodoAdapter
    private lateinit var empty:   LinearLayout
    private lateinit var rv:      RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_todo, container, false)

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        dm    = DataManager(requireContext())
        todos = dm.loadTodos()
        empty = v.findViewById(R.id.emptyTodos)
        rv    = v.findViewById(R.id.rvTodos)

        adapter = TodoAdapter(
            todos,
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

        v.findViewById<ImageButton>(R.id.addTodoBtn).setOnClickListener { showAddDialog() }

        updateEmpty()
    }

    private fun showAddDialog() {
        val input = EditText(requireContext()).apply {
            hint = "What do you need to do?"
            setPadding(60, 40, 60, 40)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("New Task")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    todos.add(0, TodoItem(UUID.randomUUID().toString(), text, createdDate = today))
                    dm.saveTodos(todos)
                    adapter.notifyItemInserted(0)
                    rv.scrollToPosition(0)
                    updateEmpty()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateEmpty() {
        empty.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
        rv.visibility    = if (todos.isEmpty()) View.GONE    else View.VISIBLE
    }
}
