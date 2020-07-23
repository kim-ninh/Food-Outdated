package com.ninh.foodoutdated.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "remind_info")
data class RemindInfo(
    @PrimaryKey(autoGenerate = true) var requestCode: Int? = null,
    var triggerDate: Calendar = Calendar.getInstance(),
    var repeating: RepeatingType = RepeatingType.NO_REPEAT,
    var productId: Long? = null
) {
}