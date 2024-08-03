package com.app.skydev.fmt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val uniqueId: Long = 0,
    val objectId: String,
    var name: String,
    var description: String,
    var timeOfAdding: Long,
    var lastTimeEdit: Long,
    var imageName: String,
    var latitude: Double,
    var longitude: Double
)