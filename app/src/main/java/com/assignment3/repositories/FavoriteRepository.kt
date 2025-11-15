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
}