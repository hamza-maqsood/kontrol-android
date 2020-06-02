package com.grayhatdevelopers.kontrol.ui.fragments.executetasks

import android.content.Context
import android.net.Uri
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.models.payment.Payment
import com.grayhatdevelopers.kontrol.models.payment.PaymentType
import com.grayhatdevelopers.kontrol.models.payment.VerificationStatus
import com.grayhatdevelopers.kontrol.models.task.Task
import com.grayhatdevelopers.kontrol.ui.activity.AppActivity
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.LaunchIntentCommand
import com.grayhatdevelopers.kontrol.utils.*
import com.grayhatdevelopers.kontrol.views.OTPPinView
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.generic.instance
import timber.log.Timber
import java.util.*

@Suppress("LeakingThis")
class ExecutePaymentsBaseViewModel(
    context: Context
) : BaseViewModel(context),
    OTPPinView.OnInputCompleteListener,
    AppActivity.OnMediaAddedListener {

    private lateinit var taskMark: TaskMarks
    private lateinit var payment: Payment
    private lateinit var pin: String
    private var attachedImage = ""
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
    val imageUploadStatusText: MutableLiveData<String> = MutableLiveData()
    @Bindable
    val isShopNameNotSelected: MutableLiveData<Boolean> = MutableLiveData()
    @Bindable
    val isPinNotCorrect: MutableLiveData<Boolean> = MutableLiveData()
    @Bindable
    val isImageUploading: MutableLiveData<Boolean> = MutableLiveData()
    @Bindable
    val isImageUploaded: MutableLiveData<Boolean> = MutableLiveData()

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
        isPinNotCorrect.postValue(false)
        isImageUploaded.postValue(false)
        isImageUploading.postValue(false)
        imageUploadStatusText.postValue("Uploading Media...")
        labelText.postValue("${repo.currentUser?.displayName?.toUpperCase(Locale.getDefault())}@${AppConstants.COMPANY_NAME}")
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

    fun removeAttachedMedia() {
        isImageUploading.postValue(false)
        isImageUploaded.postValue(false)
        attachedImage = ""
    }

    private fun verifyFromAdmin() {
        if (isInternetAvailable(kodein.direct.instance())) {
            payment.verificationStatus = VerificationStatus.ADMIN_PENDING
            executeTaskActions.postValue(ExecuteTaskActions.VerifyFromAdmin)
            // send payment to the server
            sendPaymentToTheServer()
        } else {
            executeTaskErrors.postValue(ExecuteTaskErrors.NetworkError)
        }
    }

    private fun verifyFromClient() {
        OTPPinView.bindOnInputCompleteListener(this)
        payment.verificationStatus = VerificationStatus.CLIENT_VERIFIED
        executeTaskActions.postValue(ExecuteTaskActions.VerifyFromClient)
        val message = composeMessage()
        sendMessage(message)
    }

    private fun generatePayment(): Payment =
        Payment(
            _id = task?.taskID ?: generateItemId("payment"),
            taskID = task?.taskID ?: "",
            paidAmount = enteredReceivedAmount.value!!,
            timeOfPayment = DateUtils.getCurrentTime(),
            remarks = enteredRemarks.value ?: "",
            clientPhoneNumber = enteredPhoneNumber.value ?: "",
            shopName = task?.shopName ?: selectedShop.value!!,
            imageURL = attachedImage,
            location = LocationHandler.getUserCurrentLocation(),
            paymentModel = task?.taskModel ?: taskModels.value!![selectedTaskModel.value!!],
            paymentType = when (selectedPaymentMethod.value!!) {
                1 -> { PaymentType.CASH }
                2 -> { PaymentType.CHEQUE }
                3 -> { PaymentType.BANK_TRANSFER }
                else -> throw IllegalStateException("Invalid payment type")
            },
            chequeID = if (selectedPaymentMethod.value!! == 2) generateChequeID()
                       else "NIL"
        )


    private fun sendMessage(message: String) {
        executeTaskActions.postValue(
            ExecuteTaskActions.SendSMS(
                message,
                enteredPhoneNumber.value!!
            )
        )
    }

    private fun composeMessage() = StringBuilder().apply {
        pin = generateVerificationCode()
        Timber.d("Generated PIN is: $pin")
        append("${AppConstants.COMPANY_NAME}\n")
        append("Shop: ${task?.shopName ?: selectedShop.value!!}")
        append("Debit: ${task?.debit ?: enteredDebit.value!!}\n")
        append("Paid Amount: ${enteredReceivedAmount.value!!}\n")
        append("Verification code is: ${pin}.")
    }.toString()


    private fun sendPaymentToTheServer() {
        viewModelScope.launch {
            val response = repo.addPaymentToTask(payment)
            when (response.code()) {
                AppConstants.ACCEPTED -> {
                    Timber.d("Payment sent!")
                    executeTaskActions.postValue(ExecuteTaskActions.VerificationSuccessful)
                    markTask()
                    saveToLocalDatabase(payment)
                    removeAttachedMedia()
                }

                AppConstants.NOT_FOUND -> {
                    Timber.d("Payment sending failed due to network error")
                    executeTaskErrors.postValue(ExecuteTaskErrors.NetworkError)
                }

                else -> {
                    executeTaskErrors.postValue(ExecuteTaskErrors.InvalidTask)
                    Timber.d("Payment failed due to unknown error: ${response.code()}")
                }
            }
        }
    }

    private fun saveToLocalDatabase(payment: Payment) {
        viewModelScope.launch {
            repo.addPayment(payment = payment)
        }
    }

    private fun markTask() {
        task?.let {
            mOnTasksStatusUpdatedListener?.onTaskStatusUpdatedListener(it.taskID, taskMark)
        }
        task = null
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

    override fun onInputCompleteListener(input: String) {
        Timber.d("Pin input: $input and correct pin is: $pin")
        if (input == pin) {
            isPinNotCorrect.postValue(false)
            sendPaymentToTheServer()
        } else {
            isPinNotCorrect.postValue(true)
        }
    }

    fun goBack() {
        executeTaskActions.postValue(ExecuteTaskActions.GoBack)
    }

    fun addPhoto() {
        AppActivity.bindMediaAddedListener(this)
        launchIntentCommand.postValue(LaunchIntentCommand.SelectImage)
    }

    fun saveToDraft() {
        executeTaskActions.postValue(ExecuteTaskActions.SaveToDraft)
    }

    enum class TaskMarks {
        ADMIN_VERIFIED, CLIENT_VERIFIED
    }

    override fun onMediaAdded(uri: Uri, mediaType: MediaChooser.MediaType) {
        isImageUploading.postValue(true)
        viewModelScope.launch {

            // login to firebase
            val loginStatus = repo.firebase.firebaseAuthDAO.loginUser()
            if (loginStatus) {

                // upload the file on firebase storage
                val cloudURL = repo.firebase.firebaseStorageDAO.uploadMediaToFirebaseStorage(fileUri = uri, userID = repo.currentUser!!.username)
                if (cloudURL == Uri.EMPTY) {
                    // image failed to get uploaded to firebase storage
                    executeTaskErrors.postValue(ExecuteTaskErrors.NetworkError)
                } else {

                    // attach it to the task
                    attachedImage = uri.toString()
                    Timber.d("Attached image firebase url: $attachedImage")

                    // update image upload status
                    imageUploadStatusText.postValue("Photo Uploaded!")
                    isImageUploading.postValue(false)
                    isImageUploaded.postValue(true)
                }
            } else executeTaskErrors.postValue(ExecuteTaskErrors.NetworkError)
        }
    }

    interface OnTasksStatusUpdatedListener {
        fun onTaskStatusUpdatedListener(taskID: String, taskMarks: TaskMarks)
    }

    companion object {
        private var mOnTasksStatusUpdatedListener: OnTasksStatusUpdatedListener? = null
        fun bindOnTasksStatusUpdatedListener(listener: OnTasksStatusUpdatedListener) {
            mOnTasksStatusUpdatedListener = listener
        }
    }
}