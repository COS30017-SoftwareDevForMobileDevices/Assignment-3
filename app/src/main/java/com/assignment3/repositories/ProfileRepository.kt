package com.assignment3.repositories

import com.assignment3.models.ShippingAddress
import com.assignment3.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")


    // Get user info
    suspend fun getUserInfoById(userId: String): User? {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            if (!snapshot.exists()) return null
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }


    // Get all shipping addresses from embedded list
    suspend fun getShippingAddresses(userId: String): List<ShippingAddress> {
        return try {
            val user = getUserInfoById(userId)
            user?.shippingAddresses?.reversed() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }


    // Add new shipping address
    suspend fun addShippingAddress(userId: String, address: ShippingAddress): Boolean {
        return try {
            val userRef = usersCollection.document(userId)
            val snapshot = userRef.get().await()

            val user = snapshot.toObject(User::class.java) ?: return false

            // Generate ID
            address.shippingId = firestore.collection("tmp").document().id

            val updatedList = user.shippingAddresses.toMutableList()

            // If no address exists yet, make this the default
            if (updatedList.isEmpty()) {
                address.isDefault = true
            } else {
                // All new addresses after the first must be false
                address.isDefault = false
            }

            updatedList.add(address)

            userRef.update("shipping_addresses", updatedList).await()
            true
        } catch (e: Exception) {
            false
        }
    }



    // Update shipping address
    suspend fun updateShippingAddress(userId: String, updatedAddress: ShippingAddress): Boolean {
        return try {
            val userRef = usersCollection.document(userId)
            val snapshot = userRef.get().await()

            val user = snapshot.toObject(User::class.java) ?: return false

            val updatedList = user.shippingAddresses.toMutableList()

            val index = updatedList.indexOfFirst { it.shippingId == updatedAddress.shippingId }
            if (index == -1) return false

            updatedList[index] = updatedAddress

            userRef.update("shipping_addresses", updatedList).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    // Delete shipping address
    suspend fun deleteShippingAddress(userId: String, shippingId: String): Boolean {
        return try {
            val userRef = usersCollection.document(userId)
            val snapshot = userRef.get().await()

            val user = snapshot.toObject(User::class.java) ?: return false

            val updatedList = user.shippingAddresses.filter { it.shippingId != shippingId }

            // Check if the deleted address was default
            val deletedWasDefault = user.shippingAddresses.find { it.shippingId == shippingId }?.isDefault == true

            // If default was deleted and there are remaining addresses, set the first one as default
            val finalList = if (deletedWasDefault && updatedList.isNotEmpty()) {
                updatedList.mapIndexed { index, address ->
                    address.copy(isDefault = index == 0)
                }
            } else {
                updatedList
            }

            userRef.update("shipping_addresses", finalList).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    // Set Default Shipping information for the user
    suspend fun setDefaultShippingAddress(userId: String, shippingId: String): Boolean {
        return try {
            val userRef = firestore.collection("users").document(userId)
            val snapshot = userRef.get().await()

            if (!snapshot.exists()) return false

            val user = snapshot.toObject(User::class.java) ?: return false
            val updatedAddresses = user.shippingAddresses.map { address ->
                // Set only the selected address to default
                address.copy(isDefault = address.shippingId == shippingId)
            }

            // Update entire array
            userRef.update("shipping_addresses", updatedAddresses).await()
            true
        } catch (e: Exception) {
            false
        }
    }

}
