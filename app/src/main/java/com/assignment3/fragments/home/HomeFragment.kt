package com.assignment3.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.FragmentHomeBinding
import com.assignment3.models.Product
import com.assignment3.R
import com.assignment3.adapters.products.ProductCardAdapter
import com.assignment3.fragments.shop.ShopViewModel
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.PRODUCT_ID_EXTRA

class HomeFragment : Fragment(), ProductClickListener {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
//
//        val viewModel = ViewModelProvider(this)[ShopViewModel::class.java]
//        val adapter = ProductCardAdapter(this)
//
//        binding.recyclerViewProducts.apply {
//            layoutManager = GridLayoutManager(requireContext(), 2)
//            this.adapter = adapter
//        }
//
//        // Observe data
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.uiState.collect { state ->
//                adapter.submitList(state.products)
//            }
//        }
//
//        // Initial load
//        viewModel.loadMoreProducts()
//
//        // Scroll listener for pagination
//        binding.recyclerViewProducts.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val layoutManager = recyclerView.layoutManager as GridLayoutManager
//                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
//                if (lastVisible == adapter.itemCount - 1) {
//                    viewModel.loadMoreProducts()
//                }
//            }
//        })
        return root
    }


    override fun onClick(product: Product) {
        findNavController().navigate(
            R.id.action_navigation_home_to_navigation_product_detail,
            Bundle().apply {
                putString(PRODUCT_ID_EXTRA, product.productId)
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}