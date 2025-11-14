package com.assignment3.fragments.shop

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.R
import com.assignment3.adapters.products.ProductCardAdapter
import com.assignment3.databinding.FragmentShopBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.PRODUCT_ID_EXTRA
import com.assignment3.models.Product

class ShopFragment : Fragment(), ProductClickListener {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!
    private val adapter = ProductCardAdapter(this)

    private val viewModel: ShopViewModel by viewModels()
    private var savedRecyclerViewState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupObservers()
        setupPagination()

        // Restore state after the layoutManager is set
        savedRecyclerViewState?.let { state ->
            binding.recyclerViewProducts.layoutManager?.onRestoreInstanceState(state)
        }

        return binding.root
    }

    // Reusable RecyclerView with reusable adapter for performance
    private fun setupRecyclerView() {
        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@ShopFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.products)
            }
        }
    }

    private fun setupPagination() {
        binding.recyclerViewProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Skip if scrolling up
                if (dy <= 0) return

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Load if 3 rows of products passed
                if (lastVisibleItemPosition >= totalItemCount) {
                    Log.d("Shop Fragment", "Load more...")
                    viewModel.loadMoreProducts()
                }
            }
        })
    }


    override fun onClick(product: Product) {
        findNavController().navigate(
            R.id.action_navigation_shop_to_navigation_product_detail,
            Bundle().apply {
                putString(PRODUCT_ID_EXTRA, product.productId)
            }
        )
    }


    override fun onDestroyView() {
        // Save state before destruction
        savedRecyclerViewState = binding.recyclerViewProducts.layoutManager?.onSaveInstanceState()
        super.onDestroyView()
        _binding = null
    }
}