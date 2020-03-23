package com.grayhatdevelopers.kontrol.ui.fragments.chat

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.grayhatdevelopers.kontrol.application.MainApplication.Companion.context
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseViewModel
import com.grayhatdevelopers.kontrol.utils.SingleLiveEvent
import com.grayhatdevelopers.kontrol.utils.getCurrentTime
import java.lang.Exception

class ChatViewModel : BaseViewModel(), View.OnTouchListener {

    private var recorder: MediaRecorder? = null

    val chatActions: SingleLiveEvent<ChatActions> = SingleLiveEvent()

    @Bindable
    val enteredMessage: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val recordingTime: MutableLiveData<String> = MutableLiveData()

    @Bindable
    val isRecording: MutableLiveData<Boolean> = MutableLiveData()

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

    fun performMessageAction() {

    }

    private fun sendMessage() {
        // todo here
    }


    @Suppress("UNUSED_PARAMETER")
    fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (before == 0) {
            chatActions.postValue(ChatActions.TypingStarted)
        } else if (count == 0) {
            chatActions.postValue(ChatActions.TypingStopped)
        }
    }

    private fun startRecording() {

        fileName = "${context?.externalCacheDir?.absolutePath}/voice_msg${getCurrentTime()}.3gp"

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            try {
                prepare()
            } catch (e: Exception) {
                Log.e(TAG, "prepare() failed: ${e.message}")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        v?.let {
            it.performClick()
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> { /* button is on HOLD */
                    if (!arePermissionsAllowed()) {
                        chatActions.postValue(ChatActions.RecordingStarted)
                        isRecording.postValue(true)
                        StartTime = SystemClock.uptimeMillis()
                        mRunnable.run()
                        startRecording()
                    } else chatActions.postValue(ChatActions.PermissionsMissing)
                    return true
                }
                MotionEvent.ACTION_UP -> { /* button is released */
                    chatActions.postValue(ChatActions.RecordingFinished)
                    isRecording.postValue(false)
                    // remove callback
                    handler.removeCallbacks(mRunnable)
                    // reset the timer
                    MillisecondTime = 0
                    UpdateTime = 0
                    StartTime = 0
                    seconds = 0
                    minutes = 0
                    stopRecording()
                    return true
                }
                else -> {
                    Log.d(TAG, "Motion Event: $event")
                    return false
                }
            }
        }
        return false
    }

    private fun arePermissionsAllowed(): Boolean = (
            Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED)


    companion object {
        private const val TAG = "ChatViewModel"

        private var MillisecondTime: Long = 0.toLong()
        private var StartTime: Long = 0.toLong()
        private var UpdateTime: Long = 0.toLong()

        private var seconds = 0
        private var minutes = 0

        private val handler: Handler = Handler()

        private var fileName = ""

    }


}