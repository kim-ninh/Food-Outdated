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
    private val _newRequestCodeObserver = MutableLiveData<Int>()

    val allRemindInfo: LiveData<List<RemindInfo>> = remindInfoDao.loadAllRemindInfo()

    val newRequestCodeObserver: LiveData<Int>
        get() = _newRequestCodeObserver

    fun loadByRequestCode(code: Int): LiveData<RemindInfo>{
        return remindInfoDao.loadByRequestCode(code)
    }

    fun loadByProductOwnerId(id: Long): LiveData<RemindInfo>{
        return remindInfoDao.loadByProductOwnerId(id)
    }

    fun insertRemindInfo(remindInfo: RemindInfo){
        executor.submit {
            val requestCode: Int = remindInfoDao.insertRemindInfo(remindInfo).toInt()
            _newRequestCodeObserver.postValue(requestCode)
        }
    }

    fun updateRemindInfo(remindInfo: RemindInfo){
        executor.submit {
            remindInfoDao.updateRemindInfo(remindInfo)
        }
    }

    fun deleteRemindInfo(requestCode: Int){
        executor.submit {
            remindInfoDao.deleteRemindInfoByRequestCode(requestCode)
        }
    }
}