package com.udacity.project4.locationreminders

import androidx.room.Room
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.RemindersDatabase
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val testModule = module {

    viewModel {
        RemindersListViewModel(get(), get())
    }

    single {
        Room.inMemoryDatabaseBuilder(
            get(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries()
            .build()
    }

    single {
        RemindersLocalRepository(get()) as ReminderDataSource
    }

    factory {
        get<RemindersDatabase>().reminderDao()
    }
}