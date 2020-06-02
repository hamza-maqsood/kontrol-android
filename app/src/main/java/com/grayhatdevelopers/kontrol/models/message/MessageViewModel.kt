 package com.grayhatdevelopers.kontrol.models.message

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import com.grayhatdevelopers.kontrol.utils.isInternetAvailable
import kotlinx.coroutines.launch
import org.kodein.di.direct
import org.kodein.di.generic.instance
import timber.log.Timber
import java.io.File

class MessageViewModel constructor(
    context: Context,
    private val messageEntity: MessageEntity
) : BaseViewModel(context) {

    val messageActions: SingleLiveEvent<MessageActions> = SingleLiveEvent()

    val messageMediaState: MutableLiveData<MessageMediaState> = MutableLiveData()
    val messageState: MutableLiveData<MessageState> = MutableLiveData()
    val isMediaPlayable: MutableLiveData<Boolean> = MutableLiveData()
    val isMessageText: MutableLiveData<Boolean> = MutableLiveData()
    val isAudioPlaying: MutableLiveData<Boolean> = MutableLiveData()
    val audioProgress: MutableLiveData<Int> = MutableLiveData()

    private var isMediaPlaying = false
    private var playFromStart = true
    private var isMediaPlayerInitialized = false
    private var currentLength = 0
    private var progressJump = 0
    private var mediaPlayer: MediaPlayer? = null
    private val progressHandler = Handler()
    private var progressTracker: Runnable = Runnable {
        updateAudioWave()
    }

    init {
        repo.removeFromNewMessages(id = messageEntity.messageID)
        isMediaPlayable.postValue(false)
        audioProgress.postValue(0)
        executePendingMessageOperations()
    }

    fun playAudio() {
        if (isMediaPlayable.value == true) {
            if (!isMediaPlaying) {
                // play the audio
                if (mediaPlayer == null)
                    mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer?.let {
                        if (!playFromStart)
                            mediaPlayer?.seekTo(currentLength)
                        if (!isMediaPlayerInitialized) {
                            it.setDataSource(messageEntity.messageData)
                            it.prepare()
                            isMediaPlayerInitialized = true
                        }
                        it.start()
                        progressJump = 100 / (it.duration / 1000)
                        progressTracker.run()
                        it.setOnCompletionListener {
                            currentLength = 0
                            audioProgress.postValue(0)
                            playFromStart = true
                            stopAudio()
                        }
                    }
                    isMediaPlaying = true
                    isAudioPlaying.postValue(true)
                } catch (e: Exception) {
                    Timber.d(e)
                }
            } else {
                // pause the audio
                stopAudio()
                playFromStart = false
                mediaPlayer?.pause()
                currentLength = mediaPlayer?.currentPosition ?: 0
            }
        }
    }

    fun enlargeImage() {
        messageActions.postValue(MessageActions.OpenImage(messageEntity.messageData))
    }

    // todo: make audio wave draggable
    // todo: make the previous one stop
    // todo: make random bytes streams
    private fun stopAudio() {
        isMediaPlaying = false
        isAudioPlaying.postValue(false)
        progressHandler.removeCallbacks(progressTracker)
    }

    private fun executePendingMessageOperations() {
        messageEntity.let {
            if (it.messageState == MessageState.QUEUED) {
                messageMediaState.postValue(it.messageMediaState)
                if (it.messageType == MessageType.TEXT || it.messageType == MessageType.CONTACT) {
                    // text messages or contact messages doesn't need any data to be uploaded
                    isMessageText.postValue(true)
                    sendMessage()
                } else viewModelScope.launch {
                    if (messageEntity.isMessageSent)
                        messageMediaState.postValue(MessageMediaState.UPLOADING)
                    else messageMediaState.postValue(MessageMediaState.DOWNLOAD_IN_PROGRESS)
                    // sign in to firebase storage
                    val loginStatus = repo.firebase.firebaseAuthDAO.loginUser()
                    if (loginStatus) {
                        // upload the media to firebase storage
                        // todo: handle upload error
                        val uri = repo.firebase.firebaseStorageDAO.uploadMediaToFirebaseStorage(
                            fileUri = if (messageEntity.messageType == MessageType.AUDIO)
                                Uri.fromFile(File(it.messageData))
                            else Uri.parse(it.messageData),
                            userID = it.participant
                        )
                        // update message media URI
                        it.messageData = uri.toString()
                        // send message
                        sendMessage()
                    } else Timber.d("Error Signing in to firebase storage")
                }
            } else if (it.messageState == MessageState.SENT) {
                if (it.messageType == MessageType.AUDIO) {
                    isMediaPlayable.postValue(true)
                } else {
                    Timber.d("Message Media Type: ${it.messageType}")
                }
                if (it.messageType != MessageType.AUDIO)
                    messageMediaState.postValue(MessageMediaState.SENT)
                else Timber.d("Message Media is not playable")
            } else {
                Timber.d("Message State: ${it.messageState}")
            }
        }
    }

    private fun updateAudioWave() {
        val newProgress = (mediaPlayer!!.currentPosition / 1000) * progressJump
        audioProgress.postValue(newProgress)
        progressHandler.postDelayed(progressTracker, 500)
    }

    private fun sendMessage() {
        if (isInternetAvailable(kodein.direct.instance())) {
            viewModelScope.launch {
                // upload to the server
                repo.sendMessageToServer(messageEntity)
                messageEntity.messageState = MessageState.SENT
                messageMediaState.postValue(MessageMediaState.SENT)
                // show play button if it's a voice message
                isMediaPlayable.postValue(messageEntity.messageType == MessageType.AUDIO)
                // update message status to show sent image
                messageState.postValue(MessageState.SENT)
                // update local copy
                repo.updateMessageState(
                    messageEntity.messageID,
                    messageEntity.messageState
                )
            }
            Timber.d("Message Sent: $messageEntity")
        } else {
            messageActions.postValue(MessageActions.NetworkError)
        }
    }
}