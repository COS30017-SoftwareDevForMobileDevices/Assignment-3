package com.assignment3.repositories

import com.assignment3.models.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CheckoutRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun cartItemToOrder(
        cartItemList: List<CartItem>,
        userId: String,
        buyerName: String,
        buyerAddress: String,
        buyerPhone: String
    ): Result<Boolean> {
        return try {
            if (cartItemList.isEmpty()) return Result.success(false)

            val batch = db.batch()

            // Group cart items by seller (product owner)
            val itemsBySeller = cartItemList.groupBy { it.product.ownerId }

            // Create separate order for each seller
            for ((sellerId, sellerItems) in itemsBySeller) {
                val orderRef = db.collection("orders").document()

                val orderData = mapOf(
                    "user_id" to userId,
                    "seller_id" to sellerId,
                    "buyer_name" to buyerName,
                    "buyer_address" to buyerAddress,
                    "buyer_phone" to buyerPhone,
                    "status" to "pending",
                    "created_at" to com.google.firebase.Timestamp.now(),
                    "order_items" to sellerItems
                )

                batch.set(orderRef, orderData)
            }

            // Delete all cart items for this user
            val cartSnapshot = db.collection("carts")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            for (doc in cartSnapshot.documents) {
                batch.delete(doc.reference)
            }

            batch.commit().await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}