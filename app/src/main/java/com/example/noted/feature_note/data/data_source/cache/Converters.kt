package com.example.noted.feature_note.data.data_source.cache

import androidx.room.TypeConverter
import java.util.*

object Converters {
        @TypeConverter
        fun timeStampToDate(timeStamp: Long): Date{
            return Date(timeStamp)
        }

        @TypeConverter
        fun dateToTimeStamp(date: Date): Long{
            return date.time
        }

}