package com.example.musicplayer

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.musicplayer.data.Song
import com.example.musicplayer.data.SongSource

class MusicServiceNotification(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        val COUNTER_CHANNEL_ID = "musicPlayer_channel"

        enum class Actions {
            PLAY, NEXT, PREV
        }
    }

    fun showNotification(currentSong: Song = SongSource.collection.first(), isPlaying: Boolean = false) {

        val pendingIntentPrevious: PendingIntent?

        if (SongSource.collection.indexOf(currentSong) == 0) {
            pendingIntentPrevious = null
        } else {
            pendingIntentPrevious = PendingIntent.getBroadcast(
                context, 0,
                Intent(context, MusicPlayerReceiver::class.java).setAction(Actions.PREV.toString()),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val pendingIntentPlay = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, MusicPlayerReceiver::class.java).setAction(Actions.PLAY.toString()),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pendingIntentNext: PendingIntent?

        if (SongSource.collection.indexOf(currentSong) == SongSource.collection.lastIndex) {
            pendingIntentNext = null
        } else {
            pendingIntentNext = PendingIntent.getBroadcast(
                context, 0,
                Intent(context, MusicPlayerReceiver::class.java).setAction(Actions.NEXT.toString()),
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notificationLayout = RemoteViews(context.packageName, R.layout.notification)

        notificationLayout.setTextViewText(R.id.notification_title, currentSong.name)
        notificationLayout.setTextViewText(R.id.notification_info, currentSong.author)

        if (pendingIntentPrevious != null) {
            notificationLayout.setViewVisibility(R.id.notification_prev_button, View.VISIBLE)
            notificationLayout.setOnClickPendingIntent(R.id.notification_prev_button, pendingIntentPrevious)
            notificationLayout.setImageViewResource(R.id.notification_prev_button, R.drawable.prev_24)
        } else {
            notificationLayout.setViewVisibility(R.id.notification_prev_button, View.INVISIBLE)
        }

        notificationLayout.setOnClickPendingIntent(R.id.notification_play_button, pendingIntentPlay)
        if(isPlaying) notificationLayout.setImageViewResource(R.id.notification_play_button, R.drawable.pause_24)
        else notificationLayout.setImageViewResource(R.id.notification_play_button, R.drawable.play_24)

        if (pendingIntentNext != null) {
            notificationLayout.setViewVisibility(R.id.notification_next_button, View.VISIBLE)
            notificationLayout.setOnClickPendingIntent(R.id.notification_next_button, pendingIntentNext)
            notificationLayout.setImageViewResource(R.id.notification_next_button, R.drawable.next_24)
        } else {
            notificationLayout.setViewVisibility(R.id.notification_next_button, View.INVISIBLE)
        }

        val notificationLayoutExpanded = RemoteViews(context.packageName, R.layout.notification_expanded)

        notificationLayoutExpanded.setTextViewText(R.id.expanded_notification_title, currentSong.name)
        notificationLayoutExpanded.setTextViewText(R.id.expanded_notification_info, currentSong.author)

        if (pendingIntentPrevious != null) {
            notificationLayoutExpanded.setViewVisibility(R.id.expanded_notification_prev_button, View.VISIBLE)
            notificationLayoutExpanded.setOnClickPendingIntent(R.id.expanded_notification_prev_button, pendingIntentPrevious)
            notificationLayoutExpanded.setImageViewResource(R.id.expanded_notification_prev_button, R.drawable.prev_24)
        } else {
            notificationLayoutExpanded.setViewVisibility(R.id.expanded_notification_prev_button, View.INVISIBLE)
        }

        notificationLayoutExpanded.setOnClickPendingIntent(R.id.expanded_notification_play_button, pendingIntentPlay)
        if(isPlaying) notificationLayoutExpanded.setImageViewResource(R.id.expanded_notification_play_button, R.drawable.pause_24)
        else notificationLayoutExpanded.setImageViewResource(R.id.expanded_notification_play_button, R.drawable.play_24)

        if (pendingIntentNext != null) {
            notificationLayoutExpanded.setViewVisibility(R.id.expanded_notification_next_button, View.VISIBLE)
            notificationLayoutExpanded.setOnClickPendingIntent(R.id.expanded_notification_next_button, pendingIntentNext)
            notificationLayoutExpanded.setImageViewResource(R.id.expanded_notification_next_button, R.drawable.next_24)
        } else {
            notificationLayoutExpanded.setViewVisibility(R.id.expanded_notification_next_button, View.INVISIBLE)
        }

        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setShowWhen(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()

        notificationManager.notify(1, notification)
    }
}