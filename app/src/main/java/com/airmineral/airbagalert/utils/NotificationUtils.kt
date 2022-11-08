package com.airmineral.airbagalert.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.airmineral.airbagalert.R
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.ui.incident.AlertActivity

fun Context.showNotificationWithFullScreenIntent(
    data: Incident
) {
    val title = "🚨 Telah Terjadi Kecelakaan Mobil"
    val description = "🚗 ${data.car_model} dengan nopol ${data.car_id}"

    val soundUri = Uri.parse("android.resource://$packageName/${R.raw.crash}")
    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notif)
        .setContentTitle(title).setContentText(description).setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_VIBRATE)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setFullScreenIntent(getFullScreenIntent(data), true).setSound(soundUri)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    with(notificationManager) {
        buildChannel(this@showNotificationWithFullScreenIntent)

        val notification = builder.build()

        notify(0, notification)
    }
}

private fun NotificationManager.buildChannel(context: Context) {
    val soundUri = Uri.parse("android.resource://$context.packageName/${R.raw.crash}")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Airbag Alert Notification Channel"
        val descriptionText =
            "This is used to notify the user about airbag alert in Full Screen Intent"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableLights(true)
            enableVibration(true)
            lightColor = android.graphics.Color.RED
        }
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
            .let { channel.setSound(soundUri, it) }

        createNotificationChannel(channel)
    }
}

private fun Context.getFullScreenIntent(data: Incident): PendingIntent {
    val destination = AlertActivity::class.java
    val intent = Intent(this, destination)
    intent.putExtra("DATA", data)

    return PendingIntent.getActivity(
        this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private const val CHANNEL_ID = "mantap-jiwa"
