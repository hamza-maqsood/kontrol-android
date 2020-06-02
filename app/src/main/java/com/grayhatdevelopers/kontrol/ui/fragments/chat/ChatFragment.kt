package com.grayhatdevelopers.kontrol.ui.fragments.chat

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroupOverlay
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.adapters.messages.MessagesAdapter
import com.grayhatdevelopers.kontrol.databinding.FragmentChatBinding
import com.grayhatdevelopers.kontrol.di.kodeinViewModel
import com.grayhatdevelopers.kontrol.ui.fragments.base.BaseFragment
import com.grayhatdevelopers.kontrol.utils.MediaChooser
import com.grayhatdevelopers.kontrol.utils.toast
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import timber.log.Timber

class ChatFragment : BaseFragment(), MessagesAdapter.EnlargeImageRequestListener {

    private val mViewModel: ChatViewModel by kodeinViewModel()
    private lateinit var fragmentChatBinding: FragmentChatBinding
    private lateinit var bottomSheet: BottomSheetBehavior<FrameLayout>
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MessagesAdapter.bindEnlargeImageRequestListener(this)
    }

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
            viewModel = mViewModel
        }
        return fragmentChatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bottomSheet = BottomSheetBehavior.from(fragmentChatBinding.chatStandardBottomSheet)
        setBottomSheetStateChangeListener()
        initMessagesRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeChatActions()
        observeMessages()
    }

    private fun observeChatActions() {
        mViewModel.chatActions.observe(viewLifecycleOwner) {
            when (it) {
                ChatActions.ShowAttachmentsOption -> {
                    Timber.d("Show Attachment Options Selected")
                    bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }
                is ChatActions.SelectAttachment -> {
                    Timber.d("Select Attachment: $it")
                    // hide the bottom sheet
                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                    // delegate request to MediaChooser
                    when (it.attachmentTypes) {
                        AttachmentTypes.MEDIA -> {
                            MediaChooser(context = requireActivity()).openAttachmentBottomSheet()
                        }
                        AttachmentTypes.FILE -> {
                            MediaChooser(context = requireActivity()).selectFile()
                        }
                        AttachmentTypes.CONTACT -> {
                            MediaChooser(context = requireActivity()).selectContact()
                        }
                        AttachmentTypes.LOCATION -> {
                            // todo here
                            context?.toast("Sharing location will become live soon!")
                        }
                    }
                }
                ChatActions.SendMessage -> {
                    Timber.d("Send Message")
                }
                ChatActions.TypingStarted -> {
                    Timber.d("Started Typing")
                    fragmentChatBinding.sendButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.send
                        )
                    )
                }
                ChatActions.TypingStopped -> {
                    Timber.d("Typing Stopped")
                    fragmentChatBinding.sendButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.microphone
                        )
                    )
                }
                ChatActions.RecordingStarted -> {
                    Timber.d("Recording Started")
                }
                ChatActions.RecordingFinished -> {
                    Timber.d("Recording Finished")
                }
                ChatActions.RecordingCanceled -> {
                    Timber.d("Recording Canceled")
                    // todo do something to indicate CANCEL
                }
                ChatActions.GoBack -> {
                    Timber.d("Go Back")
                    popBackToPrevious()
                }
                ChatActions.PermissionsMissing -> {
                    Timber.d("Permissions missing...")
                    runWithPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) {}
                }
            }
        }
    }

    private fun observeMessages() {
        mViewModel.messagesList.observe(viewLifecycleOwner) { list ->
            if (::messagesAdapter.isInitialized)
                messagesAdapter.submitList(list) {
                    fragmentChatBinding.messagesList.scrollToPosition(if (list.isNotEmpty()) list.size - 1 else 0)
                }
            else Timber.d("Messages Adapter is not initialized yet!")
        }
    }

    private fun initMessagesRecyclerView() {
        messagesAdapter = MessagesAdapter(
            requireContext(),
            this@ChatFragment
        )
        fragmentChatBinding.messagesList.apply {
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setBottomSheetStateChangeListener() {
        bottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
                Timber.d("Bottom sheet slided")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fragmentChatBinding.rootContainer.apply {
                        val dim: Drawable = ColorDrawable(Color.BLACK)
                        dim.setBounds(0, 0, width, height)
                        dim.alpha = (255 * 0.5.toFloat()).toInt()

                        val overlay: ViewGroupOverlay = overlay
                        overlay.add(dim)
                        isClickable = false
                    }
                } else {
                    fragmentChatBinding.rootContainer.apply {
                        overlay.clear()
                    }
                }
            }
        })
    }

    override fun enlargeImage(imageURI: String) {
        // open zoomable image view fragment
        val bundle = bundleOf("imageURI" to imageURI)
        navigateTo(ChatFragmentDirections.actionChatFragmentToZoomableImageViewFragment(imageURI), bundle)
    }
}