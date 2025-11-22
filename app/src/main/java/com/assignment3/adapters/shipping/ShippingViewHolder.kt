package com.assignment3.adapters.shipping

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.assignment3.R
import com.assignment3.databinding.ShippingInformationBinding
import com.assignment3.interfaces.ShippingClickListener
import com.assignment3.models.ShippingAddress

class ShippingViewHolder(
    private val binding: ShippingInformationBinding,
    private val clickListener: ShippingClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bindShippingInformation(shipping: ShippingAddress) = with(binding) {
        txtRecipientName.text = shipping.name
        txtAddressLine.text = shipping.address
        txtPhone.text = shipping.phone

        if (shipping.isDefault) {
            shippingContainer.setBackgroundColor(ContextCompat.getColor(root.context, R.color.card_default))
        } else {
            shippingContainer.setBackgroundColor(ContextCompat.getColor(root.context, R.color.white))
        }

        btnEdit.setOnClickListener {
            clickListener.onUpdateClick(shipping)
        }

        btnDelete.setOnClickListener {
            clickListener.onDeleteClick(shipping.shippingId)
        }

        shippingContainer.setOnClickListener {
            clickListener.onDefaultClick(shipping.shippingId)
        }
    }
}