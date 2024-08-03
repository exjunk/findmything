@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.skydev.fmt.ui.edit

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.skydev.fmt.ui.theme.MyAppTheme

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditActivity : ComponentActivity() {
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.addImage(it.toString()) }
    }

    private lateinit var viewModel: EditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                viewModel = hiltViewModel()
                EditScreen(
                   // onAddImage = { getContent.launch("image/*") },
                    onSave = { finish() }
                )
            }
        }
    }
}



@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel(),
    onSave: () -> Unit
) {
    val item by viewModel.item.collectAsState()
    val images by viewModel.images.collectAsState()

    var isFabExpanded by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            viewModel.addImageFromCamera()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Handle the selected image
            viewModel.addImage(it.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Item") },
                actions = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Save")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (isFabExpanded) {
                    SmallFloatingActionButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            isFabExpanded = false
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.AccountBox, contentDescription = "Gallery")
                    }
                    SmallFloatingActionButton(
                        onClick = {
                            viewModel.createImageFile()?.let { uri ->
                                cameraLauncher.launch(uri)
                            }
                            isFabExpanded = false
                        },
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.Face, contentDescription = "Camera")
                    }
                }
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Image",
                        modifier = Modifier.rotate(animateFloatAsState(if (isFabExpanded) 45f else 0f).value)
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Your existing form fields here
            TextField(
                value = item.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            TextField(
                value = item.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            TextField(
                value = item.latitude.toString(),
                onValueChange = { viewModel.updateLatitude(it.toDoubleOrNull() ?: 0.0) },
                label = { Text("Latitude") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            TextField(
                value = item.longitude.toString(),
                onValueChange = { viewModel.updateLongitude(it.toDoubleOrNull() ?: 0.0) },
                label = { Text("Longitude") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Display images in a grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(images) { image ->
                    ImageItem(
                        imageUri = image,
                        onDelete = { viewModel.removeImage(image) }
                    )
                }
            }
        }
    }
}

@Composable
fun ImageItem(imageUri: String, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(120.dp)
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Item image",
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete image")
        }
    }
}