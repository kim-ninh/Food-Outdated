package com.ninh.foodoutdated

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.orhanobut.logger.Logger

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val productId = intent.getIntExtra(KEY_PRODUCT_ID, -1)
        Logger.i("Alarm triggered with product: $productId")
    }

    companion object {
        fun newIntent(context: Context, productId: Int) =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra(KEY_PRODUCT_ID, productId)
            }

        private const val KEY_PRODUCT_ID = "PRODUCT_ID"
    }
}