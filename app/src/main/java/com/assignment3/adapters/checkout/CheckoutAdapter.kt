package com.assignment3.adapters.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.ProductCheckoutBinding
import com.assignment3.models.CartItem

class CheckoutCardAdapter() : ListAdapter<CartItem, CheckoutViewHolder>(CheckoutDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ProductCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bindProductCheckout(getItem(position))
    }
}

class CheckoutDiffCallBack : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
        oldItem.cartId == newItem.cartId

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
        oldItem == newItem
}