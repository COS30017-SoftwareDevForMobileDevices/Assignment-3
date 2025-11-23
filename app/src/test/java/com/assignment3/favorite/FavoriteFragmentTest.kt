package com.assignment3.favorite

import android.view.View
import android.widget.TextView
import com.assignment3.R
import com.assignment3.fragments.favorite.FavoriteFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import androidx.appcompat.app.AppCompatActivity
import com.assignment3.fakes.FakeViewModelFactory
import com.assignment3.fragments.auth.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


@RunWith(RobolectricTestRunner::class)
class FavoriteFragmentTest {

    @Test
    fun showsLoginPromptWhenNoUser() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java).setup()
        val activity = controller.get()

        // Mock AuthViewModel
        val mockAuthVM = mock<AuthViewModel>()

        // Stub firebaseUserFlow to emit null
        whenever(mockAuthVM.firebaseUserFlow).thenReturn(MutableStateFlow(null))

        // Create Fake Factory
        val fakeFactory = FakeViewModelFactory(mockAuthVM)


        val fragment = FavoriteFragment().apply {
            testViewModelFactory = fakeFactory
        }

        activity.supportFragmentManager.beginTransaction()
            .add(fragment, null)
            .commitNow()

        val txt = fragment.requireView().findViewById<TextView>(R.id.txt_no_login)

        assertEquals(View.VISIBLE, txt.visibility)
        assertEquals("Nothing here, please login", txt.text.toString())
    }
}
