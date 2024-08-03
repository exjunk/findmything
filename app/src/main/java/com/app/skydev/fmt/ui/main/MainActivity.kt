@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.skydev.fmt.ui.main

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.skydev.fmt.data.model.Item
import com.app.skydev.fmt.ui.edit.EditActivity
import dagger.hilt.android.AndroidEntryPoint
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.MoreVert
import com.app.skydev.fmt.ui.settings.SettingsActivity
import com.app.skydev.fmt.ui.theme.MyAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) + if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    } else {
                        listOf(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                )

                LaunchedEffect(Unit) {
                    permissionsState.launchMultiplePermissionRequest()
                }

                MainScreen(
                    onEditItem = { item ->
                        startActivity(Intent(this@MainActivity, EditActivity::class.java).apply {
                            putExtra("ITEM_ID", item.uniqueId)
                        })
                    },
                    onAddNewItem = {
                        startActivity(Intent(this@MainActivity, EditActivity::class.java))
                    },
                    onOpenSettings = {
                        startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onEditItem: (Item) -> Unit,
    onAddNewItem: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val items by viewModel.items.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My App") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNewItem) {
                Icon(Icons.Filled.Add, contentDescription = "Add new item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchTopAppBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery
            )
            LazyColumn {
                items(items) { item ->
                    ItemCard(item = item, onClick = { onEditItem(item) })
                }
            }
        }
    }
}
@Composable
fun SearchTopAppBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text("My App")
            }
        },
        actions = {
            IconButton(onClick = { isSearchActive = !isSearchActive }) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            AnimatedVisibility(
                visible = isSearchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun ItemCard(item: Item, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
        ,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = item.imageName,
                contentDescription = "Item image",
                modifier = Modifier.size(80.dp)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(item.name, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Location: ${item.latitude}, ${item.longitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}