package com.hfad.ismlarmanosi2023.utils.notification

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        applicationContext.sendBroadcast(intent)

        return Result.success()
    }
}