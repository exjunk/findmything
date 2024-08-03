package com.app.skydev.fmt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.skydev.fmt.data.model.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}