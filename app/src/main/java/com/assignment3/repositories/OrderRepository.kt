package com.assignment3.repositories

import android.util.Log
import com.assignment3.models.CartItem
import com.assignment3.models.OrderItem
import com.assignment3.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getAllOrderProducts(userId: String): List<OrderItem> {
        return try {
            val orderDocs = db.collection("orders")
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("OrderRepo", "Fetched ${orderDocs.documents.size} orders")

            orderDocs.documents.mapNotNull { doc ->
                val orderId = doc.id
                val status = doc.getString("status") ?: return@mapNotNull null

                val orderItemsRaw = doc.get("order_items") as? List<Map<String, Any>> ?: emptyList()

                val orderItems = orderItemsRaw.map { item ->

                    val productMap = item["product"] as Map<String, Any>

                    val product = Product(
                        name = productMap["name"] as String,
                        brand = productMap["brand"] as String,
                        category = productMap["category"] as String,
                        imageUrl = productMap["image_url"] as String,
                        price = (productMap["price"] as Number).toDouble()
                    )

                    CartItem(
                        cartId = item["cart_id"] as String,
                        product = product,
                        quantity = (item["quantity"] as Number).toInt(),
                        size = (item["size"] as Number).toDouble()
                    )
                }

                OrderItem(
                    orderId = orderId,
                    orderItems = orderItems,
                    status = status
                )
            }

        } catch (e: Exception) {
            Log.e("OrderRepo", "Failed to fetch: ${e.message}")
            emptyList()
        }
    }
}
