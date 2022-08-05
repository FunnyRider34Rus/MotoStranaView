package com.elpablo.motostrana

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.scopes.ServiceScoped

@ServiceScoped
class PushNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val remoteMessageData = remoteMessage.data
            if (remoteMessageData.isNotEmpty()) {
                val title = remoteMessageData[PUSH_KEY_TITLE]
                val user = remoteMessageData[PUSH_KEY_USER]
                val message = remoteMessageData[PUSH_KEY_MESSAGE]
                if (!title.isNullOrBlank()&&!message.isNullOrBlank()) {
                    pushNotification(title, user, message)
                }
            }
        }
    }

    private fun pushNotification(title: String, user: String?, message: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilderNews = NotificationCompat.Builder(this, CHANNEL_NEWS).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(message)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.home_tab_news)
            val channelDescription = "Оповещение о появлении новостей"
            val channelPriority = NotificationManager.IMPORTANCE_DEFAULT
            val channelNews = NotificationChannel(CHANNEL_NEWS, channelName, channelPriority).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channelNews)
        }
        notificationManager.notify((1..10).random(), notificationBuilderNews.build())

    }

    companion object {
        private const val TAG = "PUSH NOTIFICATION"
        private const val PUSH_KEY_TITLE = "PUSH TITLE"
        private const val PUSH_KEY_USER = "PUSH USER"
        private const val PUSH_KEY_MESSAGE = "PUSH MESSAGE"
        private const val CHANNEL_NEWS = "NEWS"
        private const val CHANNEL_EVENT = "EVENT"
        private const val CHANNEL_STATE = "STATE"
        private const val CHANNEL_CITY = "CITY"
        private const val CHANNEL_CHANNEL = "CHANNEL"
    }
}