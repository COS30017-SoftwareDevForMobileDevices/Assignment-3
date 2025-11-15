package com.assignment3.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.databinding.FragmentHomeBinding
import com.assignment3.models.Product
import com.assignment3.R
import com.assignment3.adapters.products.ProductCardAdapter
import com.assignment3.adapters.products.ProductCardViewHolder
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.fragments.favorite.FavoriteViewModel
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.PRODUCT_ID_EXTRA
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ProductClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var adapter: ProductCardAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ProductCardAdapter(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeUiState()
    }


    private fun setupRecyclerView() {
        binding.recyclerViewProducts.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = this@HomeFragment.adapter
        }
    }


    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.uiState.collect { state ->
                    adapter.submitList(state.products.toList()) {
                        binding.recyclerViewProducts.requestLayout()
                        binding.progressBarBottom.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }


    override fun onProductClick(product: Product) {
        findNavController().navigate(
            R.id.action_navigation_home_to_navigation_product_detail,
            Bundle().apply {
                putString(PRODUCT_ID_EXTRA, product.productId)
            }
        )
    }


    override fun onFavoriteClick(product: Product) {
        if (authViewModel.isLoggedIn()) {
            favoriteViewModel.addProductToFavorite(authViewModel.firebaseUser!!.uid, product.productId)


        } else {
            Toast.makeText(requireContext(), "Login to perform this", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}