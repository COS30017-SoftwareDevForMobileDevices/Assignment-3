package com.assignment3.fragments.resetpassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.assignment3.databinding.FragmentCartBinding
import com.assignment3.databinding.FragmentResetPasswordBinding
import com.assignment3.fragments.cart.CartViewModel
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSubmit.setOnClickListener {
            val email = binding.editEmailReset.text.toString().trim()
            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "If an account exists for this email, a password reset link has been sent.", Toast.LENGTH_LONG).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(requireContext(), it.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter your email address", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}