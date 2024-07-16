package com.example.to_do

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do.adapter.TodoAdapter
import com.example.to_do.fragment.AddTaskBottomSheetFragment
import com.example.to_do.fragment.EditTodoBottomSheetFragment
import com.example.to_do.model.Task
import com.example.to_do.utilities.Utils.isNetworkAvailable
import com.example.to_do.viewmodel.TodosViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: TodosViewModel
    private lateinit var adapter: TodoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var addTask: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (!isNetworkAvailable(this)) {
            showSnackbar("No internet connection")
        }

        viewModel = ViewModelProvider(this).get(TodosViewModel::class.java)
        adapter = TodoAdapter(emptyList()) { view, task ->
            showPopupMenu(view, task)
        }

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        addTask = findViewById(R.id.fabAddTodo)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.todos.observe(this) { tasks ->
            adapter.setTasks(tasks)
        }

        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

        addTask.setOnClickListener {
            val bottomSheetFragment = AddTaskBottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }


    private fun openEditTodoBottomSheet(task: Task) {
        val bottomSheetFragment = EditTodoBottomSheetFragment.newInstance(task)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }
    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun showPopupMenu(view: View, task: Task) {
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.popup_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    openEditTodoBottomSheet(task)
                    true
                }
                R.id.delete -> {
                    showDeleteConfirmationDialog(task)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showDeleteConfirmationDialog(task: Task) {
        val message = "Are you sure you want to delete this task?"
        val spannable = SpannableString(message)

        val alertDialog = AlertDialog.Builder(this@MainActivity)
            .setTitle("Delete Task")
            .setMessage(spannable)
            .setPositiveButton("Yes") { dialog, _ ->
                viewModel.deleteTask(task)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()


        alertDialog.show()

        val positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton?.setTextColor(Color.RED)
    }


}
