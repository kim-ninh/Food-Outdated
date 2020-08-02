package com.ninh.foodoutdated

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MyApplication : Application() {

    val workerExecutor: ExecutorService by lazy {
        val executorService = Executors.newSingleThreadExecutor()
        executorService
    }

    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(AndroidLogAdapter())
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                AlarmReceiver.NOTIFICATION_CHANNEL_ID, name, importance
            ).apply {
                description = descriptionText
            }

            val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            )!!
            notificationManager.createNotificationChannel(channel)
        }
    }
}