package com.ninh.foodoutdated.data.models

import androidx.annotation.StringRes
import com.ninh.foodoutdated.R

enum class RepeatingType {
    NO_REPEAT, DAILY, WEEKLY, MONTHLY;

    val toTimeDurationInMillis: Long
        get() = when (this) {
            NO_REPEAT -> 0
            DAILY -> 1000 * 60 * 60 * 24
            WEEKLY -> 1000 * 60 * 60 * 24 * 7
            MONTHLY -> 1000 * 60 * 60 * 24 * 31L
        }

    @StringRes
    fun toStringRes(): Int{
        return when(this){
            NO_REPEAT -> R.string.does_not_repeat
            DAILY -> R.string.daily
            WEEKLY -> R.string.weekly
            MONTHLY -> R.string.monthly
        }
    }
}