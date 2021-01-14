package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.utils.ACTION_GEOFENCE_EVENT
import com.udacity.project4.utils.GEOFENCE_RADIUS_IN_METERS
import kotlinx.coroutines.launch

class SaveReminderViewModel(val app: Application, val dataSource: ReminderDataSource) :
    BaseViewModel(app) {

    private val TAG = SaveReminderViewModel::class.java.simpleName

    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    val reminderSelectedLocationStr = MutableLiveData<String>()
    val selectedPOI = MutableLiveData<PointOfInterest>()
    val latitude = MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(app, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(app, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        selectedPOI.value = null
        latitude.value = null
        longitude.value = null
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    /*fun validateAndSaveReminder(reminderData: ReminderDataItem) {
        if (validateEnteredData(reminderData)) {
            saveReminder(reminderData)
        }
    }*/
    fun validateAndSaveReminder(reminderDTO: ReminderDTO) {
        if (validateEnteredData(reminderDTO)) {
            saveReminder(reminderDTO)
        }
    }

    /**
     * Save the reminder to the data source
     */
    fun saveReminder(reminderData: ReminderDTO) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(reminderData)
            //addGeofence(reminderData)
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.BackTo(R.id.reminderListFragment)
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    fun validateEnteredData(reminderData: ReminderDTO): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(reminderData: ReminderDTO) {
        val geofence = Geofence.Builder()
            .setRequestId(reminderData.id)
            .setCircularRegion(reminderData.latitude!!, reminderData.longitude!!, GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencingClient = LocationServices.getGeofencingClient(app.applicationContext)
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "Geofence correctly added")
            }
            addOnFailureListener {
                if (it.message != null) {
                    Log.w(TAG, it.message!!)
                    Log.d(TAG, "ERROR adding the geofence")
                }
            }
        }
    }

    private fun dataToReminderDataItem(): ReminderDTO {
        return ReminderDTO(
            reminderTitle.value,
            reminderDescription.value,
            reminderSelectedLocationStr.value,
            latitude.value,
            longitude.value
        )
    }
}