package com.ninh.foodoutdated.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "remind_info")
data class RemindInfo(
    var triggerDate: Calendar = Calendar.getInstance(),
    var repeating: RepeatingType = RepeatingType.NO_REPEAT,
    var isValid: Boolean = true,
    @PrimaryKey var productId: Int = 0
) {
    val requestCode: Int
        get() = productId
}