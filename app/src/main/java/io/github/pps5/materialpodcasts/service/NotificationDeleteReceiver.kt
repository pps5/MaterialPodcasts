package io.github.pps5.materialpodcasts.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationDeleteReceiver : BroadcastReceiver() {

    companion object {
        const val DELETE_NOTIFICATION = "delete_notification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.stopService(Intent(context, MediaService::class.java))
    }

}