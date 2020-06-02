package com.grayhatdevelopers.kontrol.ui.fragments.chat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity
import com.grayhatdevelopers.kontrol.models.message.MessageState
import com.grayhatdevelopers.kontrol.models.message.MessageType
import com.grayhatdevelopers.kontrol.ui.activity.AppActivity
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.DateUtils
import com.grayhatdevelopers.kontrol.utils.MediaChooser
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import com.grayhatdevelopers.kontrol.utils.generateItemId
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.generic.instance
import timber.log.Timber
import java.io.File

class ChatViewModel(
    context: Context
) : BaseViewModel(context), View.OnTouchListener,
    AppActivity.OnMediaAddedListener {

    private var recorder: MediaRecorder? = null

    val chatActions: SingleLiveEvent<ChatActions> = SingleLiveEvent()

    @Bindable
    val enteredMessage: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val recordingTime: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val isRecording: MutableLiveData<Boolean> = MutableLiveData()

    @Bindable
    val switchToTextMode: MutableLiveData<Boolean> = MutableLiveData()

    lateinit var messagesList: MutableLiveData<List<MessageEntity>>

    init {
        chatActions.postValue(ChatActions.TypingStopped)
        switchToTextMode.postValue(false)
        observeMessages()
        repo.observeMessages()
    }

    private val mRunnable: Runnable by lazy {
        Runnable {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime
            UpdateTime = MillisecondTime
            seconds = (UpdateTime / 1000).toInt()
            minutes = seconds / 60
            seconds %= 60

            recordingTime.postValue("""$minutes:${String.format("%02d", seconds)}""")
            handler.postDelayed(mRunnable, 0)
        }
    }

    fun goBack() {
        chatActions.postValue(ChatActions.GoBack)
    }

    fun showAttachmentOptions() {
        chatActions.postValue(ChatActions.ShowAttachmentsOption)
    }

    fun sendTextMessage() {
        sendMessage(MessageType.TEXT, enteredMessage.value!!)
        enteredMessage.postValue("")
        chatActions.postValue(ChatActions.TypingStopped)
        switchToTextMode.postValue(false)
    }

    fun selectFile() {
        delegateMediaRequest(AttachmentTypes.FILE)
    }

    fun selectImage() {
        delegateMediaRequest(AttachmentTypes.MEDIA)
    }

    fun selectContact() {
        delegateMediaRequest(AttachmentTypes.CONTACT)
    }

    fun selectLocation() {
        delegateMediaRequest(AttachmentTypes.LOCATION)
    }

    private fun observeMessages() {
        messagesList = repo.mObservableMessages
    }

    private fun delegateMediaRequest(attachmentTypes: AttachmentTypes) {
        AppActivity.bindMediaAddedListener( this)
        chatActions.postValue(ChatActions.SelectAttachment(attachmentTypes))
    }

    private fun sendMessage(messageType: MessageType, data: String) {
        Timber.d("sending message, type: $messageType")
        val messageID = when (messageType) {
            MessageType.TEXT -> {
                Timber.d("Sending text message")
                generateItemId("text")
            }
            MessageType.IMAGE -> {
                Timber.d("Sending Image")
                generateItemId("image")
            }
            MessageType.VIDEO -> {
                Timber.d("Sending Video")
                generateItemId("video")
            }
            MessageType.FILE -> {
                Timber.d("Sending File")
                generateItemId("file")
            }
            MessageType.CONTACT -> {
                Timber.d("Sending Contact")
                generateItemId("contact")
            }
            MessageType.AUDIO -> {
                Timber.d("Sending audio message")
                generateItemId("voice")
            }
        }

        val message = MessageEntity(
            messageID = messageID,
            participant = repo.currentUser?.username ?: "Unknown",
            messageType = messageType,
            time = DateUtils.getCurrentMillis().toString(),
            messageState = MessageState.QUEUED,
            isMessageSent = true,
            messageData = data
        )

        viewModelScope.launch {
            // send the message
            repo.addMessage(message)
        }
    }


    @Suppress("UNUSED_PARAMETER")
    fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (text.isNotEmpty()) {
            chatActions.postValue(ChatActions.TypingStarted)
            switchToTextMode.postValue(true)
        } else {
            chatActions.postValue(ChatActions.TypingStopped)
            switchToTextMode.postValue(false)
        }
    }

    private fun startRecording() {

        // storing the voice messages in cache, so they won't be visible to apps like music and files
        val path = "${kodein.direct.instance<Context>().externalCacheDir?.absolutePath}"
        fileName = "${path}/voice_msg${DateUtils.getTimeForFilename()}.3gp"
        val file = File(path)
        file.mkdirs()
        Timber.d("filename: $fileName")

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            try {
                prepare()
                start()
            } catch (e: Exception) {
                Timber.d("prepare() failed: $e")
            }

        }
    }

    private fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                reset()
                release()
            }
            recorder = null

            val timeFrame = (minutes * 60) + seconds
            // only send voice message if time frame is greater than the defined threshold
            if (timeFrame > voiceMessageLowerThreshold)
                sendMessage(MessageType.AUDIO, fileName)
            else Timber.d("Time Frame: $timeFrame is less than threshold, so not sending voice message..")

            // reset the timer
            MillisecondTime = 0
            UpdateTime = 0
            StartTime = 0
            seconds = 0
            minutes = 0
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    private fun arePermissionsAllowed(): Boolean = (
            Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(
                        kodein.direct.instance(),
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        kodein.direct.instance(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED)


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.let {
            it.performClick()
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> { /* button is on HOLD or PRESSED */
                    if (!arePermissionsAllowed()) {
                        chatActions.postValue(ChatActions.RecordingStarted)
                        isRecording.postValue(true)
                        StartTime = SystemClock.uptimeMillis()
                        mRunnable.run()
                        startRecording()
                    } else chatActions.postValue(ChatActions.PermissionsMissing)
                    return true
                }
                MotionEvent.ACTION_UP -> { /* button is RELEASED */
                    chatActions.postValue(ChatActions.RecordingFinished)
                    isRecording.postValue(false)
                    // remove callback
                    handler.removeCallbacks(mRunnable)
                    stopRecording()
                    return true
                }
                else -> {
                    return false
                }
            }
        }
        return false
    }


    override fun onMediaAdded(uri: Uri, mediaType: MediaChooser.MediaType) {
        when (mediaType) {
            MediaChooser.MediaType.IMAGE -> {
                Timber.d("Added Image: $uri")
                sendMessage(MessageType.IMAGE, uri.toString())
            }
            MediaChooser.MediaType.VIDEO -> {
                Timber.d("Added Video: $uri")
                sendMessage(MessageType.VIDEO, uri.toString())
            }
            MediaChooser.MediaType.CONTACT -> {
                Timber.d("Added Contact: $uri")
                sendMessage(MessageType.CONTACT, uri.toString())
            }
            MediaChooser.MediaType.FILE -> {
                Timber.d("Added File: $uri")
                sendMessage(MessageType.FILE, uri.toString())
            }
        }
    }

    companion object {

        private var MillisecondTime: Long = 0.toLong()
        private var StartTime: Long = 0.toLong()
        private var UpdateTime: Long = 0.toLong()

        private var seconds = 0
        private var minutes = 0

        private val handler: Handler = Handler()

        private const val voiceMessageLowerThreshold = 1

        private var fileName = ""

    }
}