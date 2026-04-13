package com.habittracker.app

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val todos: MutableList<TodoItem>,
    private val onToggle: (TodoItem) -> Unit,
    private val onDelete: (TodoItem) -> Unit
) : RecyclerView.Adapter<TodoAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val check: CheckBox         = v.findViewById(R.id.todoCheck)
        val text: TextView          = v.findViewById(R.id.todoText)
        val del: ImageButton        = v.findViewById(R.id.deleteTodoBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val todo = todos[pos]
        h.check.setOnCheckedChangeListener(null)
        h.check.isChecked = todo.isDone
        h.text.text = todo.text
        if (todo.isDone) {
            h.text.paintFlags = h.text.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            h.text.alpha = 0.4f
        } else {
            h.text.paintFlags = h.text.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            h.text.alpha = 1f
        }
        h.check.setOnCheckedChangeListener { _, _ -> onToggle(todo) }
        h.del.setOnClickListener { onDelete(todo) }
    }

    override fun getItemCount() = todos.size
}
