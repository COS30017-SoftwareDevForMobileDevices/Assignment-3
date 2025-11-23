package com.assignment3

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainFragmentsTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun homeFragment_displaysProductGrid() {
        // Check that the product RecyclerView is displayed on Home screen.
        onView(withId(R.id.recycler_view_products))
            .check(matches(isDisplayed()))
    }


    @Test
    fun cartFragment_showsLoginPrompt_whenNotLoggedIn() {
        onView(withId(R.id.navigation_cart)).perform(click())

        // Since no user is logged in, CartFragment should show a "please login" placeholder
        onView(withText(R.string.txt_no_login_placeholder))
            .check(matches(isDisplayed()))

        // The Checkout button should be disabled when cart is empty (no items)
        onView(withId(R.id.btn_checkout))
            .check(matches(not(isEnabled())))
    }


    @Test
    fun favoriteFragment_showsLoginPrompt_whenNotLoggedIn() {
        onView(withId(R.id.navigation_favorites)).perform(click())

        // FavoritesFragment should prompt login for guest users
        onView(withText(R.string.txt_no_login_placeholder))
            .check(matches(isDisplayed()))
    }


    @Test
    fun orderFragment_showsNoOrderMessage_whenEmpty() {
        onView(withId(R.id.navigation_order)).perform(click())

        // Without any orders, it should show "No Order Yet"
        onView(withText(R.string.txt_no_order))
            .check(matches(isDisplayed()))
    }


    @Test
    fun guestProfile_navigatesToLogin() {
        // Click on Profile tab (which triggers AuthRedirect to GuestProfile for non-logged-in user)
        onView(withId(R.id.navigation_auth_redirect)).perform(click())

        // Should be on GuestProfileFragment now, where Login and Register buttons are shown
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))

        // Simulate clicking the Login button
        onView(allOf(withId(R.id.btn_login), isDisplayed())).perform(click())
        onView(withId(R.id.edit_email)).check(matches(isDisplayed()))
    }
}
