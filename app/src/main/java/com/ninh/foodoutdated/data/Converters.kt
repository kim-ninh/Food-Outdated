package com.ninh.foodoutdated.data

import android.net.Uri
import androidx.room.TypeConverter
import com.ninh.foodoutdated.data.models.RepeatingType
import java.io.File
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(string: String?): Uri? {
        return string?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun fileToString(file: File?): String? {
        return file?.absolutePath
    }

    @TypeConverter
    fun fromStringToFile(string: String?): File? {
        return string?.let { File(it) }
    }

    @TypeConverter
    fun fromTimeInMilisec(value: Long?): Calendar? {
        return value?.let {
            Calendar.getInstance().apply {
                timeInMillis = value
            }
        }
    }

    @TypeConverter
    fun calendarToLong(calendar: Calendar?): Long?{
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun repeatingTypeToInt(repeatingType: RepeatingType): Int{
        return repeatingType.ordinal
    }

    @TypeConverter
    fun fromInt(repeatingTypeId: Int): RepeatingType{
        return enumValues<RepeatingType>()[repeatingTypeId]
    }
}