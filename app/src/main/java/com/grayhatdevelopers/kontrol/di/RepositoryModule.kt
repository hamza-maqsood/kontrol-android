package com.grayhatdevelopers.kontrol.di

import com.grayhatdevelopers.kontrol.repo.Repository
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton


private const val MODULE_NAME = "Repository Module"

val repositoryModule = Kodein.Module(
    name = MODULE_NAME,
    allowSilentOverride = false,
    prefix = "app"
) {

    bind<Repository>() with singleton {
        Repository.getInstance(instance(), instance(), instance())
    }
}