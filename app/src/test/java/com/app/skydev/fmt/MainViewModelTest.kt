package com.app.skydev.fmt

import app.cash.turbine.test
import com.app.skydev.fmt.data.model.Item
import com.app.skydev.fmt.data.repository.ItemRepository
import com.app.skydev.fmt.ui.main.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ItemRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test search items`() = runTest {
        val query = "test"
        val items = listOf(Item(name = "Test Item", longitude = 0.0, latitude = 0.0, objectId = "1", lastTimeEdit = 0L, uniqueId = 0L, timeOfAdding = 0L, imageName = "", description = ""))
        coEvery { repository.searchItems(query) } returns flowOf(items)

        viewModel.searchItems(query)

        viewModel.items.test {
            assert(awaitItem() == items)
        }
    }
}