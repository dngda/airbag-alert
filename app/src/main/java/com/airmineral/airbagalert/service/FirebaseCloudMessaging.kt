package com.airmineral.airbagalert.service

import android.util.Log
import com.airmineral.airbagalert.data.model.Incident
import com.airmineral.airbagalert.utils.showNotificationWithFullScreenIntent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent

class FirebaseCloudMessaging : FirebaseMessagingService(), KoinComponent {
//    private val alertRepository: AlertRepository by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM", "Message data payload: ${message.data}")
        Log.d(
            "FCM",
            "Message notification => ${message.notification?.title} : ${message.notification?.body}"
        )

        val data = Incident(
            message.data["id"].toString(),
            message.data["latitude"]!!.toDouble(),
            message.data["longitude"]!!.toDouble(),
            message.data["date"].toString(),
            message.data["time"].toString(),
            message.data["car_id"].toString(),
            message.data["car_model"].toString(),
            message.data["damaged"].toString(),
            null,
        )

        Log.d("FCM", "Message data: $data")
        this.showNotificationWithFullScreenIntent(data)
//        alertRepository.saveAlertData(data)
    }
}