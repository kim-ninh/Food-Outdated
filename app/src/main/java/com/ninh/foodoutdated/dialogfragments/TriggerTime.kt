package com.ninh.foodoutdated.dialogfragments

import androidx.annotation.StringRes
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.extensions.CalendarUtils
import com.ninh.foodoutdated.extensions.isTimeEquals
import java.util.*

enum class TriggerTime(val value: Calendar) {
    MORNING(TimeConst.TIME_MORNING),
    AFTERNOON(TimeConst.TIME_AFTERNOON),
    EVENING(TimeConst.TIME_EVENING),
    NIGHT(TimeConst.TIME_NIGHT),
    PICK_A_TIME(Calendar.getInstance());

    @StringRes
    fun toStringRes(): Int {
        return when (this) {
            MORNING -> R.string.morning
            AFTERNOON -> R.string.afternoon
            EVENING -> R.string.evening
            NIGHT -> R.string.night
            PICK_A_TIME -> R.string.pick_a_time
        }
    }

    companion object {
        val constValues = values()

        fun fromTriggerTimeValue(timeInMillis: Long): TriggerTime {
            val triggerTimes = constValues
            val someTime = CalendarUtils.getCalendarFrom(timeInMillis)
            return triggerTimes.find {
                it.value.isTimeEquals(someTime)
            }
                ?: PICK_A_TIME.apply {
                    this.value.timeInMillis = timeInMillis
                }
        }
    }
}
