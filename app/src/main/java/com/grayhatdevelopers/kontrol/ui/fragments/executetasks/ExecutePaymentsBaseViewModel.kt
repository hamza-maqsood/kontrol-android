package com.grayhatdevelopers.kontrol.ui.fragments.executetasks

import android.telephony.SmsMessage
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.models.LatLng
import com.grayhatdevelopers.kontrol.models.payment.Payment
import com.grayhatdevelopers.kontrol.models.payment.VerificationStatus
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.models.task.TaskStatus
import com.grayhatdevelopers.kontrol.receivers.SMSReceiver
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.LaunchIntentCommand
import com.grayhatdevelopers.kontrol.utils.AppConstants
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import com.grayhatdevelopers.kontrol.utils.getCurrentTime
import com.grayhatdevelopers.kontrol.utils.isPhoneNumberValid
import kotlinx.coroutines.launch

@Suppress("LeakingThis")
class ExecutePaymentsBaseViewModel : BaseViewModel(), SMSReceiver.IMessageListener {

    private lateinit var taskMark: TaskMarks
    private lateinit var payment: Payment
    var task: Task? = null

    val executeTaskActions: SingleLiveEvent<ExecuteTaskActions> = SingleLiveEvent()
    val attachmentPhotoState: SingleLiveEvent<AttachmentPhotoStates> = SingleLiveEvent()
    val executeTaskErrors: SingleLiveEvent<ExecuteTaskErrors> = SingleLiveEvent()

    val selectedTaskType: MutableLiveData<Int> = MutableLiveData()
    val selectedTaskModel: MutableLiveData<Int> = MutableLiveData()
    val taskTypes: MutableLiveData<List<String>> = MutableLiveData()
    val taskModels: MutableLiveData<List<String>> = MutableLiveData()

    @Bindable
    val selectedVerificationMethod: MutableLiveData<Int> = MutableLiveData()

    @Bindable
    val selectedPaymentMethod: MutableLiveData<Int> = MutableLiveData()

    @Bindable
    val enteredRemarks: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val enteredPhoneNumber: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val enteredReceivedAmount: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val isClientVerificationSelected: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val isPhoneNumberNotValid: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val isReceivedAmountEntered: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val isDebitEntered: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val labelText: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val selectedShop: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val enteredDebit: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val isShopNameNotSelected: MutableLiveData<Boolean> = MutableLiveData()

    init {
        taskTypes.postValue(AppConstants.TASK_TYPES.toList())
        taskModels.postValue(AppConstants.TASK_MODELS.toList())
        selectedTaskModel.postValue(0)
        selectedTaskType.postValue(0)
        selectedPaymentMethod.postValue(1)
        isClientVerificationSelected.postValue(false)
        isPhoneNumberNotValid.postValue(false)
        isReceivedAmountEntered.postValue(false)
        isShopNameNotSelected.postValue(false)
        labelText.postValue("${repo.currentUser?.displayName}, ${AppConstants.COMPANY_NAME}")
        SMSReceiver.bindMessageListener(listener = this)
    }

    fun verifyTask() {
        when (selectedVerificationMethod.value) {
            R.id.client_verify -> {
                when {
                    enteredPhoneNumber.value.isNullOrBlank() || !enteredPhoneNumber.value!!.isPhoneNumberValid() -> {
                        isPhoneNumberNotValid.postValue(true)
                    }
                    else -> {
                        taskMark = TaskMarks.CLIENT_VERIFIED
                        payment = generatePayment()
                        verifyFromClient()
                    }
                }
            }
            R.id.admin_verify -> {
                taskMark = TaskMarks.ADMIN_VERIFIED
                payment = generatePayment()
                verifyFromAdmin()
            }
            null -> {
                executeTaskErrors.postValue(ExecuteTaskErrors.VerificationMethodNotSelected)
            }
        }
    }

    private fun verifyFromAdmin() {
        payment.verificationStatus = VerificationStatus.ADMIN_PENDING
        executeTaskActions.postValue(ExecuteTaskActions.VerifyFromAdmin)
        // send payment to the server
        sendPaymentToTheServer()
    }

    private fun verifyFromClient() {
        payment.verificationStatus = VerificationStatus.CLIENT_VERIFIED
        executeTaskActions.postValue(ExecuteTaskActions.VerifyFromClient)
        val message = composeMessage()
        sendMessage(message)
    }

    private fun generatePayment(): Payment =
        Payment(
            _id = task?.taskID ?: "",
            taskID = task?.taskID ?: "",
            paidAmount = enteredReceivedAmount.value!!,
            timeOfPayment = getCurrentTime(),
            remarks = enteredRemarks.value ?: "",
            clientPhoneNumber = enteredPhoneNumber.value ?: "",
            shopName = task?.shopName ?: selectedShop.value!!,
            imageURL = "", // todo change
            location = LatLng(), // todo change
            paymentModel = task?.taskModel ?: taskModels.value!![selectedTaskModel.value!!],
            paymentType = task?.taskType ?: taskTypes.value!![selectedTaskModel.value!!]
        )

