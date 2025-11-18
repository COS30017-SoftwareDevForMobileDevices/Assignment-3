package com.assignment3.repositories

import com.assignment3.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class FavoriteRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addProductToFavorite(userId: String, productId: String): Result<Boolean> {
        return try {
            val zoned = ZonedDateTime.now(ZoneId.of("UTC+7"))
            val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm:ss a 'UTC+7'")
            val formattedCurrentTime = zoned.format(formatter)

            val data = mapOf(
                "user_id" to userId,
                "product_id" to productId,
                "created_at" to formattedCurrentTime
            )

            db.collection("favorites").add(data).await()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserFavoriteId(userId: String): List<String> {
        return try {
            db.collection("favorites")
                .whereEqualTo("user_id", userId)
                .get()
                .await()
                .documents
                .mapNotNull { it.getString("product_id") }
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun fetchUserFavorites(userId: String): List<Product> {
        return try {
            val favoriteDocs = db.collection("favorites")
                .whereEqualTo("user_id", userId)
                .get()
                .await()

            val productIds = favoriteDocs.documents.mapNotNull {
                it.getString("product_id")
            }

            if (productIds.isEmpty()) return emptyList()

            val products = mutableListOf<Product>()
            for (productId in productIds) {
                val doc = db.collection("products").document(productId).get().await()
                if (doc.exists()) {
                    val product = doc.toObject(Product::class.java)
                    product?.productId = doc.id
                    if (product != null) {
                        products.add(product)
                    }
                }
            }
            products
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProduct(productId: String): Product? {
        return try {
            val doc = db.collection("products").document(productId).get().await()
            if (doc.exists()) {
                val product = doc.toObject(Product::class.java)
                product?.productId = doc.id
                product
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getFavoriteDocId(userId: String, productId: String): String? {
        return try {
            val snapshot = db.collection("favorites")
                .whereEqualTo("user_id", userId)
                .whereEqualTo("product_id", productId)
                .get()
                .await()

            if (snapshot.documents.isNotEmpty()) snapshot.documents[0].id else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun toggleFavorite(userId: String, productId: String): Boolean {
        val existingId = getFavoriteDocId(userId, productId)

        // Check and uncheck favorite
        return if (existingId != null) {
            try {
                db.collection("favorites").document(existingId).delete().await()
                false
            } catch (e: Exception) {
                false
            }
        } else {
            val addResult = addProductToFavorite(userId, productId)
            addResult.isSuccess
        }
    }

}