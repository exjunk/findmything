package com.app.skydev.fmt.ui.edit

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.skydev.fmt.data.model.Item
import com.app.skydev.fmt.data.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val itemId: Long = savedStateHandle.get<Long>("ITEM_ID") ?: -1L

    private val _item = MutableStateFlow(Item(objectId = "", name = "", description = "", timeOfAdding = 0, lastTimeEdit = 0, imageName = "", latitude = 0.0, longitude = 0.0))
    val item: StateFlow<Item> = _item

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images: StateFlow<List<String>> = _images
    private var _tempImageUri: Uri? = null
    val tempImageUri: Uri?
        get() = _tempImageUri



    init {
        if (itemId != -1L) {
            viewModelScope.launch {
                repository.getItemById(itemId)?.let { loadedItem ->
                    _item.value = loadedItem
                    _images.value = loadedItem.imageName.split(",")
                }
            }
        }
    }

    fun updateName(name: String) {
        _item.value = _item.value.copy(name = name)
    }

    fun updateDescription(description: String) {
        _item.value = _item.value.copy(description = description)
    }

    fun updateLatitude(latitude: Double) {
        _item.value = _item.value.copy(latitude = latitude)
    }

    fun updateLongitude(longitude: Double) {
        _item.value = _item.value.copy(longitude = longitude)
    }

    fun addImage(imageUri: String) {
        _images.value += imageUri
        updateItemImages()
    }

    fun removeImage(imageUri: String) {
        _images.value -= imageUri
        updateItemImages()
    }

    private fun updateItemImages() {
        _item.value = _item.value.copy(imageName = _images.value.joinToString(","))
    }

    fun saveItem() {
        viewModelScope.launch {
            val updatedItem = _item.value.copy(lastTimeEdit = System.currentTimeMillis())
            if (itemId == -1L) {
                repository.insertItem(updatedItem)
            } else {
                repository.updateItem(updatedItem)
            }
        }
    }

    fun createImageFile(): Uri? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            val file = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            _tempImageUri = FileProvider.getUriForFile(
                application,
                "${application.packageName}.fileprovider",
                file
            )
            _tempImageUri
        } catch (ex: IOException) {
            null
        }
    }

    fun addImageFromCamera() {
        _tempImageUri?.let { uri ->
            addImage(uri.toString())
            _tempImageUri = null
        }
    }
}