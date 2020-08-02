package com.ninh.foodoutdated

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.ninh.foodoutdated.data.models.Product
import com.ninh.foodoutdated.extensions.CalendarUtils
import com.orhanobut.logger.Logger

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val product = getProductFromBundle(intent.extras)
        createNotificationAndNotify(context, product)
        Logger.i("Alarm triggered with product: ${product.id}")
    }

    private fun getProductFromBundle(bundle: Bundle?): Product{
        if (bundle == null)
            return Product()

        val productId = bundle.getInt(KEY_PRODUCT_ID)
        val productName = bundle.getString(KEY_PRODUCT_NAME)!!
        val productExpiry = bundle.getLong(KEY_PRODUCT_EXPIRY).let {
            CalendarUtils.getCalendarFrom(it)
        }

        return Product(productName, expiry = productExpiry, id = productId)
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

        fun newIntent(context: Context, product: Product) =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(KEY_PRODUCT_ID, product.id)
                putExtra(KEY_PRODUCT_NAME, product.name)
                putExtra(KEY_PRODUCT_EXPIRY, product.expiry.timeInMillis)
            }

        private const val KEY_PRODUCT_ID = "PRODUCT_ID"
        private const val KEY_PRODUCT_NAME = "PRODUCT_NAME"
        private const val KEY_PRODUCT_EXPIRY = "PRODUCT_EXPIRY"
    }
}