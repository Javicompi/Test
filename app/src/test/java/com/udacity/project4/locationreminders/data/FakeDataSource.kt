package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.*

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        //TODO("Return the reminders")
        reminders?.let { return Success(ArrayList(it)) }
        return Error(Exception("Reminders not found").toString())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        //TODO("save the reminder")
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        //TODO("return the reminder with the id")
        reminders.let {
            //return Success(it.find { it.id == id })
            val reminder = it?.find { it.id == id }
            if (reminder != null) {
                return Success(reminder)
            } else {
                return Error(Exception("Reminder not found").toString())
            }
        }
        return Error(Exception("Reminder not found").toString())
    }

    override suspend fun deleteAllReminders() {
        //TODO("delete all the reminders")
        reminders?.clear()
    }
}