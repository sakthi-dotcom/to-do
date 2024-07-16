package com.example.to_do.database.table

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import io.realm.RealmSchema

class MyMigration : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema: RealmSchema = realm.schema
        var changeVersion = oldVersion

        if (changeVersion == 0L) {
            schema.create("RealmTask")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("todo", String::class.java)
                .addField("completed", Boolean::class.java)
                .addField("userId", Int::class.java)
            changeVersion++
        }
        if (changeVersion == 1L) {
            val deleteTodoSchema = schema.create("DeleteTodo")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("todo", String::class.java, FieldAttribute.REQUIRED)
                .addField("completed", Boolean::class.java)
                .addField("userId", Int::class.java)
                .addField("isDeleted", Boolean::class.java)
                .addField("deletedOn", Long::class.java)

            // Check if the index exists before adding it
            if (!deleteTodoSchema.hasIndex("id")) {
                deleteTodoSchema.addIndex("id")
            }
            changeVersion++
        }
    }
}


