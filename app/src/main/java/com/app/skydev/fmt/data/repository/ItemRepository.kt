package com.app.skydev.fmt.data.repository

import com.app.skydev.fmt.data.local.ItemDao
import com.app.skydev.fmt.data.model.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ItemRepository @Inject constructor(private val itemDao: ItemDao) {
    fun getAllItems(): Flow<List<Item>> = itemDao.getAllItems()

    suspend fun insertItem(item: Item) = itemDao.insertItem(item)

    suspend fun updateItem(item: Item) = itemDao.updateItem(item)

    suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)

    fun searchItems(query: String): Flow<List<Item>> = itemDao.searchItems("%$query%")

    suspend fun getItemById(id: Long): Item? = itemDao.getItemById(id)
}