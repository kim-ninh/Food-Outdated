package com.ninh.foodoutdated.data

import androidx.room.TypeConverter
import com.ninh.foodoutdated.data.models.RepeatingType
import java.io.File
import java.util.*

class Converters {

    @TypeConverter
    fun fileToString(file: File?): String? {
        return file?.absolutePath
    }

    @TypeConverter
    fun fromStringToFile(string: String?): File? {
        return string?.let { File(it) }
    }

    @TypeConverter
    fun fromTimeInMilisec(value: Long): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = value
        }
    }

    @TypeConverter
    fun calendarToLong(calendar: Calendar): Long {
        return calendar.timeInMillis
    }

    @TypeConverter
    fun repeatingTypeToInt(repeatingType: RepeatingType): Int {
        return repeatingType.ordinal
    }

    @TypeConverter
    fun fromInt(repeatingTypeId: Int): RepeatingType {
        return enumValues<RepeatingType>()[repeatingTypeId]
    }
}