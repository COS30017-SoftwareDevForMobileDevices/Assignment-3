package com.assignment3.repositories

import com.assignment3.models.Product
import com.assignment3.models.productList
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val pageSize = 20L
    private val collectionRef = db.collection("products")

    // Create query to fetch data from Firestore
    suspend fun fetchProducts(lastVisible: DocumentSnapshot?): Pair<List<Product>, DocumentSnapshot?> = try {
        var query = collectionRef
            .orderBy("created_at", Query.Direction.DESCENDING)
            .limit(pageSize)

        lastVisible?.let {
            query = query.startAfter(it)
        }

        val snapshot = query.get().await()

        // Converts each document data to Product object
        val products = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)?.apply {
                productId = doc.id
            }
        }

        productList.addAll(products)

        // Returns the list of fetched documents and the last document in the list
        val lastDoc = snapshot.documents.lastOrNull()
        Pair(products, lastDoc)
    } catch (e: Exception) {
        e.printStackTrace()
        Pair(emptyList(), null)
    }
}
