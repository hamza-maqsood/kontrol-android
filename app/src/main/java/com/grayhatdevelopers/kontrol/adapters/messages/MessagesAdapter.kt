package com.grayhatdevelopers.kontrol.adapters.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.adapters.DataBoundListAdapter
import com.grayhatdevelopers.kontrol.database.messages.MessageEntity
import com.grayhatdevelopers.kontrol.databinding.ItemMessageReceivedBinding
import com.grayhatdevelopers.kontrol.databinding.ItemMessageSentBinding
import com.grayhatdevelopers.kontrol.models.message.MessageActions
import com.grayhatdevelopers.kontrol.models.message.MessageMediaState
import com.grayhatdevelopers.kontrol.models.message.MessageViewModel
import com.grayhatdevelopers.kontrol.models.message.MessageViewModelFactory
import com.grayhatdevelopers.kontrol.utils.hide
import com.grayhatdevelopers.kontrol.utils.show
import com.grayhatdevelopers.kontrol.utils.toast
import timber.log.Timber

class MessagesAdapter(
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner
) : DataBoundListAdapter<MessageEntity>(
    diffCallback = object : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem == newItem
        }
    }, context = context
) {

    private lateinit var source: ByteArray

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            when (viewType) {
                R.layout.item_message_received -> R.layout.item_message_received
                R.layout.item_message_sent -> R.layout.item_message_sent
                else -> throw IllegalArgumentException("Unexpected viewType")
            },
            parent,
            false
        )
    }

    override fun getItemViewType(position: Int): Int =
        if (currentList[position].isMessageSent) MessageViewItem.Sent.resource
        else MessageViewItem.Received.resource

    override fun bind(binding: ViewDataBinding, item: MessageEntity, position: Int) {

        val viewModel =
            MessageViewModelFactory(item, binding.root.context).create(MessageViewModel::class.java)
        when (binding) {
            is ItemMessageReceivedBinding -> {
                binding.viewModel = viewModel
                binding.message = item

                /**
                 * update the views according to message media states
                 */
                viewModel.messageMediaState.observe(viewLifecycleOwner) {
                    when (it) {
                        MessageMediaState.DOWNLOAD_IN_PROGRESS -> {
                            Timber.d("Downloading Message Media: ${item.messageID}")
                        }
                        MessageMediaState.DOWNLOADED -> {
                            Timber.d("Downloaded Message Media: ${item.messageID}")
                            binding.relativeLayout.hide()
                        }
                        null -> {
                            throw IllegalArgumentException("Message State can't be null")
                        }
                        else -> Timber.d("Message State${it}: ${item.messageID}")
                    }
                }
            }

            // for sent messages
            is ItemMessageSentBinding -> {
                binding.viewModel = viewModel
                binding.message = item

                /**
                 * update the views according to message media states
                 */
                viewModel.messageMediaState.observe(viewLifecycleOwner) {
                    when (it) {
                        MessageMediaState.UPLOADING -> {
                            Timber.d("Uploading Message Media: ${item.messageID}")
                            binding.relativeLayout.show()
                        }
                        MessageMediaState.SENT -> {
                            Timber.d("Message Sent, Changing Status: ${item.messageID}")
                            binding.relativeLayout.hide()
                            binding.messageStatusIndicator.show()
                        }
                        null -> {
                            throw IllegalArgumentException("Message State can't be null")
                        }
                        else -> Timber.d("Message State${it}: ${item.messageID}")
                    }
                }

                /**
                 * if the message media is playable, show play button
                 */
                viewModel.isMediaPlayable.observe(viewLifecycleOwner) {
                    if (it) {
                        Timber.d("Showing Play Button")
                        with(binding) {
                            if (!(::source.isInitialized)) {
                                source = context.assets.open("sample_audio.wav").readBytes()
                                Timber.d("Source is: $source")
                            }
                            audioWaveMsg.setRawData(source)
                            messageStatusIndicator.show()
                            relativeLayout.show()
                            voiceMsgController.setImageResource(R.drawable.play_icon_purple)
                            voiceMsgPb.hide()
                            progressView.show()
                        }

                    }
                }

                /**
                 * adjust icon according to media play states
                 */
                viewModel.isAudioPlaying.observe(viewLifecycleOwner) {
                    if (it) {
                        binding.audioWaveMsg.animate()
                        binding.voiceMsgController.setImageResource(R.drawable.pause_icon_purple)
                    } else {
                        binding.audioWaveMsg.clearAnimation()
                        binding.voiceMsgController.setImageResource(R.drawable.play_icon_purple)
                    }
                }

                /**
                 * if the message only contains text, then no need to show progress bar
                 */
                viewModel.isMessageText.observe(viewLifecycleOwner) {
                    if (it) {
                        Timber.d("Hiding the indicator")
                        binding.relativeLayout.hide()
                    }
                }

                /**
                 * observe for message actions
                 */
                viewModel.messageActions.observe(viewLifecycleOwner) {
                    when (it) {
                        MessageActions.NetworkError -> {
                            Timber.d("Internet connection error!")
                            context.toast("Make sure you've got a stable internet connection!")
                        }
                        is MessageActions.OpenImage -> {
                            Timber.d("Enlarging Image: ${it.imageURI}")
                            mEnlargeImageRequestListener?.enlargeImage(it.imageURI)
                        }
                    }
                }

                /**
                 * sync audio when user changes the audio wave
                 */
                binding.audioWaveMsg.onProgressChanged = { progress, byUser ->
                    Timber.d("New Progress is: $progress, and was it changed by the user: $byUser")
                    // todo: update media player
                }
            }
        }
    }

    interface EnlargeImageRequestListener {
        fun enlargeImage(imageURI: String)
    }

    companion object {
        private var mEnlargeImageRequestListener: EnlargeImageRequestListener? = null
        fun bindEnlargeImageRequestListener(listener: EnlargeImageRequestListener) {
            mEnlargeImageRequestListener = listener
        }
    }

}