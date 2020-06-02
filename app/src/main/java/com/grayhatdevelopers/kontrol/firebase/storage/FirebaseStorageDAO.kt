package com.grayhatdevelopers.kontrol.firebase.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

interface FirebaseStorageDAO {

    val firebaseStorage: FirebaseStorage

    suspend fun uploadMediaToFirebaseStorage(fileUri: Uri, userID: String) : Uri
}