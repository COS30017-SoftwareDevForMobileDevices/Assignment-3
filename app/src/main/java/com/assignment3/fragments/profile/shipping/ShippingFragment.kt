package com.assignment3.fragments.profile.shipping

import android.content.DialogInterface
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.assignment3.adapters.shipping.ShippingCardAdapter
import com.assignment3.databinding.FragmentShippingBinding
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.R
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.interfaces.ShippingClickListener
import com.assignment3.models.ShippingAddress
import com.google.android.material.textfield.TextInputEditText
import com.google.type.Color

class ShippingFragment : Fragment(), ShippingClickListener {

    private var _binding: FragmentShippingBinding? = null
    private val binding get() = _binding!!
    private val shippingViewModel: ShippingViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var adapter: ShippingCardAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ShippingCardAdapter(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // FAB click listener
        binding.fabAddAddress.setOnClickListener {
            showAddShippingDialog()
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val userId = authViewModel.firebaseUser?.uid
        if (userId != null) {
            shippingViewModel.getShippingAddresses(userId)
        }

        setupRecycler()
        observeState()
        observeCheckoutState()
    }


    private fun showAddShippingDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_address_information, null)

        val editName = dialogView.findViewById<TextInputEditText>(R.id.edit_name)
        val editAddress = dialogView.findViewById<TextInputEditText>(R.id.edit_address)
        val editPhone = dialogView.findViewById<TextInputEditText>(R.id.edit_phone)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Shipping Address")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = editName.text.toString().trim()
                val address = editAddress.text.toString().trim()
                val phone = editPhone.text.toString().trim()

                // Validation
                if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Get user ID and save
                val userId = authViewModel.firebaseUser?.uid
                if (userId != null) {
                    shippingViewModel.addShippingAddress(userId, ShippingAddress("", name, address, phone))

                    Log.d("Shipping Fragment", "Saving: $name, $address, $phone")
                    Toast.makeText(requireContext(), "Shipping address added", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun setupRecycler() {
        binding.recyclerViewShipping.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = this@ShippingFragment.adapter
        }
    }


    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shippingViewModel.shippingUIState.collect { state ->
                    Log.d("Shipping Fragment", state.addresses.toString())
                    binding.progressBarBottom.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    adapter.submitList(state.addresses) {
                        binding.recyclerViewShipping.requestLayout()
                    }
                }
            }
        }
    }


    private fun observeCheckoutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shippingViewModel.shippingUIState.collect { state ->
                    if (state.isLoading) {
                        Log.d("Shipping Fragment", "Update shipping in progress...")
                    }

                    state.error?.let { err ->
                        Toast.makeText(requireContext(), "Shipping update failed: $err", Toast.LENGTH_LONG).show()
                        shippingViewModel.resetShippingState()
                    }
                }
            }
        }
    }


    override fun onDeleteClick(shippingId: String) {
        val userId = authViewModel.firebaseUser?.uid ?: return
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Address")
        builder.setMessage("Are you sure you want to delete this address?")

        builder.setPositiveButton("Yes") { dialog: DialogInterface, which: Int ->
            shippingViewModel.deleteShippingAddress(userId, shippingId)
            Toast.makeText(requireContext(), "Delete confirmed!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onUpdateClick(address: ShippingAddress) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_address_information, null)

        val editName = dialogView.findViewById<TextInputEditText>(R.id.edit_name)
        val editAddress = dialogView.findViewById<TextInputEditText>(R.id.edit_address)
        val editPhone = dialogView.findViewById<TextInputEditText>(R.id.edit_phone)

        editName.setText(address.name)
        editAddress.setText(address.address)
        editPhone.setText(address.phone)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Shipping Address")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val name = editName.text.toString().trim()
                val userAddress = editAddress.text.toString().trim()
                val phone = editPhone.text.toString().trim()

                // Validation
                if (name.isEmpty() || userAddress.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Get user ID and save
                val userId = authViewModel.firebaseUser?.uid
                if (userId != null) {
                    shippingViewModel.updateShippingAddress(userId, ShippingAddress(address.shippingId, name, userAddress, phone))

                    Log.d("Shipping Fragment", "Updated: ${address.shippingId}, $name, $userAddress, $phone")
                    Toast.makeText(requireContext(), "Shipping address updated", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDefaultClick(shippingId: String) {
        // Get user ID and set default
        val userId = authViewModel.firebaseUser?.uid
        if (userId != null) {
            shippingViewModel.setDefaultAddress(userId, shippingId)

            shippingViewModel.setDefaultState.observe(viewLifecycleOwner) { success ->
                if (success) {
                    val recyclerShippingAddress = LayoutInflater.from(requireContext())
                        .inflate(R.layout.shipping_information, null)
                    val shippingContainer = recyclerShippingAddress.findViewById<ConstraintLayout>(R.id.shipping_container)
                    shippingContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_default))

                    Toast.makeText(context, "Default address updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update default address", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}