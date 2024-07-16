package com.example.to_do.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.to_do.R
import com.example.to_do.model.Task
import com.example.to_do.viewmodel.TodosViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class EditTodoBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var updateTodo: TextInputEditText
    private lateinit var updateData: MaterialButton
    private var task: Task? = null
    private val todosViewModel: TodosViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_todo_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateTodo = view.findViewById(R.id.updateTextTaskName)
        updateData = view.findViewById(R.id.buttonUpdate)

        task = arguments?.getParcelable("task") ?: Task(0, "No Data found", false, 0)
        updateTodo.setText(task!!.todo)

        updateData.setOnClickListener {
            val updatedTodo = updateTodo.text.toString()
            task?.let {
                val updatedTask = it.copy(todo = updatedTodo)
                todosViewModel.updateTask(updatedTask)
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(task: Task): EditTodoBottomSheetFragment {
            val fragment = EditTodoBottomSheetFragment()
            val args = Bundle()
            args.putParcelable("task", task)
            fragment.arguments = args
            return fragment
        }
    }


}
