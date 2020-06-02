package com.grayhatdevelopers.kontrol.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthDAOImpl(
    override val firebaseAuth: FirebaseAuth
) : FirebaseAuthDAO {

    override suspend fun loginUser() : Boolean = firebaseAuth.signInAnonymously().await().user != null

}