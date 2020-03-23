package com.grayhatdevelopers.kontrol.utils

import com.grayhatdevelopers.kontrol.models.task.TasksViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.activetasks.ActiveTasksViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModelFactory
import com.grayhatdevelopers.kontrol.ui.fragments.chat.ChatViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.dashboard.DashboardViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.executetasks.ExecutePaymentsBaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.login.LoginViewModel

object InjectorUtils {

    fun provideBaseViewModel() = BaseViewModelFactory().create(BaseViewModel::class.java)
    fun provideLoginViewModel() = BaseViewModelFactory().create(LoginViewModel::class.java)
    fun provideDashboardViewModel() = BaseViewModelFactory().create(DashboardViewModel::class.java)
    fun provideActiveTasksViewModel() =
        BaseViewModelFactory().create(ActiveTasksViewModel::class.java)

    fun provideTasksViewModel() = BaseViewModelFactory().create(TasksViewModel::class.java)
    fun provideChatViewModel() = BaseViewModelFactory().create(ChatViewModel::class.java)
    fun provideCreateNewPaymentViewModel() =
        BaseViewModelFactory().create(ExecutePaymentsBaseViewModel::class.java)
}