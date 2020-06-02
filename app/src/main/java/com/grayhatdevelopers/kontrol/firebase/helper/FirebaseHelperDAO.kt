package com.grayhatdevelopers.kontrol.firebase.helper

import com.grayhatdevelopers.kontrol.firebase.auth.FirebaseAuthDAO
import com.grayhatdevelopers.kontrol.firebase.storage.FirebaseStorageDAO

interface FirebaseHelperDAO {
    val firebaseAuthDAO : FirebaseAuthDAO
    val firebaseStorageDAO : FirebaseStorageDAO
}