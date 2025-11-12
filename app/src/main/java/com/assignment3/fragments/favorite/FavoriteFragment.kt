package com.assignment3.fragments.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.R
import com.assignment3.adapters.favorites.FavoriteCardAdapter
import com.assignment3.databinding.FragmentFavoriteBinding
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.PRODUCT_ID_EXTRA
import com.assignment3.models.Product
import com.assignment3.models.productList

class FavoriteFragment : Fragment(), ProductClickListener {

    private var _binding: FragmentFavoriteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favoriteViewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root


        populateProducts()

        val favoriteFragment = this
        _binding?.recyclerViewProducts?.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = FavoriteCardAdapter(productList, favoriteFragment)
        }


        return root
    }


    override fun onClick(product: Product) {
        findNavController().navigate(
            R.id.action_navigation_favorites_to_navigation_product_detail,
            Bundle().apply {
                putInt(PRODUCT_ID_EXTRA, product.productId)
            }
        )
    }


    private fun populateProducts() {
        productList.clear()

        productList.add(Product(R.drawable.sneaker, "Jordan 1 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 2 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 3 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 4 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 5 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 6 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 7 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 8 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 9 High", 239.54, productList.size))
        productList.add(Product(R.drawable.sneaker, "Jordan 10 High", 239.54, productList.size))
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}