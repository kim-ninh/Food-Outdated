package com.ninh.foodoutdated

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.ninh.foodoutdated.data.ProductDatabase
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.data.repo.ProductRepo
import com.ninh.foodoutdated.extensions.CalendarUtils
import com.orhanobut.logger.Logger
import java.util.concurrent.Executors

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val productId = intent.getIntExtra(KEY_PRODUCT_ID, -1)
        Logger.i("Alarm triggered with product: $productId")
        if (productId == -1){
            return
        }

        val pendingResult: PendingResult = goAsync()
        val executorService = Executors.newSingleThreadExecutor()
        val productDao = ProductDatabase.getDatabase(context.applicationContext).productDao()
        val productRepo = ProductRepo(executorService, productDao)
        val uiHandler = Handler(context.mainLooper)

        executorService.submit {
            val productAndRemindInfo = productRepo.load(productId)
            uiHandler.post {
                createNotificationAndNotify(context, productAndRemindInfo.product)
                pendingResult.finish()
            }
        }
    }

    private fun createNotificationAndNotify(context: Context, product: Product){
        val intent = MainActivity.newIntent(context, product.id)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(context)
            .run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("Expiry reminder")
            .setContentText("${product.name} is gonna expiry soon. Remember to use it.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)){
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "484"
        const val NOTIFICATION_ID = 484

        fun newIntent(context: Context, productId: Int) =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(KEY_PRODUCT_ID, productId)
            }

        private const val KEY_PRODUCT_ID = "PRODUCT_ID"
    }
}