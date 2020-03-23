package com.grayhatdevelopers.kontrol.repo

import com.grayhatdevelopers.kontrol.models.GetTasksRequest
import com.grayhatdevelopers.kontrol.models.LoginCredentials
import com.grayhatdevelopers.kontrol.models.payment.Payment
import com.grayhatdevelopers.kontrol.models.Rider
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitDAO {

    @POST(AppConstants.RIDER_LOGIN_ROUTE)
    suspend fun login(@Body credentials: LoginCredentials): Response<Rider?>

    @POST(AppConstants.GET_RIDER_TASKS)
    suspend fun getRiderTasks(@Body getTasksRequest: GetTasksRequest): Response<MutableList<Task>>

    @POST(AppConstants.ADD_PAYMENT_TO_TASK_ROUTE)
    suspend fun addPaymentToTask(@Body payment: Payment): Response<Unit>

}