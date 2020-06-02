package com.grayhatdevelopers.kontrol.repo

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.grayhatdevelopers.kontrol.database.AppDatabase
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity
import com.grayhatdevelopers.kontrol.database.messages.MessagesDAO
import com.grayhatdevelopers.kontrol.database.payments.PaymentsDAO
import com.grayhatdevelopers.kontrol.firebase.helper.FirebaseHelperDAO
import com.grayhatdevelopers.kontrol.models.GetTasksRequest
import com.grayhatdevelopers.kontrol.models.LoginCredentials
import com.grayhatdevelopers.kontrol.models.Rider
import com.grayhatdevelopers.kontrol.models.message.MessageState
import com.grayhatdevelopers.kontrol.models.payment.Payment
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.utils.sharedprefs.PrefUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

// todo: use repo model, split into room dao and what not
class Repository private constructor(
    private val sharedPreferencesHelper: PrefUtils,
    val firebase: FirebaseHelperDAO,
    context: Context
) : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    companion object {
        // Singleton instantiation
        @Volatile
        private var instance: Repository? = null

        fun getInstance(firebase: FirebaseHelperDAO, sp: PrefUtils, context: Context) =
                instance ?: synchronized(this) {
                    instance ?: Repository(sp, firebase, context).also { instance = it }
                }

        fun getUserSessionTokens() : String {
            return instance?.let {
                return@let if (it.currentUser != null)
                    it.currentUser?.sessionToken
                else ""
            }!!
        }
    }

    var currentUser: Rider? = null

    private val retrofitAPI = ApiClient.createService(RetrofitDAO::class.java)
    private val messagesDAO: MessagesDAO = AppDatabase(context = context).messagesDAO()
    private val paymentsDAO: PaymentsDAO = AppDatabase(context = context).paymentsDAO()


    val tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val mObservableMessages: MediatorLiveData<List<MessageEntity>> = MediatorLiveData()


    val lastFetchAt: Long
        get() {
            return sharedPreferencesHelper.getLastFetchTime()
        }

    fun observeMessages() {
        mObservableMessages.addSource(messagesDAO.getMessagesByParticipant(currentUser?.username ?: "Unknown")) {
            it?.let {
                mObservableMessages.postValue(it)
            }
        }
    }

    fun initRider() {
        currentUser = sharedPreferencesHelper.getRider()
        sharedPreferencesHelper.getRider()
    }

    fun updateLastFetch(lastFetch: Long) {
        sharedPreferencesHelper.updateLastFetchTime(lastFetch)
    }

    fun isRiderLoggedIn(): Boolean = sharedPreferencesHelper.isRiderLoggedIn()

    private fun changeUserLoginStatus(status: Boolean) {
        sharedPreferencesHelper.changeRiderLoginStatus(status)
    }

    fun logoutUser() {
        currentUser = null
        sharedPreferencesHelper.removeRider()
        changeUserLoginStatus(false)
    }

    suspend fun updateMessageState(messageID: String, newState: MessageState) {
        Timber.d("Updating message state. messageID: $messageID and new state: $newState")
        withContext(coroutineContext) {
            messagesDAO.updateMessageState(
                messageID = messageID,
                newMessageState = newState
            )
        }
    }

    suspend fun addMessage(messageEntity: MessageEntity) {
        Timber.d("Adding message: $messageEntity")
        withContext(coroutineContext) {
            messagesDAO.insertMessage(
                message = messageEntity
            )
        }
    }

    private suspend fun getMessagesByParticipant(participantName: String) : LiveData<List<MessageEntity>> {
        Timber.d("Request for message fetch for $participantName")
        return withContext(coroutineContext) {
            messagesDAO.getMessagesByParticipant(
                participant = participantName
            )
        }
    }

    fun removeFromNewMessages(id: String) {

    }

    suspend fun addPayment(payment: Payment) {
        withContext(Dispatchers.IO) {
            paymentsDAO.insertPayment(paymentEntity = payment.toPaymentEntity())
        }
    }

    suspend fun getPayments(userID: String) {

    }

    suspend fun getMessages(participantName: String) {

    }

    suspend fun loginRider(credentials: LoginCredentials) = retrofitAPI.login(credentials)

    suspend fun getTasks(getTasksRequest: GetTasksRequest) =
        retrofitAPI.getRiderTasks(getTasksRequest)

    suspend fun getClientsData() = retrofitAPI.getAllClients()

    suspend fun addPaymentToTask(payment: Payment) = retrofitAPI.addPaymentToTask(payment)

    fun updateRider(rider: Rider) {
        Timber.d("Updating Rider: $rider")
        changeUserLoginStatus(status = true)
        currentUser = rider
        sharedPreferencesHelper.saveRider(rider)
    }

    suspend fun sendMessageToServer(message: MessageEntity) {
        withContext(Dispatchers.IO) {
            retrofitAPI.sendMessage(message.toSimpleMessage())
        }
    }
}