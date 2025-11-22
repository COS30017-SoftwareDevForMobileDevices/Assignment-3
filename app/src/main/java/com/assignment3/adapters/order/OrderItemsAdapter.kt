package com.assignment3.adapters.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.databinding.ItemOrderProductBinding
import com.assignment3.models.CartItem
import com.squareup.picasso.Picasso

class OrderItemsAdapter(
    private var items: List<CartItem>
) : RecyclerView.Adapter<OrderItemsAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: ItemOrderProductBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.txtViewProductName.text = item.product.name
            binding.txtViewPrice.text = "$${item.product.price * item.quantity}"
            binding.txtViewSize.text = "Size: ${item.size}"
            binding.txtViewQuantity.text = "x${item.quantity}"

            Picasso.get().load(item.product.imageUrl).into(binding.imageViewProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
