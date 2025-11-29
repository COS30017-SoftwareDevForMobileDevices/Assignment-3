package com.assignment3.repositories

import android.util.Log
import com.assignment3.models.Product
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("products")

    private val placeholderImages = listOf(
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325843/1_f6rlgk.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325793/2_tav5xv.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325793/3_wjekjn.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325793/4_qgpsry.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325797/5_cfk7ym.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325795/6_c3jmky.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325795/7_phpkmg.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325795/8_hlzojy.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325796/9_xqto2i.png",
        "https://res.cloudinary.com/dcpcirmy7/image/upload/v1764325795/10_ll66zq.png"
    )

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

    suspend fun fetchUserProducts(userId: String): List<Product> {
        return try {
            val snapshot = collectionRef
                .whereEqualTo("ownerId", userId)
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
            Log.d("Product Repo", "Failed to fetch ${e.toString()}")
            emptyList()
        }
    }

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

    suspend fun addProduct(userId: String, product: Product): Boolean {
        return try {
            val imageUrl = product.imageUrl.ifBlank {
                placeholderImages.random()
            }

            val data = mapOf(
                "ownerId" to userId,
                "name" to product.name,
                "brand" to product.brand,
                "description" to product.description,
                "price" to product.price,
                "image_url" to imageUrl,
                "created_at" to Timestamp.now()
            )

            collectionRef.add(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateProduct(product: Product): Boolean {
        return try {
            val imageUrl = product.imageUrl.ifBlank {
                placeholderImages.random()
            }

            val data = mapOf(
                "name" to product.name,
                "brand" to product.brand,
                "description" to product.description,
                "price" to product.price,
                "image_url" to imageUrl
            )

            collectionRef.document(product.productId).update(data).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            collectionRef.document(productId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}