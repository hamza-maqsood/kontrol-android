package com.grayhatdevelopers.kontrol.repo

import com.grayhatdevelopers.kontrol.models.GetRequestResponse
import com.grayhatdevelopers.kontrol.models.GetTasksRequest
import com.grayhatdevelopers.kontrol.models.LoginCredentials
import com.grayhatdevelopers.kontrol.models.Rider
import com.grayhatdevelopers.kontrol.models.client.Client
import com.grayhatdevelopers.kontrol.models.message.Message
import com.grayhatdevelopers.kontrol.models.payment.Payment
import com.grayhatdevelopers.kontrol.utils.AppConstants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitDAO {

    @POST(AppConstants.RIDER_LOGIN_ROUTE)
    suspend fun login(@Body credentials: LoginCredentials): Response<Rider?>

    @POST(AppConstants.GET_RIDER_TASKS)
    suspend fun getRiderTasks(@Body getTasksRequest: GetTasksRequest): Response<GetRequestResponse>

    @POST(AppConstants.ADD_PAYMENT_TO_TASK_ROUTE)
    suspend fun addPaymentToTask(@Body payment: Payment): Response<Boolean>

    @POST(AppConstants.SEND_MESSAGE)
    suspend fun sendMessage(@Body message: Message): Response<Boolean>

    @GET(AppConstants.GET_CLIENTS_DATA)
    suspend fun getAllClients() : Response<List<Client>>

}