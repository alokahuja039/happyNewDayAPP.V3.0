package com.happyday.app

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
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val todoCheck: CheckBox = view.findViewById(R.id.todoCheck)
        val todoText: TextView = view.findViewById(R.id.todoText)
        val deleteBtn: ImageButton = view.findViewById(R.id.deleteTodoBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = todos[position]

        holder.todoCheck.setOnCheckedChangeListener(null)
        holder.todoCheck.isChecked = todo.isDone
        holder.todoText.text = todo.text

        if (todo.isDone) {
            holder.todoText.paintFlags =
                holder.todoText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.todoText.alpha = 0.45f
        } else {
            holder.todoText.paintFlags =
                holder.todoText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.todoText.alpha = 1f
        }

        holder.todoCheck.setOnCheckedChangeListener { _, _ -> onToggle(todo) }
        holder.deleteBtn.setOnClickListener { onDelete(todo) }
    }

    override fun getItemCount() = todos.size
}
