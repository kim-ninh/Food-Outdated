package com.ninh.foodoutdated.dialogfragments

import com.ninh.foodoutdated.extensions.CalendarUtils

object TimeConst {
    val TIME_MORNING = CalendarUtils.getCalendarFrom(8, 0)
    val TIME_AFTERNOON = CalendarUtils.getCalendarFrom(13, 0)
    val TIME_EVENING = CalendarUtils.getCalendarFrom(18, 0)
    val TIME_NIGHT = CalendarUtils.getCalendarFrom(20, 0)
}