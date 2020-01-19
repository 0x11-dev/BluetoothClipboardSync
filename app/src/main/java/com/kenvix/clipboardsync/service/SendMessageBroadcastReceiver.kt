//--------------------------------------------------
// Class MessageIntentService
//--------------------------------------------------
// Written by Kenvix <i@kenvix.com>
//--------------------------------------------------

package com.kenvix.clipboardsync.service

import android.app.RemoteInput
import android.content.*
import android.os.IBinder
import com.kenvix.utils.log.Logging
import com.kenvix.utils.android.bindService

class SendMessageBroadcastReceiver : BroadcastReceiver(), Logging {

    override fun onReceive(context: Context, intent: Intent) {
        val data = if (intent.getBooleanExtra("is_from_notification", false)) {
            val inputBundle = RemoteInput.getResultsFromIntent(intent)
            inputBundle.getString("message")
        } else {
            intent.getStringExtra("message")
        }

        if (data == null) {
            logger.severe("Invoked but no notification_send_message given")
            return
        }

        logger.finest("Invoked with message: $data")
        sendMessage(context, data)
    }

    private fun sendMessage(context: Context, text: String) {
        context.bindService(SyncService::class.java, object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                service as SyncService.Binder
                service.sendDataAsync(RfcommFrame.TypeEmergency, RfcommFrame.OptionNone, text.toByteArray())
            }
        })
    }

    override fun getLogTag(): String = "SendMessageBroadcastReceiver"
}