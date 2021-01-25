package com.udacity.project4.locationreminders.reminderslist

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.succeeded
import com.udacity.project4.locationreminders.data.local.FakeRemindersRepository
import com.udacity.project4.locationreminders.testModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest: KoinTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val repository: FakeRemindersRepository by inject()

    private lateinit var auth: FirebaseAuth

    @Before
    fun setup() = runBlockingTest{
        stopKoin()
        startKoin {
            androidContext(getApplicationContext())
            loadKoinModules(testModule)
        }
        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("javicompi@gmail.com", "00726318")
    }

    @After
    fun cleanup() = runBlockingTest {
        repository.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun noData_showNoDataTextView() = runBlockingTest {
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }

    @Test
    fun loadData_showRecyclerView() = runBlockingTest {
        //Set some data
        val reminder1 = ReminderDTO(
            "Title1",
            "Description1",
            "Location1",
            0.0,
            0.0
        )
        val reminder2 = ReminderDTO(
            "Title2",
            "Description2",
            "Location2",
            0.0,
            0.0
        )
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)

        //Get the reminders
        val reminders = repository.getReminders()
        assertThat(reminders.succeeded, `is`(true))

        //Launch the fragment
        launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        //Check reminders are shown
        onView(withText(reminder1.title)).check(matches(isDisplayed()))
        onView(withText(reminder2.title)).check(matches(isDisplayed()))
        repository.deleteAllReminders()
    }

    @Test
    fun addReminderButton_navigateToAddReminder() {
        //Launch the fragment
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        //Get the NavController instance
        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        //Navigate to add reminder and check navigated
        onView(withId(R.id.addReminderFAB)).perform(click())
        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
}