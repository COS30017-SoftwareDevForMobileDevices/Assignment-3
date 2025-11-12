package com.assignment3.fragments.guest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.assignment3.databinding.FragmentGuestProfileBinding

class GuestProfileFragment : Fragment() {

    private var _binding: FragmentGuestProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val guestProfileViewModel = ViewModelProvider(this)[GuestProfileViewModel::class.java]

        _binding = FragmentGuestProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_guest_to_navigation_login)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_guest_to_navigation_register)
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}