package com.ninh.foodoutdated.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.File
import java.util.*
import kotlin.math.exp

@Entity(tableName = "products")
data class Product(
    var name: String = "",
    var quantity: Int = 1,
    var expiryDate: Calendar = Calendar.getInstance(),
    var file: File? = null,
    var isValid: Boolean = true,
    @PrimaryKey(autoGenerate = true) val id: Long? = null
) {

    val state: ExpiryState
        get() {
            var newState = ExpiryState.NEW
            val now = Calendar.getInstance()
            val warningDate = (expiryDate.clone() as Calendar).apply {
                add(Calendar.DAY_OF_MONTH, -remainDayUtilWarn)
            }

            if (now.after(warningDate) && now.before(expiryDate)) {
                newState = ExpiryState.NEARLY_EXPIRY
            } else if (now.after(expiryDate)) {
                newState = ExpiryState.EXPIRED
            }
            return newState
        }

    companion object {
        private const val remainDayUtilWarn = 15
    }
}