package com.grayhatdevelopers.kontrol.application

import android.content.Context
import android.content.IntentFilter
import android.provider.Telephony
import androidx.multidex.MultiDexApplication
import com.grayhatdevelopers.kontrol.receivers.SMSReceiver

class MainApplication : MultiDexApplication() {

    private lateinit var smsBroadcastReceiver: SMSReceiver

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        smsBroadcastReceiver = SMSReceiver()
        registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )
    }

    override fun onTerminate() {
        unregisterReceiver(smsBroadcastReceiver)
        super.onTerminate()

    }

    companion object {
        var context: Context? = null
    }
}