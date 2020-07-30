package com.ninh.foodoutdated.dialogfragments

import androidx.annotation.StringRes
import com.ninh.foodoutdated.R
import com.ninh.foodoutdated.extensions.isDateEquals
import java.util.*

enum class TriggerDate(
    private val calendarField: Int,
    private val amount: Int
) {
    A_WEEK_BEFORE(Calendar.DAY_OF_MONTH, -7),
    FIFTEEN_DATE_BEFORE(Calendar.DAY_OF_MONTH, -15),
    A_MONTH_BEFORE(Calendar.MONTH, -1),
    THREE_MONTH_BEFORE(Calendar.MONTH, -3),
    PICK_A_DATE(Calendar.DAY_OF_MONTH, 0);

    private val _triggerDate: Calendar = Calendar.getInstance()
    var value: Calendar = Calendar.getInstance()
        set(value) {
            field = value
            _triggerDate.timeInMillis = value.timeInMillis
        }
        get() {
            field.timeInMillis = _triggerDate.timeInMillis
            return field
        }

    fun getValueFromExpiry(expiry: Calendar): Calendar {
        if (this == PICK_A_DATE) {
            return _triggerDate
        }

        _triggerDate.timeInMillis = expiry.timeInMillis
        _triggerDate.add(calendarField, amount)
        return _triggerDate
    }

    @StringRes
    fun toStringRes(): Int {
        return when (this) {
            A_WEEK_BEFORE -> R.string.a_week_before
            FIFTEEN_DATE_BEFORE -> R.string.fifteen_date_before
            A_MONTH_BEFORE -> R.string.a_month_before
            THREE_MONTH_BEFORE -> R.string.three_month_before
            PICK_A_DATE -> R.string.pick_a_date
        }
    }

    companion object {
        fun fromExpiryAndTriggerDateValue(
            expiry: Calendar,
            triggerDateValue: Calendar
        ): TriggerDate {

            val triggerDates = values()

            return triggerDates.find {
                it.getValueFromExpiry(expiry).isDateEquals(triggerDateValue)
            }
                ?: PICK_A_DATE.apply {
                    value = triggerDateValue
                }
        }

        fun fromStringRes(@StringRes resId: Int): TriggerDate {
            return when (resId) {
                R.string.a_week_before -> A_WEEK_BEFORE
                R.string.fifteen_date_before -> FIFTEEN_DATE_BEFORE
                R.string.a_month_before -> A_MONTH_BEFORE
                R.string.three_month_before -> THREE_MONTH_BEFORE
                else -> throw IllegalArgumentException("String resource not valid")
            }
        }
    }
}

fun main() {
    val values1 = TriggerDate.values()

    values1[0] = TriggerDate.PICK_A_DATE
    values1.forEach {
        println(it)
    }
}