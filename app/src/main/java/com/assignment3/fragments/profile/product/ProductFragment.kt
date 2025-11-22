package com.assignment3.fragments.profile.product

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment3.R
import com.assignment3.adapters.product.ProductAdapter
import com.assignment3.databinding.FragmentProductBinding
import com.assignment3.fragments.auth.AuthViewModel
import com.assignment3.interfaces.ProductClickListener
import com.assignment3.models.Product
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProductFragment : Fragment(), ProductClickListener {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ProductAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.fabAddProduct.setOnClickListener {
            showAddProductDialog()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        val userId = authViewModel.firebaseUser?.uid
        if (userId != null) {
            productViewModel.loadUserProducts(userId)
        }

        setupRecycler()
        observeState()
    }

    private fun setupRecycler() {
        binding.recyclerViewProduct.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 1)
            adapter = this@ProductFragment.adapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productUIState.collect { state ->
                    binding.progressBarBottom.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    adapter.submitList(state.products) {
                        binding.recyclerViewProduct.requestLayout()
                    }

                    state.error?.let { err ->
                        Toast.makeText(requireContext(), err, Toast.LENGTH_SHORT).show()
                        productViewModel.resetError()
                    }
                }
            }
        }
    }

    private fun showAddProductDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_manage_product, null)

        val editName = dialogView.findViewById<TextInputEditText>(R.id.edit_name)
        val editBrand = dialogView.findViewById<TextInputEditText>(R.id.edit_brand)
        val editPrice = dialogView.findViewById<TextInputEditText>(R.id.edit_price)
        val editDesc = dialogView.findViewById<TextInputEditText>(R.id.edit_desc)
        val editImage = dialogView.findViewById<TextInputEditText>(R.id.edit_image)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Product")
            .setView(dialogView)
            .setPositiveButton("Add") { dialog, _ ->
                val name = editName.text.toString().trim()
                val brand = editBrand.text.toString().trim()
                val priceStr = editPrice.text.toString().trim()
                val desc = editDesc.text.toString().trim()
                val imageUrl = editImage.text.toString().trim()

                if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceStr.toDoubleOrNull()
                if (price == null || price <= 0) {
                    Toast.makeText(requireContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val userId = authViewModel.firebaseUser?.uid
                if (userId != null) {
                    val product = Product(
                        name = name,
                        brand = brand,
                        price = price,
                        description = desc,
                        imageUrl = imageUrl
                    )
                    productViewModel.addProduct(userId, product)
                    Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onUpdateClick(product: Product) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_manage_product, null)

        val editName = dialogView.findViewById<TextInputEditText>(R.id.edit_name)
        val editBrand = dialogView.findViewById<TextInputEditText>(R.id.edit_brand)
        val editPrice = dialogView.findViewById<TextInputEditText>(R.id.edit_price)
        val editDesc = dialogView.findViewById<TextInputEditText>(R.id.edit_desc)
        val editImage = dialogView.findViewById<TextInputEditText>(R.id.edit_image)

        editName.setText(product.name)
        editBrand.setText(product.brand)
        editPrice.setText(product.price.toString())
        editDesc.setText(product.description)
        editImage.setText(product.imageUrl)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Product")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val name = editName.text.toString().trim()
                val brand = editBrand.text.toString().trim()
                val priceStr = editPrice.text.toString().trim()
                val desc = editDesc.text.toString().trim()
                val imageUrl = editImage.text.toString().trim()

                if (name.isEmpty() || brand.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceStr.toDoubleOrNull()
                if (price == null || price <= 0) {
                    Toast.makeText(requireContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val userId = authViewModel.firebaseUser?.uid
                if (userId != null) {
                    val updatedProduct = Product(
                        productId = product.productId,
                        ownerId = product.ownerId,
                        name = name,
                        brand = brand,
                        price = price,
                        description = desc,
                        imageUrl = imageUrl
                    )
                    productViewModel.updateProduct(userId, updatedProduct)
                    Toast.makeText(requireContext(), "Product updated successfully", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDeleteClick(productId: String) {
        val userId = authViewModel.firebaseUser?.uid ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Yes") { dialog: DialogInterface, _ ->
                productViewModel.deleteProduct(userId, productId)
                Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog: DialogInterface, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}