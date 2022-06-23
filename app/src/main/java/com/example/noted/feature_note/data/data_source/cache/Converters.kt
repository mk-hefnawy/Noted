package com.example.noted.feature_note.data.data_source.cache

import androidx.room.TypeConverter
import com.example.noted.feature_note.presentation.model.AttachedImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object Converters {
    @TypeConverter
    fun timeStampToDate(timeStamp: Long): Date {
        return Date(timeStamp)
    }

    @TypeConverter
    fun dateToTimeStamp(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun listOfStringsToString(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun stringToListOfStrings(string: String): List<String> {
        val listType = object : TypeToken<List<String>>(){}.type
        return Gson().fromJson(string, listType)
    }

    @TypeConverter
    fun attachedImagesListToString(attachedImages: List<AttachedImage>): String {
        return Gson().toJson(attachedImages)
    }

    @TypeConverter
    fun stringToAttachedImagesList(string: String): List<AttachedImage> {
        val type = object : TypeToken<List<AttachedImage>>(){}.type
        return Gson().fromJson(string, type)
    }



}