package com.app.skydev.fmt.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.skydev.fmt.data.model.Item
import com.app.skydev.fmt.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: ItemRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            combine(repository.getAllItems(), searchQuery) { items, query ->
                if (query.isEmpty()) items else items.filter { it.name.contains(query, ignoreCase = true) }
            }.collect { filteredItems ->
                _items.value = filteredItems
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}