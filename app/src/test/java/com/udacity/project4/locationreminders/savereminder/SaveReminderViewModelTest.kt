package com.udacity.project4.locationreminders.savereminder

import android.content.Context
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SaveReminderViewModelTest {
    //TODO: provide testing to the SaveReminderView and its live data objects

    private lateinit var viewModel: SaveReminderViewModel

    private lateinit var dataSource: FakeDataSource

    private lateinit var context: Context

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        context = ApplicationProvider.getApplicationContext()
        FirebaseApp.initializeApp(context)
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @After
    fun cleanupDataSource() = runBlockingTest {
        stopKoin()
    }

    @Test
    fun validateData() {
        val reminder = createReminderDTO()
        assertThat(viewModel.validateEnteredData(reminder), `is`(true))
    }

    @Test
    fun emptyTitle_saveReminder_showSnackBar() {
        val reminder = createReminderDTO()
        reminder.title = ""
        mainCoroutineRule.pauseDispatcher()
        viewModel.validateAndSaveReminder(reminder)
        val snackbar = viewModel.showSnackBarInt.getOrAwaitValue()
        mainCoroutineRule.resumeDispatcher()
        assertThat(snackbar, `is`(R.string.err_enter_title))
    }

    @Test
    fun emptyLocation_saveReminder_showSnackBar() {
        val reminder = createReminderDTO()
        reminder.location = ""
        mainCoroutineRule.pauseDispatcher()
        viewModel.validateAndSaveReminder(reminder)
        val snackbar = viewModel.showSnackBarInt.getOrAwaitValue()
        mainCoroutineRule.resumeDispatcher()
        assertThat(snackbar, `is`(R.string.err_select_location))
    }

    @Test
    fun showLoading_saveReminder_navigateBack() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(createReminderDTO())
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
        val toast = viewModel.showToast.getOrAwaitValue()
        assertThat(toast, `is`(context.getString(R.string.reminder_saved)))
        val navigation = viewModel.navigationCommand.getOrAwaitValue()
        assertThat(navigation, instanceOf(NavigationCommand.BackTo::class.java))
    }

    private fun createReminderDTO(): ReminderDTO {
        return ReminderDTO(
            "Title",
            "Description",
            "Test",
            0.0,
            0.0
        )
    }
}