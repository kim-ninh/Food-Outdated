package com.ninh.foodoutdated.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ninh.foodoutdated.FoodOutdatedApplication
import com.ninh.foodoutdated.data.ProductDatabase
import com.ninh.foodoutdated.data.models.RemindInfo
import com.ninh.foodoutdated.data.repo.RemindInfoRepo

class RemindInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RemindInfoRepo

    init {
        val remindInfoDao = ProductDatabase.getDatabase(application.applicationContext)
            .remindInfoDao()
        repository = RemindInfoRepo(remindInfoDao)
    }

    val allRemindInfo: LiveData<List<RemindInfo>> = repository.allAsync

    fun load(productId: Int) = repository.load(productId)
}