package com.ninh.foodoutdated.data.repo

import androidx.lifecycle.LiveData
import com.ninh.foodoutdated.data.dao.RemindInfoDao
import com.ninh.foodoutdated.data.models.RemindInfo
import java.util.concurrent.ExecutorService

class RemindInfoRepo(
    private val remindInfoDao: RemindInfoDao
) {
    val allAsync: LiveData<List<RemindInfo>> = remindInfoDao.loadAllAsync()

    val all: List<RemindInfo> = remindInfoDao.loadAll()

    fun load(productId: Int)
            = remindInfoDao.load(productId)
}