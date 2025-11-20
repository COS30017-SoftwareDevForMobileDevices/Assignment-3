package com.assignment3.repositories

import com.assignment3.models.CartItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CheckoutRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun cartItemToOrder(cartItemList: List<CartItem>, userId: String): Result<Boolean> {
        return try {
            if (cartItemList.isEmpty()) return Result.success(false)

            val batch = db.batch()

            // Create order document
            val orderRef = db.collection("orders").document()

            val orderData = mapOf(
                "user_id" to userId,
                "status" to "processing",
                "created_at" to com.google.firebase.Timestamp.now(),
                "order_items" to cartItemList,
            )

            batch.set(orderRef, orderData)

            // Delete all cart items for this user
            val cartSnapshot = db.collection("carts")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            for (doc in cartSnapshot.documents) {
                batch.delete(doc.reference)
            }

            // Commit all operations atomically
            batch.commit().await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
