package com.assignment3.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.assignment3.databinding.FragmentRegisterBinding
import kotlin.getValue

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }


        // Handle register logic
        binding.btnRegister.setOnClickListener {
            val fullName = binding.editName.text.toString()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val confirmPassword = binding.editConfirmPassword.text.toString()

            // Validation checking
            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    viewModel.register(fullName, email, password)
                    viewModel.registerResult.observe(viewLifecycleOwner) { result ->
                        result.onSuccess {
                            findNavController().navigate(R.id.action_navigation_register_to_navigation_login)
                        }
                        result.onFailure { e ->
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }


        // Avoid nested navigation between login and register
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