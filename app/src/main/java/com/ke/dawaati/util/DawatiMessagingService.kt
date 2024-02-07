package com.ke.dawaati.util

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DawatiMessagingService : FirebaseMessagingService() {

    private var broadcaster: LocalBroadcastManager? = null

    override fun onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onCre

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }

    // this is called when a message is received
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // check messages
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload, you can get the payload here and add as an intent to your activity
        remoteMessage.data.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data.toString())
        }

        // Check if message contains a notification payload, send notification
        remoteMessage.notification?.let {
            val intent = Intent("DawatiNotification")
            intent.putExtra("title", it.title)
            intent.putExtra("body", it.body)
            broadcaster?.sendBroadcast(intent)
        }
    }

    override fun onNewToken(token: String) {
        Log.d("rfst", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // you can send the updated value of the token to your server here
    }
}
