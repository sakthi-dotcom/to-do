package com.example.to_do.network

import com.example.to_do.model.ApiResponse
import com.example.to_do.model.DeleteTask
import com.example.to_do.model.Task
import com.example.to_do.model.TaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("todos")
    suspend fun getTodos(): ApiResponse

    @POST("todos/add")
    suspend fun addTask(@Body newTask: Task): TaskResponse

    @PUT("todos/{id}")
    suspend fun updateTask(@Path("id") id: Int): TaskResponse

    @DELETE("todos/{id}")
    suspend fun deleteTask(@Path("id") id: Int): Response<DeleteTask>
}