    /**
     *This method gets invoked whenever a new SMS message is received
     */
    override fun messageReceived(message: SmsMessage) {
        if (message.displayOriginatingAddress == enteredPhoneNumber.value!!
                .replaceFirst("0".toRegex(), "+92").replace(
                    " ", ""
                ) || message
                .displayOriginatingAddress == enteredPhoneNumber.value!!.replace(" ", "")
        )
            if (message.displayMessageBody == "y" || message.displayMessageBody == "Y")
                smsReceived(true)
            else
                smsReceived(false)
    }

    private fun sendMessage(message: String) {
        executeTaskActions.postValue(
            ExecuteTaskActions.SendSMS(
                message,
                enteredPhoneNumber.value!!
            )
        )
    }

    private fun composeMessage() = StringBuilder().apply {
        append("${AppConstants.COMPANY_NAME}\n")
        append("Shop: ${task?.shopName ?: selectedShop.value!!}")
        append("Debit: ${task?.debit ?: enteredDebit.value!!}\n")
        append("Paid Amount: ${enteredReceivedAmount.value!!}\n")
        append("Reply with Y to verify or N to deny.")
    }.toString()

    private fun sendPaymentToTheServer() {
        viewModelScope.launch {
            val response = repo.addPaymentToTask(payment)
            when (response.code()) {
                AppConstants.ACCEPTED -> {
                    Log.d(TAG, "Payment sent!")
                    executeTaskActions.postValue(ExecuteTaskActions.VerificationSuccessful)
                    markTask()
                    saveToLocalDatabase(payment)
                }

                AppConstants.NOT_FOUND -> {
                    Log.d(TAG, "Payment sending failed due to network error")
                    executeTaskErrors.postValue(ExecuteTaskErrors.NetworkError)
                }

                else -> {
                    Log.d(TAG, "Payment failed due to unknown error: ${response.code()}")
                }
            }
        }
    }

    private fun saveToLocalDatabase(payment: Payment) {

    }

    private fun markTask() {
        task?.let {
            repo.tasks.value?.find {
                it.taskID == task!!.taskID
            }?.let {
                it.taskStatus = when (taskMark) {
                    TaskMarks.ADMIN_VERIFIED -> {
                        TaskStatus.WAITING
                    }
                    TaskMarks.CLIENT_VERIFIED -> {
                        TaskStatus.COMPLETED
                    }
                }
            }
        }
    }

    private fun smsReceived(status: Boolean) {
        if (status) { // verified
            sendPaymentToTheServer()
        } else { // not verified
            executeTaskErrors.postValue(ExecuteTaskErrors.SMSVerificationFailed)
        }
    }

    fun toggleRadioSelection() {
        when (selectedVerificationMethod.value!!) {
            R.id.admin_verify -> {
                isClientVerificationSelected.postValue(false)
            }
            R.id.client_verify -> {
                isClientVerificationSelected.postValue(true)
            }
        }
    }

    fun changeSelectedCashMethod(value: Int) {
        selectedPaymentMethod.postValue(value)
    }

    fun showVerifyOptions() {
        if (selectedShop.value.isNullOrBlank()) isShopNameNotSelected.postValue(true)
        else isShopNameNotSelected.postValue(false)

        if (enteredDebit.value.isNullOrBlank()) isDebitEntered.postValue(true)
        else isDebitEntered.postValue(false)

        if (enteredReceivedAmount.value.isNullOrBlank()) isReceivedAmountEntered.postValue(true)
        else {
            isReceivedAmountEntered.postValue(false)
            if (task != null) { // means it's a task
                executeTaskActions.postValue(ExecuteTaskActions.ShowVerifyOptions)
            }
        }

        if (!selectedShop.value.isNullOrBlank() &&
            !enteredReceivedAmount.value.isNullOrBlank() &&
            !enteredDebit.value.isNullOrBlank()
        ) {
            executeTaskActions.postValue(ExecuteTaskActions.ShowVerifyOptions)
        }
    }

    fun goBack() {
        executeTaskActions.postValue(ExecuteTaskActions.GoBack)
    }

    fun addPhoto() {
        launchIntentCommand.postValue(LaunchIntentCommand.SelectImage)
    }

    fun saveToDraft() {
        executeTaskActions.postValue(ExecuteTaskActions.SaveToDraft)
    }

    enum class TaskMarks {
        ADMIN_VERIFIED, CLIENT_VERIFIED
    }

    companion object {
        private const val TAG = "ExecuteTasksBaseVM"
    }
}