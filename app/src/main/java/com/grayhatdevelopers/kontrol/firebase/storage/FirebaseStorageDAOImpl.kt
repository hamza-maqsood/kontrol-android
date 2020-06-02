package com.grayhatdevelopers.kontrol.firebase.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseStorageDAOImpl(
    override val firebaseStorage: FirebaseStorage
) : FirebaseStorageDAO {

    override suspend fun uploadMediaToFirebaseStorage(fileUri: Uri, userID: String) : Uri {
        Timber.d("File URI: $fileUri")
        return try {
            val storageReference =
                FirebaseStorage.getInstance()
                    .getReference(
                        PROFILE_IMAGES_BUCKET +
                                "${userID}/" +
                                System.currentTimeMillis() +
                                fileUri.pathSegments.lastOrNull()
                    )

            storageReference.putFile(fileUri).await().task
            storageReference.downloadUrl.await()
        } catch (e: Exception) {
            Timber.d(e)
            Uri.EMPTY
        }
    }

    companion object {
        // constants
        private const val PROFILE_IMAGES_BUCKET = "user_uploaded_media/"

    }
}