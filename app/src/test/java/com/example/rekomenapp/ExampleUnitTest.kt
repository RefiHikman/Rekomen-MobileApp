package com.example.rekomenapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private lateinit var dbUserRef: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    @Test
    fun firebaseTest() {
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUserId = firebaseAuth.currentUser!!.uid

        dbUserRef = FirebaseDatabase.getInstance().getReference("Users")
        dbUserRef.child(currentUserId).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
}