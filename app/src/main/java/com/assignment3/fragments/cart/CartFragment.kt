package com.assignment3.fragments.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.R
import com.assignment3.adapters.carts.CartCardAdapter
import com.assignment3.databinding.FragmentCartBinding
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.interfaces.CartClickListener
import com.assignment3.repositories.CartRepository
import kotlinx.coroutines.launch

class CartFragment : Fragment(), CartClickListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val repository: CartRepository = CartRepository()

    private lateinit var adapter: CartCardAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = CartCardAdapter(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.btnCheckout.setOnClickListener {
            val currentCartItems = cartViewModel.cartUIState.value.cartItems
            findNavController().navigate(
                R.id.action_navigation_cart_to_navigation_checkout,
                Bundle().apply {
                    putParcelableArrayList("cart_items", ArrayList(currentCartItems))
                }
            )
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

        setupRecycler()
        observeState()

        // Initial load if logged in
        if (authViewModel.isLoggedIn()) {
            cartViewModel.loadAllCartProducts(authViewModel.firebaseUser!!.uid)
        }
    }


    private fun setupRecycler() {
        binding.recyclerCartItems.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = this@CartFragment.adapter
        }
    }


    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe auth changes
                launch {
                    authViewModel.firebaseUserFlow.collect { user ->
                        if (user?.uid != null) {
                            cartViewModel.loadAllCartProducts(user.uid)
                            binding.txtNoLogin.visibility = View.GONE
                        } else {
                            cartViewModel.clearCartItems()
                            binding.txtNoLogin.visibility = View.VISIBLE
                        }
                    }
                }

                // Observe list data
                launch {
                    cartViewModel.cartUIState.collect { state ->
                        binding.progressBarBottom.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                        adapter.submitList(state.cartItems) {
                            binding.recyclerCartItems.requestLayout()
                        }
                        binding.btnCheckout.isEnabled = !state.isLoading && state.cartItems.isNotEmpty()
                        Log.d("Cart Fragment", state.cartItems.toString())
                    }
                }
            }
        }
    }


    override fun onIncreaseClick(cartItemId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = repository.increaseQuantity(cartItemId)
            if (result.isSuccess) {
                cartViewModel.loadAllCartProducts(authViewModel.firebaseUser!!.uid)
            } else {
                Toast.makeText(requireContext(), "An error occurred, try again later!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDecreaseClick(cartItemId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = repository.decreaseQuantity(cartItemId)
            if (result.isSuccess) {
                cartViewModel.loadAllCartProducts(authViewModel.firebaseUser!!.uid)
            } else {
                Toast.makeText(requireContext(), "An error occurred, try again later!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDeleteClick(cartItemId: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = repository.deleteCartItem(cartItemId)
            if (result.isSuccess) {
                cartViewModel.loadAllCartProducts(authViewModel.firebaseUser!!.uid)
            } else {
                Toast.makeText(requireContext(), "An error occurred, try again later!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}