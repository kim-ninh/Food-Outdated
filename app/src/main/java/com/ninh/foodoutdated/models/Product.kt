package com.ninh.foodoutdated.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ninh.foodoutdated.DateUtils
import com.ninh.foodoutdated.ExpiryState
import java.util.*

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String?,
    var expiry: Date?,
    var uri: Uri?
) {

    @delegate:Ignore
    val state by lazy {
        var newState = ExpiryState.NEW
        val now = Calendar.getInstance()
        val elapsedDate = DateUtils.substract(expiry, now.time)
        if (0 < elapsedDate && elapsedDate <= remainDayUtilWarn) {
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