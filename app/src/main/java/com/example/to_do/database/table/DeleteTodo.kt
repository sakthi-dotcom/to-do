package com.example.to_do.database.table

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class DeleteTodo : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var todo: String = ""
    var completed: Boolean = false
    var userId: Int = 0
    var isDeleted: Boolean = false
    var deletedOn: Long = 0L
}