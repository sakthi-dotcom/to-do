package com.example.to_do.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.to_do.R
import com.example.to_do.model.Task
import com.example.to_do.viewmodel.TodosViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddTaskBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var editTextTaskName: TextInputEditText
    private lateinit var editTextUserID: TextInputEditText
    private lateinit var buttonSave: MaterialButton

    private lateinit var viewModel: TodosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet_add_task, container, false)

        editTextTaskName = view.findViewById(R.id.editTextTaskName)
        editTextUserID = view.findViewById(R.id.editTextUserID)
        buttonSave = view.findViewById(R.id.buttonSave)

        viewModel = ViewModelProvider(requireActivity())[TodosViewModel::class.java]

        buttonSave.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val userIDString = editTextUserID.text.toString()

            if (taskName.isNotEmpty() && userIDString.isNotEmpty()) {
                val userID = userIDString.toIntOrNull()
                if (userID != null) {
                    viewModel.addTask(Task(todo = taskName, completed = false, userId = userID))
                    dismiss()
                } else {
                    Toast.makeText(context, "User id must be a valid number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

}



