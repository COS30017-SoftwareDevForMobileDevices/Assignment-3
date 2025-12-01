package com.assignment3.fragments.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment3.adapters.order.OrderAdapter
import com.assignment3.databinding.FragmentOrderBinding
import com.assignment3.interfaces.OrderClickListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class OrderFragment : Fragment(), OrderClickListener {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()

    private lateinit var buyerAdapter: OrderAdapter
    private lateinit var sellerAdapter: OrderAdapter

    private var currentUserId: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid


        if (currentUserId == null) {
            showLoggedOutState()
            return
        }

        setupAdapters()
        setupRecycler()
        setupTabs()
        setupFab()
        observeData()

        // Load orders
        viewModel.loadBuyerOrders(currentUserId!!)
        viewModel.loadSellerOrders(currentUserId!!)

        // Restore tab selection
        binding.tabLayout.getTabAt(viewModel.tabPosition!!)?.select()
        applyTabState(viewModel.tabPosition!!)
    }

    // UI STATE FOR LOGGED OUT USER
    private fun showLoggedOutState() {
        viewModel.clearAllData()

        binding.recyclerViewOrders.visibility = View.GONE
        binding.fabMarkAll.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.txtNoLogin.visibility = View.VISIBLE
    }


    private fun setupAdapters() {
        buyerAdapter = OrderAdapter(clickListener = null, isSellerView = false)
        sellerAdapter = OrderAdapter(clickListener = this, isSellerView = true)
    }


    private fun setupRecycler() {
        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = buyerAdapter
        }
    }


    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val pos = tab?.position ?: return
                viewModel.tabPosition = pos
                applyTabState(pos)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


    private fun setupFab() {
        binding.fabMarkAll.setOnClickListener {
            showMarkAllConfirmDialog()
        }
    }


    private fun applyTabState(position: Int) {
        when (position) {
            0 -> {
                switchToBuyerAdapter()
                viewModel.applyFilter(OrderFilter.ALL)
                binding.fabMarkAll.visibility = View.GONE
            }
            1 -> {
                switchToBuyerAdapter()
                viewModel.applyFilter(OrderFilter.PENDING)
                binding.fabMarkAll.visibility = View.GONE
            }
            2 -> {
                switchToBuyerAdapter()
                viewModel.applyFilter(OrderFilter.PROCESSING)
                binding.fabMarkAll.visibility = View.GONE
            }
            3 -> {
                switchToSellerAdapter()
                viewModel.applyFilter(OrderFilter.WAITING)
                binding.fabMarkAll.visibility = View.VISIBLE
            }
        }
    }


    private fun switchToBuyerAdapter() {
        if (binding.recyclerViewOrders.adapter != buyerAdapter) {
            binding.recyclerViewOrders.adapter = buyerAdapter
        }
    }


    private fun switchToSellerAdapter() {
        if (binding.recyclerViewOrders.adapter != sellerAdapter) {
            binding.recyclerViewOrders.adapter = sellerAdapter
        }
    }


    // Observe ViewModel data
    private fun observeData() {
        viewModel.filteredOrders.observe(viewLifecycleOwner) { orders ->
            val currentFilter = viewModel.currentFilter.value

            if (currentFilter == OrderFilter.WAITING) {
                sellerAdapter.submitList(orders)
            } else {
                buyerAdapter.submitList(orders)
            }

            binding.txtNoOrder.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess && it.getOrNull() == true) {
                    Toast.makeText(requireContext(), "Order status updated!", Toast.LENGTH_SHORT).show()
                } else if (it.isFailure) {
                    Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
                }
                viewModel.clearUpdateResult()
            }
        }
    }


    // Status Update Dialog
    override fun onStatusClick(orderId: String) {
        showStatusConfirmDialog(orderId)
    }


    private fun showStatusConfirmDialog(orderId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Update Order Status")
            .setMessage("Mark this order as processing?")
            .setPositiveButton("Confirm") { dialog, _ ->
                currentUserId?.let { uid ->
                    viewModel.markOrderAsProcessing(orderId, uid)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    private fun showMarkAllConfirmDialog() {
        val pendingCount = viewModel.sellerOrders.value?.size ?: 0

        if (pendingCount == 0) {
            Toast.makeText(requireContext(), "No pending orders to process", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Mark All as Processing")
            .setMessage("Update all $pendingCount pending orders to processing?")
            .setPositiveButton("Confirm") { dialog, _ ->
                currentUserId?.let { uid ->
                    viewModel.markAllOrdersAsProcessing(uid)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
