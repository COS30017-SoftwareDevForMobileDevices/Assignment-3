package com.assignment3.fragments.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment3.adapters.order.OrderAdapter
import com.assignment3.databinding.FragmentOrderBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        setupTabs()
        observeData()

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.loadOrders(uid)
    }


    private fun setupRecycler() {
        orderAdapter = OrderAdapter()
        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }


    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.applyFilter(OrderFilter.ALL)
                    1 -> viewModel.applyFilter(OrderFilter.PENDING)
                    2 -> viewModel.applyFilter(OrderFilter.PROCESSING)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


    private fun observeData() {
        viewModel.filteredOrders.observe(viewLifecycleOwner) {
            orderAdapter.submitList(it)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
