package com.assignment3.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.databinding.FragmentHomeBinding
import com.assignment3.models.Product
import com.assignment3.R
import com.assignment3.adapters.CardAdapter
import com.assignment3.fragments.productdetail.ProductDetailFragment
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.productList
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
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        populateProducts()

        val homeFragment = this
        _binding?.recyclerViewProducts?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = CardAdapter(productList, homeFragment)
        }

        return root
    }


    override fun onClick(product: Product) {
        findNavController().navigate(
            R.id.action_homeFragment_to_productDetailFragment,
            Bundle().apply {
                putInt(PRODUCT_ID_EXTRA, product.productId)
            }
        )
    }


    private fun populateProducts() {
        productList.clear()

        productList.add(
            Product(R.drawable.sneaker, "Jordan 1 High", 239.54, productList.size)
        )
        productList.add(
            Product(R.drawable.sneaker, "Jordan 2 High", 239.54, productList.size)
        )
        productList.add(
            Product(R.drawable.sneaker, "Jordan 3 High", 239.54, productList.size)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}