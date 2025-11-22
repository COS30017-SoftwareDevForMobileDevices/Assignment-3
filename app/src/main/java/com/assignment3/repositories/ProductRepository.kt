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
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/636ed63f-f78f-400d-9b38-91b3e4249187/NIKE+AIR+MAX+95.png",
        "https://static.nike.com/a/images/w_1280,q_auto,f_auto/ba6d6a6c-36ec-4782-be43-f2cb0a0f6b16/air-max-95-og-neon-yellow-release-date.jpg",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/c543e01d-9128-410e-a62f-436416e1834a/W+NIKE+AIR+MAX+95+BIG+BUBBLE.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/9694818a-cf82-43e8-81f3-58f432c0f7b6/G.T.+JUMP+ACADEMY+EP.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/01f63791-c733-4e2b-9df5-7cab44dba920/M+NIKE+COURT+LITE+4.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/6188443e-71b7-49f2-b0c3-619dd3ef039a/NIKE+UPLIFT+SC.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/9e0de4e1-6862-4d96-9821-94050582defe/M+NIKE+AIR+ZOOM+TR+1.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/47b7945e-a379-4c24-b9df-98f4eef178e5/NIKE+AIR+MAX+PLUS.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/63c4596a-ca9a-4a56-82f3-0387903ed5f1/AIR+ZOOM+PEGASUS+41.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/9c3f5d2c-2a33-4e11-8bf7-cddad6e14121/NIKE+RUN+DEFY.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/db94d306-2abb-43e0-bc9e-4c2a6837ef98/G.T.+CUT+3+SE+%28GS%29.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/285df0bb-f4c3-420e-8b4f-d57c1431abf2/W+NIKE+INTERACT+RUN+EASYON.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/e7f0dcb1-be59-4e8d-8732-508108065339/NIKE+AIR+ZOOM+G.T.+JUMP+2+EP.png",
        "https://static.nike.com/a/images/t_default/273e8141-944b-4d82-9619-9f4870053d2d/TEAM+HUSTLE+D+11+%28GS%29.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/75bc7c02-f18c-43d3-9456-2d00bb6d5e30/NIKE+JOURNEY+RUN.png",
        "https://static.nike.com/a/images/t_web_pdp_936_v2/f_auto/642cec04-9407-4c94-9c2b-b03843a85caa/JA+2+%28GS%29.png",
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