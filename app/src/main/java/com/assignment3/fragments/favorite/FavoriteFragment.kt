package com.assignment3.fragments.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.R
import com.assignment3.adapters.shop.ShopAdapter
import com.assignment3.databinding.FragmentFavoriteBinding
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.interfaces.ShopClickListener
import com.assignment3.models.PRODUCT_FAVORITE_CHECK
import com.assignment3.models.PRODUCT_ID_EXTRA
import com.assignment3.models.Product
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment(), ShopClickListener {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    /**
     * Allows test to inject Fake ViewModelFactory.
     * The app ignores this unless tests set it.
     */
    var testViewModelFactory: ViewModelProvider.Factory? = null

    private val authViewModel: AuthViewModel by viewModels {
        testViewModelFactory ?: defaultViewModelProviderFactory
    }

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        testViewModelFactory ?: defaultViewModelProviderFactory
    }

    private lateinit var adapter: ShopAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ShopAdapter(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
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
            favoriteViewModel.loadFavorites(authViewModel.firebaseUser!!.uid)
        }
    }


    private fun setupRecycler() {
        binding.recyclerViewProducts.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FavoriteFragment.adapter
        }
    }


    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observe auth changes to reload favorites
                launch {
                    authViewModel.firebaseUserFlow.collect { user ->
                        val userId = user?.uid
                        if (userId != null) {
                            favoriteViewModel.loadFavorites(userId)
                            binding.txtNoLogin.visibility = View.GONE
                        } else {
                            favoriteViewModel.clearFavorites()
                            binding.txtNoLogin.visibility = View.VISIBLE
                            adapter.submitList(emptyList())
                        }
                    }
                }

                // Observe UI state from ViewModel
                launch {
                    favoriteViewModel.uiState.collect { state ->
                        adapter.submitList(state.favorites) {
                            binding.progressBarBottom.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                            binding.recyclerViewProducts.requestLayout()
                        }
                    }
                }
            }
        }
    }


    // Product Click Events
    override fun onProductClick(product: Product) {
        findNavController().navigate(
            R.id.action_navigation_favorites_to_navigation_product_detail,
            Bundle().apply {
                putString(PRODUCT_ID_EXTRA, product.productId)
                putBoolean(PRODUCT_FAVORITE_CHECK, product.isFavorite)
            }
        )
    }


    override fun onFavoriteClick(product: Product) {
        val userId = authViewModel.firebaseUser?.uid ?: return
        favoriteViewModel.toggleFavorite(userId, product.productId)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}