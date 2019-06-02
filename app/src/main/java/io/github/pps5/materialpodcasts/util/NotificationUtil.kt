package io.github.pps5.materialpodcasts.util

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat.*
import io.github.pps5.materialpodcasts.R

private const val PLAYBACK_GROUP_NAME = "Playback"
private const val PLAYBACK_CHANNEL_NAME = "Playback"

interface HasNotificationAction {

    enum class NotificationGroup(val groupName: String) {
        Playback(groupName = PLAYBACK_GROUP_NAME)
    }

    enum class NotificationType(
        val channelName: String,
        val importance: Int
    ) {
        Playback(channelName = PLAYBACK_CHANNEL_NAME, importance = NotificationManager.IMPORTANCE_LOW);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels(manager: NotificationManager) = manager.createNotificationChannels(
        NotificationType.values().map { NotificationChannel(it.channelName, it.channelName, it.importance) }
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createGroups(manager: NotificationManager) = manager.createNotificationChannelGroups(
        NotificationGroup.values().map { NotificationChannelGroup(it.groupName, it.groupName) }
    )

    fun getNotificationBuilder(context: Context, notificationType: NotificationType? = null): NotificationCompat.Builder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationType == null) {
                throw IllegalStateException("Notification type is required for Oreo or newer version")
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.let {
                createGroups(it)
                createChannels(it)
            }
            return NotificationCompat.Builder(context, notificationType.channelName)
        } else {
            @Suppress("deprecation")
            NotificationCompat.Builder(context)
        }
    }

    fun NotificationCompat.Builder.addActionsForPlayback(context: Context, state: Int?) = apply {
        // rewind
        addAction(
            NotificationCompat.Action.Builder(NotificationCompat.Action(
                R.drawable.exo_icon_rewind,
                "rewind",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_REWIND)
            )).build()
        )

        // play/pause
        addAction(if (state == STATE_PLAYING) {
            NotificationCompat.Action(
                R.drawable.exo_icon_pause,
                "pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.exo_icon_play,
                "play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PLAY)
            )
        })

        // fast-forward
        addAction(
            NotificationCompat.Action.Builder(NotificationCompat.Action(
                R.drawable.exo_icon_fastforward,
                "fast forward",
                MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_FAST_FORWARD)
            )).build()
        )
    }
}
