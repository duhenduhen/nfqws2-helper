package org.nfqws2.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.concurrent.Executors

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED) return
        val pending = goAsync()
        Executors.newSingleThreadExecutor().execute {
            try { NotificationController.show(context, Nfqws2Status.read(context)) } finally { pending.finish() }
        }
    }
}
