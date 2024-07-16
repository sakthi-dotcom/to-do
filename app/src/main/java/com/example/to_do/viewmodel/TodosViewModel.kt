package com.example.to_do.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_do.database.table.DeleteTodo
import com.example.to_do.database.table.RealmTask
import com.example.to_do.model.Task
import com.example.to_do.model.TaskResponse
import com.example.to_do.network.RetrofitInstance
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodosViewModel : ViewModel() {

    private val _todos = MutableLiveData<List<Task>>()
    val todos: LiveData<List<Task>> get() = _todos

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getTodos()
                _todos.value = response.todos
                saveTasksToDb(response.todos)
                fetchTasksFromDb()
            } catch (e: Exception) {
                Log.e("TodosViewModel", "Error fetching tasks: ${e.message}")
                fetchTasksFromDb()
            } finally {
                _loading.value = false
            }
        }
    }

    private fun fetchTasksFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = Realm.getDefaultInstance()
            try {
                val results = realm.where(RealmTask::class.java).findAll()
                val tasks = results.map { realmTask ->
                    Task(
                        id = realmTask.id,
                        todo = realmTask.todo,
                        completed = realmTask.completed,
                        userId = realmTask.userId
                    )
                }
                _todos.postValue(tasks)
            } catch (e: Exception) {
                Log.e("TodosViewModel", "Error fetching tasks from Realm: ${e.message}")
            } finally {
                realm.close()
            }
        }
    }

    private fun saveTasksToDb(tasks: List<Task>) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { realmInstance ->
                    tasks.forEach { task ->
                        var realmTask = realmInstance.where(RealmTask::class.java).equalTo("id", task.id).findFirst()
                        if (realmTask == null) {
                            realmTask = realmInstance.createObject(RealmTask::class.java, task.id!!)
                        }
                        realmTask?.todo = task.todo
                        realmTask?.completed = task.completed
                        realmTask?.userId = task.userId
                    }
                }
            } catch (e: Exception) {
                Log.e("RealmError", "Error saving tasks to Realm: ${e.message}")
            } finally {
                realm.close()
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.addTask(task)
                val newTask = response.toTask()
                newTask.id = generateUniqueId()
                addTaskDb(newTask)
                val updatedTasks = _todos.value?.toMutableList() ?: mutableListOf()
                updatedTasks.add(newTask)
                _todos.postValue(updatedTasks)
                Log.d("New Added Data", "Id of new data is :${newTask.id}")
            } catch (e: Exception) {
                Log.d("Something went wrong", e.message.toString())
            }
        }
    }

    private fun addTaskDb(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { realmInstance ->
                    var realmTask = realmInstance.where(RealmTask::class.java).equalTo("id", task.id).findFirst()
                    if (realmTask == null) {
                        realmTask = realmInstance.createObject(RealmTask::class.java, task.id!!)
                    }
                    realmTask?.todo = task.todo
                    realmTask?.completed = task.completed
                    realmTask?.userId = task.userId
                }
            } catch (e: Exception) {
                Log.e("RealmError", "Error adding task to Realm: ${e.message}")
            } finally {
                realm.close()
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                val apiResponse = task.id?.let { RetrofitInstance.api.updateTask(it) }
                Log.d("Update API response", "Data has been updated: ${apiResponse?.todo}")

                val updatedTaskResponse = task.id?.let {
                    TaskResponse(
                        id = it,
                        todo = task.todo,
                        completed = task.completed,
                        userId = task.userId
                    )
                }
                updatedTaskResponse?.let { updateTaskDb(it) }

                // Update LiveData to notify UI of changes
                val updatedTasks = _todos.value?.map { if (it.id == task.id) task else it } ?: listOf()
                _todos.postValue(updatedTasks)
                Log.d("Update Data", "Updated data with id: ${task.id}")
            } catch (e: Exception) {
                Log.e("Update Error", "Error updating task: ${e.message}")
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.deleteTask(task.id!!)
                if (response.isSuccessful) {
                    val realm = Realm.getDefaultInstance()
                    try {
                        realm.executeTransaction { realmInstance ->
                            val realmTask = realmInstance.where(DeleteTodo::class.java).equalTo("id", task.id).findFirst()
                            realmTask?.apply {
                                isDeleted = true
                                deletedOn = System.currentTimeMillis()
                            }
                        }
                    } finally {
                        realm.close()
                    }

                    val updatedTasks = _todos.value?.filter { it.id != task.id }
                    _todos.postValue(updatedTasks!!)
                } else { Log.e("TodosViewModel", "API call to delete task failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Delete Error", "Error deleting task: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }
    }


    private fun updateTaskDb(task: TaskResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            val realm = Realm.getDefaultInstance()
            try {
                realm.executeTransaction { realmInstance ->
                    val realmTask = realmInstance.where(RealmTask::class.java).equalTo("id", task.id).findFirst()
                    realmTask?.apply {
                        todo = task.todo
                        completed = task.completed
                        userId = task.userId
                    }
                }
                Log.d("Updated Data", "Data updated to ${task.todo}")
            } catch (e: Exception) {
                Log.e("RealmError", "Error updating task in Realm: ${e.message}")
            } finally {
                realm.close()
            }
        }
    }

    private fun generateUniqueId(): Int {
        val realm = Realm.getDefaultInstance()
        val maxId = realm.where(RealmTask::class.java).max("id")?.toInt() ?: 0
        realm.close()
        return maxId + 1
    }

    private fun TaskResponse.toTask(): Task {
        return Task(
            id = this.id,
            todo = this.todo,
            completed = this.completed,
            userId = this.userId
        )
    }
}
