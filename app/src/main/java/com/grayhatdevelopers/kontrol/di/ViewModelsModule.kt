package com.grayhatdevelopers.kontrol.di

import androidx.lifecycle.ViewModelProvider
import com.grayhatdevelopers.kontrol.models.drafts.DraftsViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.activetasks.ActiveTasksViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModelFactory
import com.grayhatdevelopers.kontrol.ui.fragments.chat.ChatViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.dashboard.DashboardViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.executetasks.ExecutePaymentsBaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.login.LoginViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.profile.ProfileViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.taskshistory.TasksHistoryViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.zoomableimageview.ZoomableImageViewViewModel
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

private const val MODULE_NAME = "ViewModels Module"

val viewModelsModule = Kodein.Module(
    name = MODULE_NAME,
    allowSilentOverride = false,
    prefix = "app"
) {

    bind<ViewModelProvider.Factory>() with singleton {
        BaseViewModelFactory(injector = kodein.direct)
    }

    bindViewModel<BaseViewModel>() with singleton {
        BaseViewModel(app = instance())
    }

    bindViewModel<DashboardViewModel>() with singleton {
        DashboardViewModel(context = instance())
    }

    bindViewModel<ActiveTasksViewModel>() with provider {
        ActiveTasksViewModel(context = instance())
    }

    bindViewModel<ChatViewModel>() with provider {
        ChatViewModel(context = instance())
    }

    bindViewModel<ExecutePaymentsBaseViewModel>() with singleton {
        ExecutePaymentsBaseViewModel(context = instance())
    }

    bindViewModel<LoginViewModel>() with singleton {
        LoginViewModel(context = instance())
    }

    bindViewModel<ProfileViewModel>() with singleton {
        ProfileViewModel(context = instance())
    }

    bindViewModel<TasksHistoryViewModel>() with singleton {
        TasksHistoryViewModel(context = instance())
    }

    bindViewModel<DraftsViewModel>() with singleton {
        DraftsViewModel(context = instance())
    }

    bindViewModel<ZoomableImageViewViewModel>() with singleton {
        ZoomableImageViewViewModel(context = instance())
    }

}