package org.nfqws2.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.concurrent.Executors

class CommandReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        Executors.newSingleThreadExecutor().execute {
            try {
                when (intent.action) {
                    START -> ModuleController.start()
                    STOP -> ModuleController.stop()
                    RESTART -> ModuleController.restart()
                }
                NotificationController.show(context, Nfqws2Status.read(context))
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        const val START = "org.nfqws2.helper.START"
        const val STOP = "org.nfqws2.helper.STOP"
        const val RESTART = "org.nfqws2.helper.RESTART"
        const val REFRESH = "org.nfqws2.helper.REFRESH"
    }
}
