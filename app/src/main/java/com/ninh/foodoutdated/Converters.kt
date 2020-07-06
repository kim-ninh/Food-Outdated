package com.ninh.foodoutdated

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long?{
        return date?.time
    }

    @TypeConverter
    fun fromString(string: String?): Uri?{
        return string?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String?{
        return uri?.toString()
    }
}