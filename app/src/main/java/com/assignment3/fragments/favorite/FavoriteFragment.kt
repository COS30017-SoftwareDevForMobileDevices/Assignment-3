package com.assignment3.fragments.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.assignment3.databinding.FragmentFavoriteBinding

//ProductClickListener
class FavoriteFragment : Fragment() {

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


//        populateProducts()
//
//        val favoriteFragment = this
//        binding.recyclerViewProducts.apply {
//            layoutManager = GridLayoutManager(requireContext(), 2)
//            adapter = FavoriteCardAdapter(productList, favoriteFragment)
//        }


        return root
    }


//    override fun onClick(product: Product) {
//        findNavController().navigate(
//            R.id.action_navigation_favorites_to_navigation_product_detail,
//            Bundle().apply {
//                putInt(PRODUCT_ID_EXTRA, product.productId)
//            }
//        )
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}