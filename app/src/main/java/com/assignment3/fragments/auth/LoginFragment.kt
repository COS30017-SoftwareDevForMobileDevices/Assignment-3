package com.assignment3.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.assignment3.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            // Validation checking
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
                viewModel.loginResult.observe(viewLifecycleOwner) { result ->
                    result.onSuccess {
                        findNavController().navigate(R.id.navigation_profile)
                    }
                    result.onFailure { e ->
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }


        binding.txtForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_reset_password)
        }


        // Avoid nested navigation between login and register
        val previousFragmentId = findNavController().previousBackStackEntry?.destination?.id
        if (findNavController().previousBackStackEntry != null && previousFragmentId != R.id.navigation_guest) {
            binding.txtRegister.setOnClickListener {
                findNavController().navigateUp()
            }
        } else {
            binding.txtRegister.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_login_to_navigation_register)
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}