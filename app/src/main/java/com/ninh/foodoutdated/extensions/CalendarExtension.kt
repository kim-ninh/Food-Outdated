package com.ninh.foodoutdated.extensions

import java.util.*

val Calendar.year
    get() = this.get(Calendar.YEAR)

val Calendar.month
    get() = this.get(Calendar.MONTH)

val Calendar.day
    get() = this.get(Calendar.DAY_OF_MONTH)

fun Calendar.isDateEquals(calendar: Calendar): Boolean{
    return year == calendar.year && month == calendar.month && day == calendar.day
}

object CalendarExtension {
    fun getCalendarInstanceFrom(year: Int, month: Int, dayOfMonth: Int): Calendar {
        return Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }
    }

    fun getCalendarInstanceFrom(date: Date?): Calendar{
        val calendar = Calendar.getInstance()

        if (date != null) {
            calendar.time = date
        }

        return calendar
    }
}