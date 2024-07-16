package com.example.to_do.database.table

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmTask : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var todo: String = ""
    var completed: Boolean = false
    var userId: Int = 0
}
