package com.assignment3.repositories

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

    suspend fun fetchUserFavorites(userId: String): List<String> {
        return try {
            db.collection("favorites")
                .whereEqualTo("user_id", userId)
                .get()
                .await()
                .documents
                .map { it.getString("product_id")!! }
        } catch (e: Exception) {
            emptyList()
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
            db.collection("favorites").document(existingId).delete().await()
            false
        } else {
            addProductToFavorite(userId, productId)
            true
        }
    }

}