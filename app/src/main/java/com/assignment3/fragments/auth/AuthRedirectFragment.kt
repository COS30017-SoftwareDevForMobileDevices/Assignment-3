package com.assignment3.fragments.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.google.firebase.auth.FirebaseAuth

class AuthRedirectFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()

        // Immediately check user state and redirect
        val navController = findNavController()
        val target = if (firebaseAuth.currentUser == null) {
            Log.d("Auth Redirect", "Redirecting to guest")
            R.id.navigation_guest
        } else {
            Log.d("Auth Redirect", "Redirecting to profile")
            R.id.navigation_profile
        }

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.navigation_auth_redirect, true)
            .build()

        navController.navigate(target, null, navOptions)

        return null
    }
}