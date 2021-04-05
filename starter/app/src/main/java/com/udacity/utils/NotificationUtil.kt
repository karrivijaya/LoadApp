package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

val NOTIFICATION_ID = 0
private val FLAGS = 0
val URL_EXTRA = "url"
val DOWNLOAD_STATUS = "status"

fun NotificationManager.sendNotification(messageBody:String, applicationContext: Context, url: String, status: String){

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(URL_EXTRA, url)
    contentIntent.putExtra(DOWNLOAD_STATUS, status)

    val contentPendingIntent = PendingIntent.getActivity(
                                applicationContext,
                                NOTIFICATION_ID,
                                contentIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(
                        applicationContext,
                        applicationContext.getString(R.string.loadapp_notification_channel_id))
                    .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                    .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .addAction(R.drawable.ic_assistant_black_24dp,
                    applicationContext.getString(R.string.notification_check_status),
                    contentPendingIntent)
            .setAutoCancel(true)


    notify(NOTIFICATION_ID, builder.build())

    fun NotificationManager.cancelNotification(){
        cancelAll()
    }

}