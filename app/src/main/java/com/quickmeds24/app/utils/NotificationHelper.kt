package com.quickmeds24.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.quickmeds24.app.R
import com.quickmeds24.app.data.model.InAppPushNotification
import com.quickmeds24.app.data.model.PushType

/**
 * NotificationHelper
 * Posts system-level Android notifications for rider milestones
 * (Order Picked Up, Driver Arrived).
 */
object NotificationHelper {

    private const val CHANNEL_ID = "qwikmeds_order_tracking"
    private const val CHANNEL_NAME = "Live Order Tracking"

    fun showSystemNotification(context: Context, push: InAppPushNotification) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time updates on rider location and delivery arrival"
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(push.title)
            .setContentText(push.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(push.message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notifId = if (push.type == PushType.ORDER_PICKED) 1001 else 1002
        manager.notify(notifId, notification)
    }
}

