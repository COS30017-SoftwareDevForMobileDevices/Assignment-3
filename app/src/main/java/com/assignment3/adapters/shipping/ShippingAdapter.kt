package com.assignment3.adapters.shipping

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.assignment3.databinding.ShippingInformationBinding
import com.assignment3.interfaces.ShippingClickListener
import com.assignment3.models.ShippingAddress

class ShippingCardAdapter(
    private val clickListener: ShippingClickListener
) : ListAdapter<ShippingAddress, ShippingViewHolder>(ShippingDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingViewHolder {
        val binding = ShippingInformationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShippingViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: ShippingViewHolder, position: Int) {
        holder.bindShippingInformation(getItem(position))
    }
}

class ShippingDiffCallBack : DiffUtil.ItemCallback<ShippingAddress>() {
    override fun areItemsTheSame(oldItem: ShippingAddress, newItem: ShippingAddress): Boolean =
        oldItem.shippingId == newItem.shippingId

    override fun areContentsTheSame(oldItem: ShippingAddress, newItem: ShippingAddress): Boolean =
        oldItem == newItem
}