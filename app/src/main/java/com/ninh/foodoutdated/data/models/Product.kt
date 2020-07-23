package com.ninh.foodoutdated.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.File
import java.util.*

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String?,
    var expiry: Date?,
    var file: File?
) {

    @delegate:Ignore
    val state by lazy {
        var newState = ExpiryState.NEW
        val now = Calendar.getInstance()
        val expiryDate = Calendar.getInstance().apply { time = expiry!! }
        val warningDate = (expiryDate.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, -remainDayUtilWarn)
        }

        if (now.after(warningDate) && now.before(expiryDate)){
            newState = ExpiryState.NEARLY_EXPIRY
        }else if (now.after(expiryDate)){
            newState = ExpiryState.EXPIRED
        }
        newState
    }

    companion object {
        private const val remainDayUtilWarn = 15
    }
}