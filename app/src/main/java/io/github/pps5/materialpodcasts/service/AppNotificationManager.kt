package io.github.pps5.materialpodcasts.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import io.github.pps5.materialpodcasts.R
import io.github.pps5.materialpodcasts.view.MainActivity

class AppNotificationManager(private val mediaService: MediaService) {

    companion object {
        const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE_CREATE = 1
        private const val REQUEST_CODE_DELETE = 0
        private val TAG = AppNotificationManager::class.java.simpleName
        private const val NAME_PLAYBACK = "Playback"
    }

    private val notificationManager =
        mediaService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val actionPlay = NotificationCompat.Action(
        R.drawable.exo_icon_play,
        "play",
        MediaButtonReceiver.buildMediaButtonPendingIntent(mediaService, PlaybackStateCompat.ACTION_PLAY)
    )

    private val actionPause = NotificationCompat.Action(
        R.drawable.exo_icon_pause,
        "pause",
        MediaButtonReceiver.buildMediaButtonPendingIntent(mediaService, PlaybackStateCompat.ACTION_PAUSE)
    )

    private val actionRewind = NotificationCompat.Action.Builder(NotificationCompat.Action(
        R.drawable.exo_icon_rewind,
        "rewind",
        MediaButtonReceiver.buildMediaButtonPendingIntent(mediaService, PlaybackStateCompat.ACTION_REWIND)
    )).build()

    private val actionFastForward = NotificationCompat.Action.Builder(NotificationCompat.Action(
        R.drawable.exo_icon_fastforward,
        "fast forward",
        MediaButtonReceiver.buildMediaButtonPendingIntent(mediaService, PlaybackStateCompat.ACTION_FAST_FORWARD)
    )).build()

    fun createMediaNotification(
        description: MediaDescriptionCompat,
        isPlaying: Boolean
    ): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
        return buildNotification(description, isPlaying)
    }

    fun notify(notification: Notification) =
        notificationManager.notify(NOTIFICATION_ID, notification)

    private fun buildNotification(
        description: MediaDescriptionCompat,
        isPlaying: Boolean
    ): Notification {
        val builder =
            NotificationCompat.Builder(mediaService, NotificationType.Playback.channelName)
                .setContentTitle(description.title)
                .setContentText(description.subtitle)
                .setSubText(description.description)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_share_black_24dp)  // todo: replace
                .setLargeIcon(description.iconBitmap)
                .setContentIntent(createContentIntent())
                .setDeleteIntent(createDeleteIntent())
                .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaService.sessionToken)
                    .setShowActionsInCompactView(1))
        builder.addAction(actionRewind)
        builder.addAction(if (isPlaying) actionPause else actionPlay)
        builder.addAction(actionFastForward)
        return builder.build()
    }

    private fun createContentIntent(): PendingIntent {
        val i = Intent(mediaService, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent
            .getActivity(mediaService, REQUEST_CODE_CREATE, i, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun createDeleteIntent() = PendingIntent.getBroadcast(
        mediaService, REQUEST_CODE_DELETE, Intent(NotificationDeleteReceiver.DELETE_NOTIFICATION), 0)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        Log.d(TAG, "Create channels: ${NotificationType.values()}")
        val channels = NotificationType.values()
            .map { NotificationChannel(it.channelName, it.channelName, it.importance) }
        notificationManager.createNotificationChannels(channels)
    }

    enum class NotificationType(
        val channelName: String,
        val importance: Int
    ) {
        Playback(channelName = NAME_PLAYBACK, importance = NotificationManagerCompat.IMPORTANCE_LOW)
    }

}