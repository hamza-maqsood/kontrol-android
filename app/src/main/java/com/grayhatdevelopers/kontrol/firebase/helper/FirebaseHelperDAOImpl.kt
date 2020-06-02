package com.grayhatdevelopers.kontrol.firebase.helper

import com.grayhatdevelopers.kontrol.firebase.auth.FirebaseAuthDAO
import com.grayhatdevelopers.kontrol.firebase.storage.FirebaseStorageDAO

class FirebaseHelperDAOImpl(
    override val firebaseAuthDAO: FirebaseAuthDAO,
    override val firebaseStorageDAO: FirebaseStorageDAO
) : FirebaseHelperDAO