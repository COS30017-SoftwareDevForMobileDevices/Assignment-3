package com.assignment3.repositories

import android.util.Log
import com.assignment3.models.CartItem
import com.assignment3.models.OrderItem
import com.assignment3.models.Product
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class OrderRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getAllOrderProducts(userId: String): List<OrderItem> {
        return try {
            val orderDocs = db.collection("orders")
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("OrderRepo", "Fetched ${orderDocs.documents.size} orders for user $userId")

            orderDocs.documents.mapNotNull { doc ->
                val status = doc.getString("status") ?: run {
                    Log.w("OrderRepo", "Order ${doc.id} missing status")
                    return@mapNotNull null
                }

                val firebaseTimestamp: Timestamp = doc.getTimestamp("created_at")!!
                val date = firebaseTimestamp.toDate()

                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val formattedDate = outputFormat.format(date)


                // order_items is an array
                val rawItems = doc.get("order_items") as? List<Map<String, Any>> ?: emptyList()

                val cartItems = rawItems.mapNotNull { itemMap ->
                    try {
                        val cartId = itemMap["cart_id"] as? String ?: ""
                        val quantity = (itemMap["quantity"] as? Long ?: 0L).toInt()
                        val size = (itemMap["size"] as? Long ?: 0L).toDouble()

                        // product is a map
                        val productMap = itemMap["product"] as? Map<String, Any>

                        // Map productMap to the Product model.
                         val product = Product(
                             productId = productMap?.get("productId") as? String ?: "",
                             name = productMap?.get("name") as? String ?: "",
                             brand = productMap?.get("brand") as? String ?: "",
                             price = productMap?.get("price") as? Double ?: 0.0,
                             imageUrl = productMap?.get("image_url") as? String ?: ""
                         )

                         CartItem(cartId, product, quantity, size)
                    } catch (e: Exception) {
                        Log.w("OrderRepo", "Error mapping order_items element: ${e.message}")
                        null
                    }
                }

                OrderItem(
                    orderId = doc.id,
                    items = cartItems,
                    status = status,
                    createAt = formattedDate
                )
            }
        } catch (e: Exception) {
            Log.e("Order Repo", "Failed to fetch orders: ${e.message}", e)
            emptyList()
        }
    }
}
