package com.grayhatdevelopers.kontrol.views

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View.OnKeyListener
import android.view.View.OnTouchListener
import android.widget.EditText
import android.widget.LinearLayout
import com.grayhatdevelopers.kontrol.R
import kotlinx.android.synthetic.main.view_otp_pin.view.*

class OTPPinView : LinearLayout {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    )
            : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    )
            : super(context, attrs, defStyleAttr, defStyleRes)

    private val textWatcher: TextWatcher
    private val keyListener: OnKeyListener
    private val onTouchListener: OnTouchListener
    private val editTexts = arrayListOf<EditText>()
    private lateinit var currentFocus: EditText

    init {
        //inflate the layout components
        LayoutInflater.from(context).inflate(R.layout.view_otp_pin, this, true)

        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing to do here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing to do here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val view = editTexts.find {
                    it == currentFocus
                }
                if (view != null && count != 0) {
                    val index = editTexts.indexOf(view)
                    if (index != editTexts.lastIndex) {
                        editTexts[index + 1].requestFocus()
                        currentFocus = editTexts[index + 1]
                    }
                }

                with(getEnteredPin()) {
                    if (this.length == 4) {
                        // input is completed, notify the listener
                        mOnInputCompleteListener?.onInputCompleteListener(this)
                    }
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        onTouchListener = OnTouchListener { view, _ ->
            currentFocus = view as EditText
            return@OnTouchListener false
        }

        keyListener = OnKeyListener { view, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                val editText = editTexts.find {
                    it == view
                }
                if (editText != null) {
                    val length = editText.text.length
                    if (length == 0) {
                        val index = editTexts.indexOf(editText)
                        if (index != 0) {
                            editTexts[index - 1].requestFocus()
                            currentFocus = editTexts[index - 1]
                        }
                    }
                }
            }
            return@OnKeyListener false
        }

        // add all edit texts to a list
        editTexts.add(pin1)
        editTexts.add(pin2)
        editTexts.add(pin3)
        editTexts.add(pin4)

        // add text watcher
        editTexts.forEach {
            it.addTextChangedListener(textWatcher)
            it.setOnKeyListener(keyListener)
            it.setOnTouchListener(onTouchListener)
        }

        currentFocus = editTexts.first()
    }

    fun isKeyboardEnabled(enable: Boolean) {
        editTexts.forEach {
            it.isEnabled = enable
        }
    }

    fun input(key: Int) {
        if (key == -1) {
            if (currentFocus.text.toString().isBlank()) {
                // get to the previous field, if current focus is not on the first field
                if (currentFocus != editTexts.first()) {
                    val index = editTexts.indexOf(currentFocus)
                    currentFocus = editTexts[index - 1]
                    currentFocus.setText("")
                }
            } else {
                // set the text to blank of the current field
                currentFocus.setText("")
            }
        } else {
            // set the text to the current field
            currentFocus.setText(key.toString())
            // prompt the focus to the next field
//            if (editTexts.last() != currentFocus) {
//                currentFocus = editTexts[editTexts.indexOf(currentFocus) + 1]
//            }
        }
    }

    private fun getEnteredPin() = StringBuilder().apply {
        editTexts.forEach{
            append(it.text.toString())
        }
    }.toString().trim()

    interface OnInputCompleteListener {
        fun onInputCompleteListener(input: String)
    }

    companion object {
        private var mOnInputCompleteListener: OnInputCompleteListener? = null

        fun bindOnInputCompleteListener(listener: OnInputCompleteListener) {
            mOnInputCompleteListener = listener
        }
    }

}