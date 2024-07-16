package com.example.to_do.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: Int? = null,
    val todo: String,
    val completed: Boolean,
    val userId: Int
):Parcelable
