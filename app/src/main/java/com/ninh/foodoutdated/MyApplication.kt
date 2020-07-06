package com.ninh.foodoutdated

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MyApplication: Application() {

    val workerExecutor: ExecutorService by lazy {
        val executorService = Executors.newSingleThreadExecutor()
        executorService
    }

    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(AndroidLogAdapter())
    }
}