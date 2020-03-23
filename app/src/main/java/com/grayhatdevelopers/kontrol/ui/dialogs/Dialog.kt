package com.grayhatdevelopers.kontrol.ui.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.grayhatdevelopers.kontrol.R
import com.grayhatdevelopers.kontrol.utils.remove
import kotlinx.android.synthetic.main.dialog_layout.*

class Dialog(
    context: Context,
    private val dialogType: DialogType
) : AlertDialog(context) {

    private var mOnPositiveButtonClicked: OnPositiveButtonClicked? = null
    private var mOnNegativeButtonClicked: OnNegativeButtonClicked = object :
        OnNegativeButtonClicked {
        override fun onNegativeButtonClicked() {
            dismiss()
        }
    }

    private var mPositiveButtonDrawable = context.getDrawable(R.drawable.neutral_btn_bg)
    var title = "Title"
    var message = "Message"
    var positiveButtonText = ""
    var negativeButtonText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_layout)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initView()
    }

    private fun initView() {
        when (dialogType) {
            DialogType.SUCCESS -> {
                icon.setImageDrawable(context.getDrawable(R.drawable.checked))
            }

            DialogType.ERROR -> {
                icon.setImageDrawable(context.getDrawable(R.drawable.error))
                mPositiveButtonDrawable = context.getDrawable(R.drawable.error_btn_bg)
            }

            DialogType.WARNING -> {
                icon.setImageDrawable(context.getDrawable(R.drawable.alert))
                mPositiveButtonDrawable = context.getDrawable(R.drawable.warning_btn_bg)
            }

            DialogType.INFO -> {
                icon.setImageDrawable(context.getDrawable(R.drawable.info))
            }
        }

        mOnPositiveButtonClicked ?: positive_btn.remove()
        title_tv.text = title
        message_tv.text = message
        negative_btn.text = negativeButtonText
        positive_btn.text = positiveButtonText
        positive_btn.background = mPositiveButtonDrawable

        positive_btn.setOnClickListener {
            mOnPositiveButtonClicked?.onPositiveButtonClicked()
        }

        negative_btn.setOnClickListener {
            mOnNegativeButtonClicked.onNegativeButtonClicked()
        }
    }

    enum class DialogType {
        SUCCESS, ERROR, WARNING, INFO
    }

    fun setOnPositiveButtonClickedListener(listener: OnPositiveButtonClicked) {
        mOnPositiveButtonClicked = listener
    }

    fun setOnNegativeButtonClickedListener(listener: OnNegativeButtonClicked) {
        mOnNegativeButtonClicked = listener
    }

    interface OnPositiveButtonClicked {
        fun onPositiveButtonClicked()
    }

    interface OnNegativeButtonClicked {
        fun onNegativeButtonClicked()
    }
}