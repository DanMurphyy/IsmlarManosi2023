package com.hfad.ismlarmanosi2023.utils.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val notificationService = NotificationService(context)
        notificationService.showNotification()
        notificationService.scheduleDailyNotification(context)
    }
}

