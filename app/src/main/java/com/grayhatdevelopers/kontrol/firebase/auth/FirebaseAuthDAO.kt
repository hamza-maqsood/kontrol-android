package com.grayhatdevelopers.kontrol.firebase.auth

import com.google.firebase.auth.FirebaseAuth


interface FirebaseAuthDAO {

    val firebaseAuth: FirebaseAuth

    suspend fun loginUser() : Boolean
}