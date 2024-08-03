package com.app.skydev.fmt.data.local

import androidx.room.*
import com.app.skydev.fmt.data.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("SELECT * FROM items WHERE name LIKE :searchQuery OR latitude LIKE :searchQuery OR longitude LIKE :searchQuery")
    fun searchItems(searchQuery: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE uniqueId = :id")
    suspend fun getItemById(id: Long): Item?
}