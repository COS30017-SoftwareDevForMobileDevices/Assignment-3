package com.assignment3.repositories

import android.util.Log
import com.assignment3.models.CartItem
import com.assignment3.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


class CartRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch all cart items
    suspend fun fetchAllCartProducts(userId: String): List<CartItem> {
        return try {
            val cartDocs = db.collection("carts")
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("Cart Repo", "Fetched ${cartDocs.documents.size} cart docs for user $userId")
            cartDocs.documents.mapNotNull { cartDoc ->
                val productId = cartDoc.getString("product_id") ?: return@mapNotNull null
                val quantity = cartDoc.getLong("quantity")?.toInt() ?: 1
                val size = cartDoc.getDouble("size") ?: 0.0
                val cartId = cartDoc.id

                val productDoc = db.collection("products").document(productId).get().await()
                if (!productDoc.exists()) return@mapNotNull null

                val product = productDoc.toObject(Product::class.java)?.apply {
                    this.productId = productId
                } ?: return@mapNotNull null

                CartItem(cartId, product, quantity, size)
            }
        } catch (e: Exception) {
            Log.d("Cart Repo", "Fetched failed! ${e.toString()}")
            emptyList()
        }
    }


    // Add product to cart
    suspend fun addToCart(userId: String, product: Product, size: Double): Result<Boolean> {
        return try {
            val productCart = db.collection("carts")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("product_id", product.productId)
                .whereEqualTo("size", size)
                .get()
                .await()

            Log.d("Cart Repo", "Added ${productCart.documents.size} cart docs for user $userId and product ${product.productId}")
            if (productCart.documents.isNotEmpty()) {
                // Product already exists in cart, update quantity
                val existingDoc = productCart.documents.first()
                val currentQuantity = existingDoc.getLong("quantity") ?: 0
                val nextQuantity = currentQuantity + 1

                db.collection("carts")
                    .document(existingDoc.id)
                    .update("quantity", nextQuantity)
                    .await()
            } else {
                val data = mapOf(
                    "user_id" to userId,
                    "product_id" to product.productId,
                    "quantity" to 1,
                    "size" to size,
                    "created_at" to com.google.firebase.Timestamp.now()
                )

                db.collection("carts").add(data).await()
            }

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun increaseQuantity(cartItemId: String): Result<Boolean> {
        return try {
            val docRef = db.collection("carts").document(cartItemId)
            val doc = docRef.get().await()
            val currentQuantity = doc.getLong("quantity") ?: 0
            docRef.update("quantity", currentQuantity + 1).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun decreaseQuantity(cartItemId: String): Result<Boolean> {
        return try {
            val docRef = db.collection("carts").document(cartItemId)
            val doc = docRef.get().await()
            val currentQuantity = doc.getLong("quantity") ?: 0
            if (currentQuantity > 1) {
                docRef.update("quantity", currentQuantity - 1).await()
                Result.success(true)
            } else {
                deleteCartItem(cartItemId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteCartItem(cartItemId: String): Result<Boolean> {
        return try {
            db.collection("carts").document(cartItemId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}