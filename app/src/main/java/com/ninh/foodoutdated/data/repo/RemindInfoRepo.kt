package com.ninh.foodoutdated.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ninh.foodoutdated.data.dao.RemindInfoDao
import com.ninh.foodoutdated.data.models.RemindInfo
import java.util.concurrent.ExecutorService

class RemindInfoRepo(
    private val executor: ExecutorService,
    private val remindInfoDao: RemindInfoDao
) {
    val allRemindInfo: LiveData<List<RemindInfo>> = remindInfoDao.loadAll()

    fun load(productId: Int)
            = remindInfoDao.load(productId)
}