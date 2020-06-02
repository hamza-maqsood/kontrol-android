package com.grayhatdevelopers.kontrol.di

import android.content.Context
import android.content.res.Resources
import com.grayhatdevelopers.kontrol.application.KontrolApp
import com.grayhatdevelopers.kontrol.utils.sharedprefs.PrefUtils
import com.grayhatdevelopers.kontrol.utils.sharedprefs.SharedPreferences
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val MODULE_NAME = "App Module"

val appModule = Kodein.Module(
    name = MODULE_NAME,
    allowSilentOverride = false,
    prefix = "app"
) {


    bind<PrefUtils>() with singleton {
        getPrefsUtils(instance("ApplicationContext"))
    }

    bind<Resources>() with singleton {
        instance<KontrolApp>().resources
    }
}

private fun getPrefsUtils(context: Context) : PrefUtils = PrefUtils(
    prefs = SharedPreferences.getInstance(
        context = context
    )
)