package com.assignment3.repositories

import com.assignment3.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("products")


    // Since the entire dataset is small, fetching all works well
    suspend fun fetchAllProducts(): List<Product> {
        return try {
            val snapshot = collectionRef
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.apply {
                    productId = doc.id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    // Fetching the specific product for the detail screen
    suspend fun fetchProductById(productId: String): Product? {
        return try {
            val docRef = collectionRef.document(productId)
            val snapshot = docRef.get().await()
            snapshot.toObject(Product::class.java)?.apply {
                this.productId = snapshot.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}