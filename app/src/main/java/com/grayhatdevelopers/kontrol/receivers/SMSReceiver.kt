package com.grayhatdevelopers.kontrol.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage

class SMSReceiver : BroadcastReceiver() {
    private var bundle: Bundle? = null
    private var currentSMS: SmsMessage? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            bundle = intent.extras
            if (bundle != null) {
                val pduObjects = bundle!!.get("pdus") as Array<*>?
                if (pduObjects != null) {
                    for (aObject in pduObjects) {
                        currentSMS = getIncomingMessage(aObject!!, bundle!!)
                        mListener?.messageReceived(message = currentSMS!!)
                    }
                    this.abortBroadcast()
                }
            }
        }
    }

    interface IMessageListener {
        fun messageReceived(message: SmsMessage)
    }

    companion object {
        private var mListener: IMessageListener? = null
        fun bindMessageListener(listener: IMessageListener) {
            mListener = listener
        }
    }
}

@Suppress("DEPRECATION")
private fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage {
    val currentSMS: SmsMessage
    currentSMS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val format = bundle.getString("format")
        SmsMessage.createFromPdu(aObject as ByteArray, format)
    } else {
        SmsMessage.createFromPdu(aObject as ByteArray)
    }
    return currentSMS
}