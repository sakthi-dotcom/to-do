package com.example.to_do.model

import java.util.Date

data class DeleteTask(
    val id: Int,
    val todo: String,
    val completed: Boolean,
    val userId: Int,
    val isDeleted: Boolean,
    val deletedOn: Date
)