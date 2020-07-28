package com.ninh.foodoutdated.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "remind_info")
data class RemindInfo(
    var triggerDate: Calendar = Calendar.getInstance(),
    var repeating: RepeatingType = RepeatingType.NO_REPEAT,
    var isValid: Boolean = true,
    @PrimaryKey var productId: Int = 0
): Parcelable {
    val requestCode: Int
        get() = productId
}