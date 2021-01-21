package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class RemindersListViewModelTest {
    //TODO: provide testing to the RemindersListViewModel and its live data objects

    private lateinit var viewModel: RemindersListViewModel

    private lateinit var dataSource: FakeDataSource

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun cleanupDataSource() = runBlockingTest {
        stopKoin()
    }

    @Test
    fun showLoading_loadReminders_showNoData() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun showLoading_loadReminders_showData() {
        dataSource.reminders = createReminders()
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_showNoData() = runBlockingTest {
        dataSource.deleteAllReminders()
        dataSource.deleteAllReminders()
        viewModel.loadReminders()
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun loadReminders_showData() = runBlockingTest {
        dataSource.deleteAllReminders()
        val reminders = createReminders()
        dataSource.reminders = reminders
        viewModel.loadReminders()
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_remindersSet() = runBlockingTest {
        dataSource.deleteAllReminders()
        val reminders = createReminders()
        dataSource.reminders = reminders
        viewModel.loadReminders()
        assertThat(viewModel.remindersList.value?.size, `is`(2))
        assertThat(viewModel.remindersList.value?.get(0)?.title, `is`("First"))
        assertThat(viewModel.remindersList.value?.get(1)?.title, `is`("Second"))
    }

    private fun createReminders(): MutableList<ReminderDTO> {
        val reminderOne = ReminderDTO(
            "First",
            "At home",
            "Dropped pin",
            38.23239517211914,
            -0.05470231175422663
        )
        val reminderTwo = ReminderDTO(
            "Second",
            "At Mercadona",
            "Mercadona",
            38.23388671875,
            -0.5505555868148804
        )
        return mutableListOf(reminderOne, reminderTwo)
    }
}