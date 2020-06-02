package com.grayhatdevelopers.kontrol.ui.fragments.base

import android.content.Context
import android.os.Bundle
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.grayhatdevelopers.kontrol.repo.Repository
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

open class BaseViewModel(
    app: Context
) : ViewModel(),
    Observable,
    KodeinAware {

    override val kodein: Kodein by closestKodein(context = app)

    val repo: Repository by instance<Repository>()

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val launchIntentCommand: SingleLiveEvent<LaunchIntentCommand> = SingleLiveEvent()

    fun navigate(directions: NavDirections, bundle: Bundle?) {
        navigationCommand.postValue(NavigationCommand.To(directions, bundle))
    }

    fun navigate(directions: NavDirections) {
        navigationCommand.postValue(NavigationCommand.To(directions, null))
    }


    fun popBack() {
        navigationCommand.postValue(NavigationCommand.Back)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.add(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callbacks.remove(callback)
    }
}