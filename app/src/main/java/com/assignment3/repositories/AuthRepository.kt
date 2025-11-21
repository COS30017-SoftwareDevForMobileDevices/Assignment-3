package com.assignment3.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun register(fullName: String, email: String, password: String): Result<Boolean> {
        return try {
            val auth = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.user ?: return Result.failure(Exception("User not created"))

            val data = mapOf(
                "full_name" to fullName,
                "email" to user.email,
                "wallet_balance" to 10000
            )

            db.collection("users").document(user.uid).set(data).await()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

