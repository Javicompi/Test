package com.udacity.project4.locationreminders

import androidx.room.Room
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.FakeRemindersRepository
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val testModule = module {

    viewModel {
        RemindersListViewModel(
            get(),
            get() as FakeRemindersRepository
        )
    }
    single {
        Room.inMemoryDatabaseBuilder(
            get(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries()
            .build()
    }
    //single { RemindersLocalRepository(get()) as ReminderDataSource }
    single { LocalDB.createRemindersDao(androidContext()) }
    single { FakeRemindersRepository() }
    //single { FakeRemindersRepository() as ReminderDataSource }
    //single { LocalDB.createRemindersDao(androidContext()) }
}