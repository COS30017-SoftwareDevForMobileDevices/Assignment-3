package com.assignment3.fragments.productdetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.assignment3.R
import com.assignment3.databinding.FragmentProductDetailBinding
import com.assignment3.models.PRODUCT_ID_EXTRA
import com.assignment3.models.Product
import com.assignment3.models.productList

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val productDetailViewModel = ViewModelProvider(this)[ProductDetailViewModel::class.java]

        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arguments?.let {
            val productId = it.getInt(PRODUCT_ID_EXTRA)
            val product = productFromId(productId)

            Log.d("Product Detail", "Product ID: $productId")

            if (product != null) {
                binding.imgProduct.setImageResource(product.imageUrl)
                binding.txtProductName.text = product.name
                binding.txtProductPrice.text = "$" + product.price.toString()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return root
    }


    private fun productFromId(productId: Int) : Product? {
        for (product in productList) {
            if (product.productId == productId) {
                return product
            }
        }
        return null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById<View>(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

