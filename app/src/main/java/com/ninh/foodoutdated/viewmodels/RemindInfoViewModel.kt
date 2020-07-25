package com.ninh.foodoutdated.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ninh.foodoutdated.MyApplication
import com.ninh.foodoutdated.data.ProductDatabase
import com.ninh.foodoutdated.data.models.RemindInfo
import com.ninh.foodoutdated.data.repo.RemindInfoRepo

class RemindInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RemindInfoRepo

    init {
        val executor = (application as MyApplication).workerExecutor
        val remindInfoDao = ProductDatabase.getDatabase(application.applicationContext)
            .remindInfoDao()
        repository = RemindInfoRepo(executor, remindInfoDao)
    }

    val allRemindInfo: LiveData<List<RemindInfo>> = repository.allRemindInfo

    fun insert(remindInfo: RemindInfo){
        repository.insertRemindInfo(remindInfo)
    }

    fun deleteByRequestCode(code: Int){
        repository.deleteRemindInfo(code)
    }

    fun loadByRequestCode(code: Int) = repository.loadByRequestCode(code)

    fun loadByProductOwnerId(id: Long) = repository.loadByProductOwnerId(id)

    fun update(remindInfo: RemindInfo){
        repository.updateRemindInfo(remindInfo)
    }
}