package com.ninh.foodoutdated.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ninh.foodoutdated.DateUtils
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
        val elapsedDate = DateUtils.subtract(expiry, now.time)
        if (elapsedDate in 1..remainDayUtilWarn) {
            newState = ExpiryState.NEARLY_EXPIRY
        } else if (elapsedDate <= 0) {
            newState = ExpiryState.EXPIRED
        }
        newState
    }

    companion object {
        private const val remainDayUtilWarn = 15
    }
}