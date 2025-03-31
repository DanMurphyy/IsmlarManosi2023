package com.hfad.ismlarmanosi2023.utils.notification

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hfad.ismlarmanosi2023.fragments.quotes.QuoteViewModel
import com.hfad.ismlarmanosi2023.MainActivity
import com.hfad.ismlarmanosi2023.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationService(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var mQuoteViewModel: QuoteViewModel =
        QuoteViewModel(application = context.applicationContext as Application)

    init {
        createNotificationChannel()
    }

    fun showNotification() {

        val quoteData = mQuoteViewModel.quoteData.value?.shuffled()?.firstOrNull()
        val quote = quoteData?.quote
        val author = quoteData?.author
        val position = mQuoteViewModel.quoteData.value?.indexOf(quoteData) ?: -1

        if (!author.isNullOrEmpty() && !quote.isNullOrEmpty()) {

            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("screen", "quotes")
                putExtra("quote_position", position)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val bigTextStyle = NotificationCompat.BigTextStyle()
                .bigText(quote)

            val notification = NotificationCompat.Builder(context, DAILY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(author)
                .setContentText(quote)
                .setStyle(bigTextStyle)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(1, notification)
        }
    }

    fun scheduleDailyNotification(context: Context) {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 11)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = calendar.timeInMillis - System.currentTimeMillis()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("daily_notification")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DAILY_CHANNEL_ID,
                "Daily Quotes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for daily quotes notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val DAILY_CHANNEL_ID = "1"
    }
}