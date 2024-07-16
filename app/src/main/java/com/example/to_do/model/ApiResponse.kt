package com.example.to_do.model


data class ApiResponse(
    val todos: List<Task>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
