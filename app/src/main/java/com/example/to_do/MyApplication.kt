package com.example.to_do

import android.app.Application
import android.util.Log
import com.example.to_do.database.table.MyMigration
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)

        val realmConfig = RealmConfiguration.Builder()
            .schemaVersion(2)
            .migration(MyMigration())
            .build()
        Realm.setDefaultConfiguration(realmConfig)
        Log.d("RealmInit", "Realm default configuration set with schema version: ${realmConfig.schemaVersion}")
    }
}


