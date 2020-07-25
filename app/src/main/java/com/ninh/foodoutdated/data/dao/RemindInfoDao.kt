package com.ninh.foodoutdated.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninh.foodoutdated.data.models.RemindInfo

@Dao
interface RemindInfoDao {

    @Query("SELECT * FROM remind_info WHERE isValid = 1")
    fun loadAllRemindInfo(): LiveData<List<RemindInfo>>

    @Query("SELECT * FROM remind_info WHERE productOwnerId = :id AND isValid = 1 LIMIT 1")
    fun loadByProductOwnerId(id: Long): LiveData<RemindInfo>

    @Query("SELECT * FROM remind_info WHERE requestCode = :code AND isValid = 1 LIMIT 1")
    fun loadByRequestCode(code: Int): LiveData<RemindInfo>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertRemindInfo(remindInfo: RemindInfo): Long

    @Update
    fun updateRemindInfo(remindInfo: RemindInfo)

    @Query("UPDATE remind_info set isValid = 0 WHERE requestCode = :code")
    fun deleteRemindInfoByRequestCode(code: Int)
}