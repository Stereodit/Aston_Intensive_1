package com.example.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MusicPlayerApp: Application() {

    companion object {
        var isReceiverRegistered = false
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MusicServiceNotification.COUNTER_CHANNEL_ID,
                "Music player Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for music player control panel notifications"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}