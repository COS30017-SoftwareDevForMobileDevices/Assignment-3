package com.assignment3.adapters.carts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.ProductCartBinding
import com.assignment3.interfaces.CartClickListener
import com.assignment3.models.CartItem

class CartCardAdapter(
    private val clickListener: CartClickListener
) : ListAdapter<CartItem, CartCardViewHolder>(CartDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartCardViewHolder {
        val binding = ProductCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartCardViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: CartCardViewHolder, position: Int) {
        holder.bindProductCart(getItem(position))
    }
}

class CartDiffCallBack : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
        oldItem.cartId == newItem.cartId

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
        oldItem == newItem
}