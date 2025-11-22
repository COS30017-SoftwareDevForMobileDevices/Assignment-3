package com.assignment3.fragments.checkout

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.R
import com.assignment3.adapters.checkout.CheckoutCardAdapter
import com.assignment3.databinding.FragmentCheckoutBinding
import com.assignment3.fragments.cart.CartViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.fragments.profile.ProfileViewModel
import com.assignment3.fragments.profile.shipping.ShippingViewModel
import com.assignment3.models.CartItem
import kotlin.math.round

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private val shippingViewModel: ShippingViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private var subTotal = 0.0
    private var cartItems: ArrayList<CartItem>? = null

    private lateinit var adapter: CheckoutCardAdapter

    // Flags for UI logic
    private var hasShipping = false
    private var hasEnoughBalance = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CheckoutCardAdapter()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnPay.setOnClickListener {
            val userId = authViewModel.firebaseUser?.uid
            if (userId == null) {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val items = adapter.currentList
            if (items.isEmpty()) {
                Toast.makeText(requireContext(), "No items to checkout", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            profileViewModel.updateWallet((subTotal + 10).toLong())
            checkoutViewModel.checkout(items, userId)
        }

        binding.btnChange.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_checkout_to_navigation_shipping)
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

        cartItems = arguments?.getParcelableArrayList("cart_items")

        setupRecycler()
        loadCartItems()
        observeCheckoutState()
        loadUserInfo()
    }


    private fun loadUserInfo() {
        val userId = authViewModel.firebaseUser?.uid ?: return

        shippingViewModel.getShippingAddresses(userId)
        profileViewModel.loadUserProfile()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Shipping collector
                launch {
                    shippingViewModel.shippingUIState.collect { state ->
                        val defaultAddress = state.defaultAddress

                        if (defaultAddress != null) {
                            hasShipping = true

                            binding.shippingInfoContainer.visibility = View.VISIBLE
                            binding.txtNoShipping.visibility = View.GONE

                            binding.txtShippingName.text = defaultAddress.name
                            binding.txtAddress.text = defaultAddress.address
                            binding.txtPhoneNumber.text = defaultAddress.phone

                        } else {
                            hasShipping = false

                            binding.shippingInfoContainer.visibility = View.GONE
                            binding.txtNoShipping.visibility = View.VISIBLE
                        }

                        updatePayButtonState()

                        state.error?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // Wallet Collector
                launch {
                    profileViewModel.user.collect { user ->
                        val balance = user?.walletBalance ?: 0L
                        binding.txtDollarAmount.text = "$$balance"

                        if (balance >= subTotal + 10) {
                            hasEnoughBalance = true
                            binding.txtInsufficient.visibility = View.GONE
                        } else {
                            hasEnoughBalance = false
                            binding.txtInsufficient.visibility = View.VISIBLE
                        }

                        updatePayButtonState()
                    }
                }
            }
        }
    }

    // Centralized Button Logic
    private fun updatePayButtonState() {
        binding.btnPay.isEnabled = hasShipping && hasEnoughBalance
    }


    private fun setupRecycler() {
        binding.recyclerCheckoutItems.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = this@CheckoutFragment.adapter
        }
    }

    private fun loadCartItems() {
        if (cartItems != null && cartItems!!.isNotEmpty()) {
            adapter.submitList(cartItems) {
                binding.recyclerCheckoutItems.requestLayout()
            }
            subTotal = cartItems!!.sumOf { it.product.price * it.quantity }
            binding.txtSubtotalAmount.text = "$${round(subTotal)}"
            binding.txtTotalAmount.text = "$${round(subTotal + 10)}"
        } else {
            binding.btnPay.isEnabled = false
            Toast.makeText(requireContext(), "No cart items received", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeCheckoutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                checkoutViewModel.checkoutUIState.collect { state ->

                    binding.btnPay.isEnabled = !state.isLoading

                    if (state.isSuccess) {
                        Toast.makeText(requireContext(), "Order placed successfully", Toast.LENGTH_SHORT).show()
                        cartViewModel.clearCartItems()
                        checkoutViewModel.resetCheckoutState()
                        findNavController().navigateUp()
                    }

                    state.error?.let {
                        Toast.makeText(requireContext(), "Checkout failed: $it", Toast.LENGTH_LONG).show()
                        checkoutViewModel.resetCheckoutState()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
