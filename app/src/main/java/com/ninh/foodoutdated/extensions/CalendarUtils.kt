package com.ninh.foodoutdated.extensions

import java.util.*

val Calendar.year
    get() = this.get(Calendar.YEAR)

val Calendar.month
    get() = this.get(Calendar.MONTH)

val Calendar.day
    get() = this.get(Calendar.DAY_OF_MONTH)

val Calendar.hour
    get() = this.get(Calendar.HOUR_OF_DAY)

val Calendar.minute
    get() = this.get(Calendar.MINUTE)

fun Calendar.isDateEquals(calendar: Calendar): Boolean{
    return year == calendar.year && month == calendar.month && day == calendar.day
}

fun Calendar.isTimeEquals(calendar: Calendar): Boolean{
    return hour == calendar.hour && minute == calendar.minute
}

object CalendarUtils {
    fun getCalendarFrom(year: Int, month: Int, dayOfMonth: Int): Calendar {
        return Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
    }

    fun getCalendarFrom(hour: Int, minute: Int): Calendar =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

    fun getCalendarFrom(timeInMillis: Long): Calendar =
        Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
        }

    fun getCalendarFrom(date: Date?): Calendar{
        val calendar = Calendar.getInstance()

        if (date != null) {
            calendar.time = date
        }

        return calendar
    }
}