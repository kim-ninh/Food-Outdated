package com.ninh.foodoutdated

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import com.ninh.foodoutdated.data.models.ProductAndRemindInfo
import com.ninh.foodoutdated.data.models.RemindInfo
import com.orhanobut.logger.Logger

object AlarmUtils {
    fun add(context: Context, remindInfo: RemindInfo) {
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)!!
        val intent = AlarmReceiver.newIntent(context, remindInfo.productId)
        val pendingIntent = PendingIntent.getBroadcast(context, remindInfo.requestCode, intent, 0)

        Logger.i("Trigger Date: ${remindInfo.triggerDate.time}")
        if (remindInfo.repeating.toTimeDurationInMillis == 0L) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                remindInfo.triggerDate.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                remindInfo.triggerDate.timeInMillis,
                remindInfo.repeating.toTimeDurationInMillis,
                pendingIntent
            )
        }
    }

    fun delete(context: Context, remindInfo: RemindInfo) {
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)!!
        val intent = AlarmReceiver.newIntent(context, remindInfo.productId)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                remindInfo.requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE
            )

        alarmManager.cancel(pendingIntent)
    }

    fun delete(context: Context, productId: Int){
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)!!
        val intent = AlarmReceiver.newIntent(context, productId)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                productId,
                intent,
                PendingIntent.FLAG_NO_CREATE
            )

        alarmManager.cancel(pendingIntent)
    }

    fun delete(context: Context, productIds: IntArray){
        productIds.forEach {
            delete(context, it)
        }
    }

    fun update(context: Context, remindInfo: RemindInfo) {

        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)!!
        val intent = AlarmReceiver.newIntent(context, remindInfo.productId)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context,
                remindInfo.requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        if (remindInfo.repeating.toTimeDurationInMillis == 0L) {
            alarmManager.set(
                AlarmManager.RTC,
                remindInfo.triggerDate.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                remindInfo.triggerDate.timeInMillis,
                remindInfo.repeating.toTimeDurationInMillis,
                pendingIntent
            )
        }
    }
}