package com.ninh.foodoutdated

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.ninh.foodoutdated.data.ProductDatabase
import com.ninh.foodoutdated.data.repo.RemindInfoRepo
import java.util.concurrent.Executors

class BootCompleteReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED){

            val pendingResult: PendingResult = goAsync()
            val executorService = Executors.newSingleThreadExecutor()
            val remindInfoDao = ProductDatabase.getDatabase(context.applicationContext).remindInfoDao()
            val remindInfoRepo = RemindInfoRepo(remindInfoDao)
            val uiHandler = Handler(context.mainLooper)

            executorService.submit {
                val remindInfoList = remindInfoRepo.all
                uiHandler.post {
                    remindInfoList.forEach{
                        AlarmUtils.add(context, it)
                    }
                    pendingResult.finish()
                }
            }
        }
    }
}