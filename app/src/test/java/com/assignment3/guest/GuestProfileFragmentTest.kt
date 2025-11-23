package com.assignment3.guest

import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.assignment3.R
import com.assignment3.fragments.guest.GuestProfileFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import androidx.appcompat.app.AppCompatActivity
import org.junit.Assert.assertEquals

@RunWith(RobolectricTestRunner::class)
class GuestProfileFragmentTest {

    @Test
    fun clickingLogin_navigatesToLoginFragment() {
        // Set up a TestNavHostController with the navigation graph
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.mobile_navigation)
        navController.setCurrentDestination(R.id.navigation_guest)

        // Launch GuestProfileFragment in a host activity
        val activityController: ActivityController<AppCompatActivity> =
            Robolectric.buildActivity(AppCompatActivity::class.java).apply {
                get().setTheme(R.style.Theme_Assignment3)
            }.setup()
        val activity = activityController.get()

        val guestFragment = GuestProfileFragment()
        activity.supportFragmentManager.beginTransaction()
            .add(guestFragment, "GuestProfileFragment")
            .commitNow()

        // Attach the TestNavHostController to the fragment's view
        Navigation.setViewNavController(guestFragment.requireView(), navController)

        // Find and click the Login button
        val loginButton: Button = guestFragment.requireView().findViewById(R.id.btn_login)
        loginButton.performClick()

        // The TestNavHostController should now have navigated to the Login destination
        assertEquals(R.id.navigation_login, navController.currentDestination?.id)
    }
}
