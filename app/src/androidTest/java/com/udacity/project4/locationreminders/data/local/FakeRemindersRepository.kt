package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeRemindersRepository : ReminderDataSource {

    var remindersServiceData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    //private val observableReminders = MutableLiveData<Result<List<ReminderDTO>>>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(remindersServiceData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        remindersServiceData[id]?.let {
            return Result.Success(it)
        }
        return Result.Error("Could not find reminder")
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }
}