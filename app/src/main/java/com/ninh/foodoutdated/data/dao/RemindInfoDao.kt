package com.ninh.foodoutdated.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ninh.foodoutdated.data.models.RemindInfo

@Dao
interface RemindInfoDao {

    @Query("SELECT * FROM remind_info WHERE isValid = 1")
    fun loadAllAsync(): LiveData<List<RemindInfo>>

    @Query("SELECT * FROM remind_info WHERE isValid = 1")
    fun loadAll(): List<RemindInfo>

    @Query("SELECT * FROM remind_info WHERE productId = :id AND isValid = 1 LIMIT 1")
    fun load(id: Int): LiveData<RemindInfo>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(remindInfo: RemindInfo)

    @Update
    fun update(remindInfo: RemindInfo)

    @Query("UPDATE remind_info set isValid = 0 WHERE productId = :id")
    fun delete(id: Int)
}