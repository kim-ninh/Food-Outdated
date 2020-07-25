package com.ninh.foodoutdated.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.util.*

@Entity(tableName = "products")
data class Product(
    var name: String = "",
    var quantity: Int = 1,
    var expiry: Calendar = Calendar.getInstance(),
    var thumb: File? = null,
    var isValid: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) {

    val state: ExpiryState
        get() {
            var newState = ExpiryState.NEW
            val now = Calendar.getInstance()
            val warningDate = (expiry.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, -remainDayUtilWarn)
            }

            if (now.after(warningDate) && now.before(expiry)) {
                newState = ExpiryState.NEARLY_EXPIRY
            } else if (now.after(expiry)) {
                newState = ExpiryState.EXPIRED
            }
            return newState
        }

    companion object {
        private const val remainDayUtilWarn = 15
    }
}