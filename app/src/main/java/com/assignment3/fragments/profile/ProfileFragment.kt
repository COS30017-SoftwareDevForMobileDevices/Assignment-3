package com.assignment3.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.assignment3.databinding.FragmentProfileBinding
import com.assignment3.R
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.models.Product
import com.assignment3.models.User
import com.assignment3.repositories.ProfileRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.cardShippingAddress.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_profile_to_navigation_shipping
            )
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load data
        profileViewModel.loadUserProfile()

        // Observe user data
        lifecycleScope.launchWhenStarted {
            profileViewModel.user.collect { user ->
                if (user != null) {
                    binding.txtUserName.text = user.fullName
                    binding.txtUserEmail.text = user.email
                    binding.txtUserBalance.text = "$${NumberFormat.getNumberInstance(Locale.US).format(user.walletBalance)}"
                }
                Log.d("Profile Fragment", user.toString())
            }
        }

        // Observe loading state
        lifecycleScope.launchWhenStarted {
            profileViewModel.loading.collect { isLoading ->
                binding.progressBarBottom.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(R.id.navigation_auth_redirect)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
