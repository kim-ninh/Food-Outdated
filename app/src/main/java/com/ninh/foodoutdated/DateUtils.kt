package com.ninh.foodoutdated

import java.util.*

object DateUtils {
    fun subtract(d2: Date?, d1: Date?): Int {
        val c1: Calendar = Calendar.getInstance()
        val c2: Calendar = Calendar.getInstance()
        c1.time = d1
        c2.time = d2
        return subtract(c2, c1)
    }

    fun subtract(c2: Calendar, c1: Calendar): Int {
        var d = 0
        val tmpC1: Calendar
        val tmpC2: Calendar
        if (c2.after(c1)) {
            tmpC1 = c1.clone() as Calendar
            tmpC2 = c2.clone() as Calendar
        } else {
            tmpC2 = c1.clone() as Calendar
            tmpC1 = c2.clone() as Calendar
        }
        val nLeapYear = countLeapYear(tmpC1[Calendar.YEAR], tmpC2[Calendar.YEAR])
        val nYear = tmpC2[Calendar.YEAR] - tmpC1[Calendar.YEAR]
        d = tmpC2[Calendar.DAY_OF_YEAR] - tmpC1[Calendar.DAY_OF_YEAR]
        d += 366 * nLeapYear + 365 * (nYear - nLeapYear)
        if (c1.after(c2)) {
            d *= -1
        }
        return d
    }

    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0
                || year % 400 == 0)
    }

    private fun countLeapYear(fromYear: Int, toYear: Int): Int {
        var count = 0
        for (yyyy in fromYear until toYear) {
            if (isLeapYear(yyyy)) count++
        }
        return count
    }
}