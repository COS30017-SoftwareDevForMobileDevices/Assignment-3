package com.assignment3.fragments.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.R
import com.assignment3.adapters.orders.OrderCardAdapter
import com.assignment3.databinding.FragmentOrderBinding
import com.assignment3.fragments.auth.AuthViewModel
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val orderViewModel: OrderViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var adapter: OrderCardAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = OrderCardAdapter()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
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
            orderViewModel.loadAllOrderProducts(authViewModel.firebaseUser!!.uid)
        }
    }


    private fun setupRecycler() {
        binding.recyclerViewOrders.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = this@OrderFragment.adapter
        }
    }


    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observe login changes
                launch {
                    authViewModel.firebaseUserFlow.collect { user ->
                        if (user?.uid != null) {
                            orderViewModel.loadAllOrderProducts(user.uid)
                            binding.txtNoLogin.visibility = View.GONE
                        } else {
                            orderViewModel.clearOrders()
                            binding.txtNoLogin.visibility = View.VISIBLE
                        }
                    }
                }

                // Observe order list
                launch {
                    orderViewModel.orderUIState.collect { state ->

                        binding.progressBar.visibility =
                            if (state.isLoading) View.VISIBLE else View.GONE

                        adapter.submitList(state.orderItems) {
                            binding.recyclerViewOrders.requestLayout()
                        }

                        // Show “No orders yet”
                        binding.txtNoOrder.visibility =
                            if (!state.isLoading && state.orderItems.isEmpty()) View.VISIBLE
                            else View.GONE

                        Log.d("Order Fragment", state.orderItems.toString())
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
