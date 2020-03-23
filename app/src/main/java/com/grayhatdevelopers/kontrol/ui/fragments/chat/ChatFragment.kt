package com.grayhatdevelopers.kontrol.ui.fragments.chat

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.databinding.FragmentChatBinding
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.InjectorUtils
import com.grayhatdevelopers.kontrol.utils.PhotoChooser
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions

class ChatFragment : BaseFragment() {

    private lateinit var mViewModel: ChatViewModel
    private lateinit var fragmentChatBinding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat,
            container,
            false
        )
        fragmentChatBinding.apply {
            lifecycleOwner = this@ChatFragment
            mViewModel = InjectorUtils.provideChatViewModel()
            viewModel = mViewModel
        }
        return fragmentChatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentChatBinding.ripplePulse.startRippleAnimation()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeChatActions()
    }

    private fun observeChatActions() {
        mViewModel.chatActions.observe(viewLifecycleOwner) {
            when (it) {
                ChatActions.ShowAttachmentsOption -> {
                    Log.d(TAG, "Show Attachment Options Selected")
                }
                is ChatActions.SelectAttachment -> {
                    Log.d(TAG, "Select Attachment: $it")
                    when (it.attachmentTypes) {
                        AttachmentTypes.MEDIA -> {
                            PhotoChooser(context = activity!!).openAttachmentBottomSheet()
                            // todo bind the listener
                        }
                        AttachmentTypes.FILE -> {
                            selectFile()
                            // todo bind the listener
                        }
                        AttachmentTypes.CONTACT -> {
                            selectContact()
                            // todo bind the listener
                        }
                    }
                }
                ChatActions.SendMessage -> {
                    Log.d(TAG, "Send Message")
                }
                ChatActions.TypingStarted -> {
                    Log.d(TAG, "Started Typing")
                    fragmentChatBinding.sendButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.send
                        )
                    )
                }
                ChatActions.TypingStopped -> {
                    Log.d(TAG, "Typing Stopped")
                    fragmentChatBinding.sendButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            context!!,
                            R.drawable.microphone
                        )
                    )
                }
                ChatActions.RecordingStarted -> {
                    Log.d(TAG, "Recording Started")
                    fragmentChatBinding.ripplePulse.startRippleAnimation()
                }
                ChatActions.RecordingFinished -> {
                    Log.d(TAG, "Recording Finished")
                    fragmentChatBinding.ripplePulse.stopRippleAnimation()
                }
                ChatActions.RecordingCanceled -> {
                    Log.d(TAG, "Recording Canceled")
                    fragmentChatBinding.ripplePulse.stopRippleAnimation()
                    // todo do something to indicate CANCEL
                }
                ChatActions.GoBack -> {
                    Log.d(TAG, "Go Back")
                    baseViewModel.popBack()
                }
                ChatActions.PermissionsMissing -> {
                    Log.d(TAG, "Permissions missing...")
                    runWithPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) {}
                }
            }
        }
    }

    private fun selectFile() = this.runWithPermissions(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) {
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
        }
        this.startActivityForResult(intent, REQUEST_CODE_FILE)
    }

    private fun selectContact() = this.runWithPermissions(
        Manifest.permission.READ_CONTACTS
    ) {
        val intent = Intent().apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            action = Intent.ACTION_PICK
        }
        this.startActivityForResult(intent, REQUEST_CODE_CONTACT)
    }

    companion object {
        private const val TAG = "ChatFragment"
        const val REQUEST_CODE_CONTACT = 33
        const val REQUEST_CODE_FILE = 44
    }
}