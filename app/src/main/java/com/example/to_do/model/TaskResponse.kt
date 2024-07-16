package com.example.to_do.model

data class TaskResponse(
    val id: Int,
    val todo: String,
    val completed: Boolean,
    val userId: Int
)
