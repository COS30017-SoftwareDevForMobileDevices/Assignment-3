package com.assignment3.fragments.productdetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.assignment3.databinding.FragmentProductDetailBinding
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.fragments.favorite.FavoriteViewModel
import com.assignment3.models.PRODUCT_FAVORITE_CHECK
import com.assignment3.models.PRODUCT_ID_EXTRA
import com.assignment3.models.Product
import com.assignment3.models.productList
import com.assignment3.repositories.ProductRepository
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val repository = ProductRepository()
    private val authViewModel: AuthViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private var productId: String? = null
    private var isFavorite: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getString(PRODUCT_ID_EXTRA)
        isFavorite = arguments?.getBoolean(PRODUCT_FAVORITE_CHECK)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.txtSizeGuide.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            val sizeDialog = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_size_guide_dialog, null)
            bottomSheetDialog.setContentView(sizeDialog)
            bottomSheetDialog.show()

            val btnCancel = sizeDialog.findViewById<Button>(R.id.btn_cancel)
            btnCancel.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
        }

        binding.btnFavoriteTop.setOnClickListener {
            if (authViewModel.isLoggedIn()) {
                isFavorite = !isFavorite!!
                updateFavoriteIcon()
                favoriteViewModel.toggleFavorite(authViewModel.firebaseUser!!.uid, productId!!)
            } else {
                Toast.makeText(requireContext(), "Login to perform this", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddToCart.setOnClickListener {

        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById<View>(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        productId?.let {
            fetchProductDetails(it)
        } ?: run {
            binding.txtError.text = "No product ID provided"
            binding.txtError.visibility = View.VISIBLE
        }
    }


    private fun fetchProductDetails(id: String) {
        binding.progressBarBottom.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            val product = repository.fetchProductById(id)
            binding.progressBarBottom.visibility = View.GONE
            if (product != null) {
                displayProduct(product)
                binding.txtError.visibility = View.GONE
            } else {
                binding.txtError.visibility = View.VISIBLE
            }
        }
    }


    private fun displayProduct(product: Product) {
        with(binding) {
            updateFavoriteIcon()
            Glide.with(binding.imgProduct.context)
                .load(product.imageUrl)
                .into(binding.imgProduct)
            txtProductName.text = product.name
            txtProductPrice.text = "$${product.price}"
            txtBrand.text = product.brand
            txtProductDescription.text = product.description
        }
    }


    private fun updateFavoriteIcon() {
        if (isFavorite!!) {
            binding.btnFavoriteTop.setImageResource(R.drawable.ic_favorite_fill_red)
        } else {
            binding.btnFavoriteTop.setImageResource(R.drawable.ic_favorite_outline)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

