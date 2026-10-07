package org.nfqws2.helper

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationController {
    const val channelId = "nfqws2_status"
    private const val notificationId = 2082

    fun show(context: Context, status: Nfqws2Status) {
        if (status.error != null) return
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, context.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_LOW)
            )
        }
        val title = when {
            status.isPaused -> context.getString(R.string.notification_title_paused)
            status.running -> context.getString(R.string.notification_title_running)
            else -> context.getString(R.string.notification_title_stopped)
        }
        val text = if (status.running) {
            context.getString(R.string.notification_strategy, status.strategy, status.pid.ifBlank { "—" })
        } else if (status.isPaused) {
            context.getString(R.string.notification_home_wifi, status.paused)
        } else context.getString(R.string.notification_disabled)

        fun action(name: String, requestCode: Int): PendingIntent = PendingIntent.getBroadcast(
            context, requestCode,
            Intent(context, CommandReceiver::class.java).setAction(name),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_nfqws2_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setOngoing(status.running)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setColor(ContextCompat.getColor(context, R.color.nfqws2_notification_accent))
        if (status.running) builder.addAction(0, context.getString(R.string.action_stop), action(CommandReceiver.STOP, 1))
        else builder.addAction(0, context.getString(R.string.action_start), action(CommandReceiver.START, 2))
        builder.addAction(0, context.getString(R.string.action_refresh), action(CommandReceiver.REFRESH, 3))
        manager.notify(notificationId, builder.build())
    }
}
