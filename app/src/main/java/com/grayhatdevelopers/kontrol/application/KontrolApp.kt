package com.grayhatdevelopers.kontrol.application

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.grayhatdevelopers.kontrol.BuildConfig
import com.grayhatdevelopers.kontrol.di.appModule
import com.grayhatdevelopers.kontrol.di.firebaseModule
import com.grayhatdevelopers.kontrol.di.repositoryModule
import com.grayhatdevelopers.kontrol.di.viewModelsModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import timber.log.Timber

class KontrolApp
    : MultiDexApplication(),
    KodeinAware {

    override val kodein: Kodein
        get() = Kodein.lazy {

            bind<Context>("ApplicationContext") with singleton {
                this@KontrolApp.applicationContext
            }

            bind<KontrolApp>() with singleton {
                this@KontrolApp
            }

            import(appModule)
            import(firebaseModule)
            import(viewModelsModule)
            import(repositoryModule)
        }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}