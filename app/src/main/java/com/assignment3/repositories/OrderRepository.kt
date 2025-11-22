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


    // Get orders where user is the buyer
    suspend fun getAllOrderProducts(userId: String): List<OrderItem> {
        return try {
            val orderDocs = db.collection("orders")
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("Order Repo", "Fetched ${orderDocs.documents.size} orders for buyer $userId")
            parseOrderDocuments(orderDocs.documents)
        } catch (e: Exception) {
            Log.e("Order Repo", "Failed to fetch buyer orders: ${e.message}", e)
            emptyList()
        }
    }


    // Get orders where user is the seller (products they own being purchased)
    suspend fun getSellerOrders(sellerId: String): List<OrderItem> {
        return try {
            val orderDocs = db.collection("orders")
                .whereEqualTo("seller_id", sellerId)
                .whereEqualTo("status", "pending")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("Order Repo", "Fetched ${orderDocs.documents.size} pending orders for seller $sellerId")
            parseOrderDocuments(orderDocs.documents)
        } catch (e: Exception) {
            Log.e("Order Repo", "Failed to fetch seller orders: ${e.message}", e)
            emptyList()
        }
    }


    // Update single order status
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Boolean> {
        return try {
            db.collection("orders")
                .document(orderId)
                .update("status", newStatus)
                .await()

            Log.d("Order Repo", "Updated order $orderId to $newStatus")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("Order Repo", "Failed to update order status: ${e.message}", e)
            Result.failure(e)
        }
    }


    // Update multiple orders status at once
    suspend fun updateAllOrdersStatus(orderIds: List<String>, newStatus: String): Result<Boolean> {
        return try {
            val batch = db.batch()

            for (orderId in orderIds) {
                val docRef = db.collection("orders").document(orderId)
                batch.update(docRef, "status", newStatus)
            }

            batch.commit().await()
            Log.d("Order Repo", "Updated ${orderIds.size} orders to $newStatus")
            Result.success(true)
        } catch (e: Exception) {
            Log.e("Order Repo", "Failed to batch update orders: ${e.message}", e)
            Result.failure(e)
        }
    }


    // Parse order documents to OrderItem list
    private fun parseOrderDocuments(documents: List<com.google.firebase.firestore.DocumentSnapshot>): List<OrderItem> {
        return documents.mapNotNull { doc ->
            try {
                val status = doc.getString("status") ?: return@mapNotNull null
                val sellerId = doc.getString("seller_id") ?: ""
                val buyerId = doc.getString("user_id") ?: ""
                val buyerName = doc.getString("buyer_name") ?: ""
                val buyerAddress = doc.getString("buyer_address") ?: ""
                val buyerPhone = doc.getString("buyer_phone") ?: ""

                // Format date
                val firebaseTimestamp: Timestamp = doc.getTimestamp("created_at") ?: return@mapNotNull null
                val date = firebaseTimestamp.toDate()
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val formattedDate = outputFormat.format(date)

                // Parse order items
                val rawItems = doc.get("order_items") as? List<Map<String, Any>> ?: emptyList()
                val cartItems = parseCartItems(rawItems)

                OrderItem(
                    orderId = doc.id,
                    items = cartItems,
                    status = status,
                    createAt = formattedDate,
                    sellerId = sellerId,
                    buyerId = buyerId,
                    buyerName = buyerName,
                    buyerAddress = buyerAddress,
                    buyerPhone = buyerPhone
                )
            } catch (e: Exception) {
                Log.w("Order Repo", "Error parsing order document: ${e.message}")
                null
            }
        }
    }


    // Parse cart items from raw map data
    private fun parseCartItems(rawItems: List<Map<String, Any>>): List<CartItem> {
        return rawItems.mapNotNull { itemMap ->
            try {
                val cartId = itemMap["cart_id"] as? String ?: ""
                val quantity = (itemMap["quantity"] as? Long ?: 0L).toInt()
                val size = itemMap["size"] as? Double ?: 0.0

                val productMap = itemMap["product"] as? Map<String, Any>
                val product = Product(
                    productId = productMap?.get("productId") as? String ?: "",
                    name = productMap?.get("name") as? String ?: "",
                    brand = productMap?.get("brand") as? String ?: "",
                    price = productMap?.get("price") as? Double ?: 0.0,
                    imageUrl = productMap?.get("image_url") as? String ?: ""
                )

                CartItem(cartId, product, quantity, size)
            } catch (e: Exception) {
                Log.w("Order Repo", "Error parsing cart item: ${e.message}")
                null
            }
        }
    }
}