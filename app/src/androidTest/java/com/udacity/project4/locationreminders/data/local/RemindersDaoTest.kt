package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
//    TODO: Add testing implementation to the RemindersDao.kt
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertReminder_getReminderById() = runBlockingTest{
        val reminder = ReminderDTO(
            "Title",
            "Description",
            "Somewhere",
            0.0,
            0.0
        )
        database.reminderDao().saveReminder(reminder)
        val retrieved = database.reminderDao().getReminderById(reminder.id)
        assertThat(retrieved, notNullValue())
        assertThat(retrieved.id, `is`(reminder.id))
    }

    @Test
    fun B_getAllReminders() = runBlockingTest {
        val reminder = ReminderDTO(
            "Title",
            "Description",
            "Somewhere",
            0.0,
            0.0
        )
        database.reminderDao().saveReminder(reminder)
        val reminder2 = ReminderDTO(
            "Title2",
            "Description2",
            "Somewhere2",
            0.0,
            0.0
        )
        database.reminderDao().saveReminder(reminder2)
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.size, `is`(2))
        assertThat(reminders.get(0).title, `is`("Title"))
        assertThat(reminders.get(1).title, `is`("Title2"))
    }

    @Test
    fun C_deleteAllReminders() = runBlockingTest{
        val reminder = ReminderDTO(
            "Title3",
            "Description3",
            "Somewhere3",
            0.0,
            0.0
        )
        database.reminderDao().saveReminder(reminder)
        val retrieved = database.reminderDao().getReminderById(reminder.id)
        assertThat(reminder.id, `is`(retrieved.id))
        database.reminderDao().deleteAllReminders()
        val emptyReminders = database.reminderDao().getReminders()
        assertThat(emptyReminders.size, `is`(0))
    }
}