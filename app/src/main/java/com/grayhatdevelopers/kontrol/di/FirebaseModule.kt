package com.grayhatdevelopers.kontrol.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.grayhatdevelopers.kontrol.firebase.auth.FirebaseAuthDAO
import com.grayhatdevelopers.kontrol.firebase.auth.FirebaseAuthDAOImpl
import com.grayhatdevelopers.kontrol.firebase.helper.FirebaseHelperDAO
import com.grayhatdevelopers.kontrol.firebase.helper.FirebaseHelperDAOImpl
import com.grayhatdevelopers.kontrol.firebase.storage.FirebaseStorageDAO
import com.grayhatdevelopers.kontrol.firebase.storage.FirebaseStorageDAOImpl
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val MODULE_NAME = "Firebase Module"

val firebaseModule = Kodein.Module(
    name = MODULE_NAME,
    allowSilentOverride = false,
    prefix = "app"
) {

    bind<FirebaseStorageDAO>() with singleton {
        FirebaseStorageDAOImpl(
            firebaseStorage = Firebase.storage
        )
    }

    bind<FirebaseAuthDAO>() with singleton {
        FirebaseAuthDAOImpl(
            firebaseAuth = FirebaseAuth.getInstance()
        )
    }
    bind<FirebaseHelperDAO>() with singleton {
        FirebaseHelperDAOImpl(
            instance(),
            instance()
        )
    }
}