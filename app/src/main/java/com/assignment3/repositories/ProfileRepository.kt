package com.assignment3.repositories

import com.assignment3.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getUserInfoById(userId: String): User? {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (!snapshot.exists()) return null

            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
