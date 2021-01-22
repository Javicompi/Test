package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {
    //    TODO: Add testing implementation to the RemindersLocalRepository.kt

    private lateinit var repository: RemindersLocalRepository

    private lateinit var remindersDao: RemindersDao

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val reminder1 = ReminderDTO(
        "Title1",
        "Description1",
        "Location1",
        0.0,
        0.0
    )

    private val reminder2 = ReminderDTO(
        "Title2",
        "Description2",
        "Location2",
        0.0,
        0.0
    )

    @Before
    fun createRepository() {
        remindersDao = LocalDB.createRemindersDao(ApplicationProvider.getApplicationContext())
        repository = RemindersLocalRepository(remindersDao, Dispatchers.Unconfined)
    }

    @After
    fun cleanUp() = runBlockingTest {
        remindersDao.deleteAllReminders()
    }

    @Test
    fun saveReminder_retrieveReminder() = runBlockingTest{
        repository.saveReminder(reminder1)
        val retrieved = repository.getReminder(reminder1.id)
        assertThat(retrieved.succeeded, `is`(true))
        retrieved as Result.Success
        assertThat(retrieved.data.id, `is`(reminder1.id))
        repository.deleteAllReminders()
    }

    @Test
    fun saveReminders_retrieveReminders() = runBlockingTest {
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        val retrievedReminders = repository.getReminders()
        assertThat(retrievedReminders.succeeded, `is`(true))
        retrievedReminders as Result.Success
        assertThat(retrievedReminders.data.get(0).id, `is`(reminder1.id))
        assertThat(retrievedReminders.data.get(1).id, `is`(reminder2.id))
        repository.deleteAllReminders()
    }

    @Test
    fun saveReminders_retrieveReminders_deleteAll() = runBlockingTest {
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        val retrievedReminders = repository.getReminders()
        assertThat(retrievedReminders.succeeded, `is`(true))
        retrievedReminders as Result.Success
        assertThat(retrievedReminders.data.size, `is`(2))
        repository.deleteAllReminders()
        val emptyReminders = repository.getReminders()
        assertThat(emptyReminders.succeeded, `is`(true))
        emptyReminders as Result.Success
        assertThat(emptyReminders.data.size, `is`(0))
    }
}