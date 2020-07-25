package com.ninh.foodoutdated.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "remind_info")
data class RemindInfo(
    @PrimaryKey(autoGenerate = true) val requestCode: Int = 0,
    var triggerDate: Calendar = Calendar.getInstance(),
    var repeating: RepeatingType = RepeatingType.NO_REPEAT,
    var productOwnerId: Long? = null,
    var isValid: Boolean = true
) {
}