package com.example.to_do.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do.R
import com.example.to_do.model.Task

class TodoAdapter(
    private var todos: List<Task>,
    private val onMenuClick: (View, Task) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val todoTextView: TextView = view.findViewById(R.id.todoTextView)
        val userIdTextView: TextView = itemView.findViewById(R.id.userIdTextView)
        val menuIcon: ImageView = itemView.findViewById(R.id.moreOptionsButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val task = todos[position]
        holder.todoTextView.text = task.todo
        holder.userIdTextView.text = "User ID: ${task.userId}"
        holder.menuIcon.setOnClickListener { onMenuClick(it, task) }
    }

    override fun getItemCount(): Int = todos.size

    fun setTasks(tasks: List<Task>) {
        this.todos = tasks
        notifyDataSetChanged()
    }
}
