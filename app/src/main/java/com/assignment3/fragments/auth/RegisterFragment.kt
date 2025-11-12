package com.assignment3.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.assignment3.databinding.FragmentRegisterBinding
import com.assignment3.fragments.guest.GuestProfileFragment

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val previousFragmentId = findNavController().previousBackStackEntry?.destination?.id
        if (findNavController().previousBackStackEntry != null && previousFragmentId != R.id.navigation_guest) {
            binding.txtLogin.setOnClickListener {
                findNavController().navigateUp()
            }
        } else {
            binding.txtLogin.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
            }
        }


        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById<View>(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}