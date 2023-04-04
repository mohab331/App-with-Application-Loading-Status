package com.udacity

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.app.NotificationManager


    private const val NOTIFICATION_ID = 0


    fun  NotificationManager.sendNotification(
        channelId: String,
        messageContent: String,
        applicationContext: Context,
        fileName: String,
        status: String
        ) {


        val detailIntent = Intent(applicationContext, DetailActivity::class.java)
            .putExtra("status", status)
            .putExtra("fileName", fileName)

        val detailPendingIntent= PendingIntent.getActivity(applicationContext, NOTIFICATION_ID ,detailIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)



        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageContent)
            .setContentIntent(detailPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                applicationContext.getString(R.string.notification_button),
                detailPendingIntent)


        notify(NOTIFICATION_ID, notificationBuilder.build())
    }

